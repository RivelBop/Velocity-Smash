package com.rivelbop.velocitysmash.networking;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

public class Network {
	public static final float PACKET_UPDATE_INTERVAL = 1 / 20f;
	public static boolean isHosting;
	public static String IP = "localhost";
    public static int PORT = 54555;

    static public void register(EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();
        kryo.register(Packet.class);
        kryo.register(AddPlayerPacket.class);
        kryo.register(RemovePlayerPacket.class);
        kryo.register(UpdatePlayerPacket.class);
    }

    private static class Packet {
        public int id;
    }

    public static class AddPlayerPacket extends Packet {
        public float x, y, rotation;
    }

    public static class RemovePlayerPacket extends Packet {
    }

    public static class UpdatePlayerPacket extends Packet {
        public float x, y, rotation;
    }
}