package com.example.addon.utils;

import net.minecraft.world.phys.Vec3;

public class VectorUtils {
    public static String stringifyVec(Vec3 v) {
        if (v == null) return "";
        return  String.format("%.2f", (float)v.x())+","+
                String.format("%.2f", (float)v.y())+","+
                String.format("%.2f", (float)v.z());
    }
}
