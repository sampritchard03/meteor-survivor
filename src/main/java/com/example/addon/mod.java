package com.example.addon;


import org.slf4j.Logger;

import com.example.addon.modules.Bot;
import com.example.addon.modules.Web;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.Settings;
import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.client.Minecraft;

public class mod {
    
    public static final Bot bot = new Bot();
    public static final Web web = new Web();
    public static final Minecraft mc = Minecraft.getInstance();
    public static final IBaritone b = BaritoneAPI.getProvider().getPrimaryBaritone();
    public static final Settings s = BaritoneAPI.getSettings();

    public static void log(String str) {AddonTemplate.LOG.info(str);}
    public static void error(String str) {AddonTemplate.LOG.error(str);}
}
