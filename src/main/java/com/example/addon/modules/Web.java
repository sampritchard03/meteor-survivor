package com.example.addon.modules;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.Settings;
import baritone.api.pathing.goals.GoalBlock;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;

import com.example.addon.AddonTemplate;
import com.example.addon.mod;
import com.example.addon.packets.Packet;
import com.example.addon.packets.PositionPacket;
import com.example.addon.packets.SpawnPacket;
import com.example.addon.packets.TickPacket;
import com.example.addon.utils.TimeUtils;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Inventory;

public class Web extends Module implements WebSocket.Listener {

    private WebSocket webSocket = null;
    private int t = 0;

    public boolean isConnected() {
        return webSocket != null && !webSocket.isOutputClosed();
    }

    public void clearConnection() {
        if (webSocket != null) webSocket.abort();
        webSocket = null;
        t = 0;
    }

    public void send(String msg) {
        if (webSocket != null) webSocket.sendText(msg, true);
    }

    public void send(Packet packet) {
        send(packet.toString());
    }

	private final SettingGroup sgGeneral = settings.getDefaultGroup();

	private final Setting<String> addrSetting = sgGeneral.add(new StringSetting.Builder()
		.name("Address")
		.description("The websocket address to connect to.")
		.defaultValue("ws://localhost:8080")
		.build()
	);

	public Web() {
		super(AddonTemplate.WEBCATEGORY, "web", "Connect to websocket server");
	}

    @Override
    public void onOpen(WebSocket _webSocket) {
        mod.log("Connected to WebSocket server");
        webSocket = _webSocket;
        TimeUtils.setTimeout(() -> send(new SpawnPacket()), 500);
        WebSocket.Listener.super.onOpen(webSocket);
    }

    @Override
    public CompletionStage<?> onText(WebSocket _webSocket, CharSequence data, boolean last) {
        mod.log(data.toString());
        webSocket = _webSocket;
        return WebSocket.Listener.super.onText(webSocket, data, last);
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        mod.error("WebSocket connection error");
        clearConnection();
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        mod.log("WebSocket connection closed");
        clearConnection();
        return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
    }

	@Override
	public void onActivate() {
        connect();
		// leave empty for now
	}

	@Override
	public void onDeactivate() {
        // make it close the connection
		clearConnection();
	}

    public void sendPackets() {
        if (mod.bot.vel().length() > 0) 
            send(new PositionPacket());

        send(new TickPacket());
    }

    @EventHandler  
    public void onPlayerTick(TickEvent.Post event) {
        if (!isConnected()) {
            if (t % 20 == 0) connect();
            return;
        }

        sendPackets();
    }

    private void connect() {
        URI endpoint = URI.create(addrSetting.get());
        HttpClient httpClient = HttpClient.newHttpClient();

        if (this.isConnected()) {
            return;
        }

        httpClient.newWebSocketBuilder()
                .buildAsync(endpoint, this)
                .exceptionally(error -> {
                    mod.error("Could not connect to WebSocket server");
                    clearConnection();
                    return null;
                });
    }
}
