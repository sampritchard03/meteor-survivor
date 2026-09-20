package com.example.addon;

import com.example.addon.commands.CommandExample;
import com.example.addon.hud.HudExample;
import com.example.addon.item.ItemData;
import com.example.addon.modules.Bot;
import com.example.addon.modules.Web;
import com.mojang.logging.LogUtils;

import baritone.api.BaritoneAPI;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.misc.text.MeteorClickEvent;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.world.item.Items;

import org.slf4j.Logger;

public class AddonTemplate extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category BOTCATEGORY = new Category("Bot");
    public static final Category WEBCATEGORY = new Category("Web");
    public static final HudGroup HUD_GROUP = new HudGroup("Example");

    @Override
    public void onInitialize() {
        LOG.info("Initializing Meteor Addon Template");

        ItemData.initialize0();
        ItemData.initialize1();
        ItemData.initialize2();
        ItemData.initialize3();

        MeteorClient.EVENT_BUS.subscribe(this);
        // Modules
        Modules.get().add(mod.bot);
        Modules.get().add(mod.web);

        // Commands
        Commands.add(new CommandExample());

        // HUD
        Hud.get().register(HudExample.INFO);
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(BOTCATEGORY);
        Modules.registerCategory(WEBCATEGORY);
    }

    @Override
    public String getPackage() {
        return "com.example.addon";
    }

    @Override
    public GithubRepo getRepo() {
        return new GithubRepo("MeteorDevelopment", "meteor-addon-template");
    }
}
