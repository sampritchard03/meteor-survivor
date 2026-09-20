package com.example.addon.packets;

public class TickPacket extends Packet {
    public TickPacket() {
        super("tick");
    }

    public String serialize() {return "";}
}
