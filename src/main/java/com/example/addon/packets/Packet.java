package com.example.addon.packets;

public abstract class Packet {
    public final String name;

    public Packet(String name) {
        this.name = name;
    }
    public abstract String serialize();

    public String toString() {
        return this.name+":|:"+serialize();
    }
}
