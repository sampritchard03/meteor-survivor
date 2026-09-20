package com.example.addon.packets;

import com.example.addon.mod;
import com.example.addon.utils.VectorUtils;

public class SpawnPacket extends Packet {

    public SpawnPacket() {
        super("spawn");
    }

    public String serialize() {
        return  mod.bot.username+":"+
                VectorUtils.stringifyVec(mod.bot.pos())+"|"+
                VectorUtils.stringifyVec(mod.bot.vel());
    }
}
