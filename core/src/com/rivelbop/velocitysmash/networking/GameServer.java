package com.rivelbop.velocitysmash.networking;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.rivelbop.velocitysmash.networking.Network.AddPlayerPacket;
import com.rivelbop.velocitysmash.networking.Network.RemovePlayerPacket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;

public class GameServer implements Listener {
    private final Server server;
    private final HashMap<Integer, PlayerServer> players;

    public GameServer() {
        server = new Server();
        Network.register(server);
        try {
            server.bind(new InetSocketAddress(Network.IP, Network.PORT), new InetSocketAddress(Network.IP, Network.PORT));
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.start();
        server.addListener(this);
        System.out.println("The server is ready!");

        players = new HashMap<>();
    }

    @Override
    public void connected(Connection c) {
        // Create a new player up here
        PlayerServer player = new PlayerServer();
        player.rotation = 90f;

        // Notify all other players in the server that a new player has joined
        AddPlayerPacket packet = new AddPlayerPacket();
        packet.id = c.getID();
        packet.rotation = player.rotation;
        server.sendToAllExceptTCP(c.getID(), packet);

        // Send all currently joined players to the new connected player
        for (int id : players.keySet()) {
            packet = new AddPlayerPacket();
            packet.id = id;
            packet.x = players.get(id).x;
            packet.y = players.get(id).y;
            packet.rotation = players.get(id).rotation;
            c.sendTCP(packet);
        }

        // Add the player to the list of players
        players.put(c.getID(), player);
        System.out.println("Player: " + c.getID() + " has connected!");
    }

    @Override
    public void received(Connection c, Object o) {
        if (o instanceof Network.UpdatePlayerPacket) {
            Network.UpdatePlayerPacket packet = (Network.UpdatePlayerPacket) o;
            players.get(c.getID()).x = packet.x;
            players.get(c.getID()).y = packet.y;
            players.get(c.getID()).rotation = packet.rotation;

            packet.id = c.getID();
            server.sendToAllExceptUDP(c.getID(), packet);
        }
    }

    @Override
    public void disconnected(Connection c) {
        // Remove the player from the list of players
        players.remove(c.getID());

        // Notify the other players that the player has disconnected
        RemovePlayerPacket packet = new RemovePlayerPacket();
        packet.id = c.getID();
        server.sendToAllExceptTCP(c.getID(), packet);

        System.out.println("Player: " + c.getID() + " has disconnected!");
    }

    public void close() {
        server.stop();
        server.close();
        try {
            server.dispose();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}