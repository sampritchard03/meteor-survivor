package com.example.addon.packets;

import com.example.addon.AddonTemplate;
import com.example.addon.mod;
import com.example.addon.utils.VectorUtils;

import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.world.phys.Vec3;

public class PositionPacket extends Packet {

    public PositionPacket() {
        super("position");
    }

    public String serialize() {
        Vec3 p = mod.bot.pos();
        Vec3 v = mod.bot.vel();
        return  VectorUtils.stringifyVec(p)+"|"+VectorUtils.stringifyVec(v);
    }
}
