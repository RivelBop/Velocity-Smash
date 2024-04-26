package com.rivelbop.velocitysmash.networking;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.rivelbop.velocitysmash.scene.MainGame;

import java.io.IOException;
import java.util.HashMap;

import static com.rivelbop.velocitysmash.VelocitySmash.PPM;

public class GameClient implements Listener {
    private Client client;
    public HashMap<Integer, PlayerClient> players;

    public void connect() {
        client = new Client();
        Network.register(client);
        client.start();
        client.addListener(this);
        try {
            client.connect(5000, Network.IP, Network.PORT, Network.PORT);
        } catch (IOException e) {
            System.err.println("Client: Not Connected!");
            e.printStackTrace();
        }

        if(client.isConnected()) {
            System.out.println("Client: Connected!");
        }
        players = new HashMap<>();
    }

    @Override
    public void received(Connection c, Object o) {
        if (o instanceof Network.AddPlayerPacket) {
            // Adds a player to the players list
            Network.AddPlayerPacket packet = (Network.AddPlayerPacket) o;
            players.put(packet.id, new PlayerClient(MainGame.world, packet.x, packet.y, packet.rotation));
            System.out.println("Client: Received Add Player Packet (ID:" + packet.id + ")");
        } else if (o instanceof Network.RemovePlayerPacket) {
            // Removes a player from the players list
            Network.RemovePlayerPacket packet = (Network.RemovePlayerPacket) o;
            players.remove(packet.id);
            System.out.println("Client: Received Remove Player Packet (ID:" + packet.id + ")");
        } else if (o instanceof Network.UpdatePlayerPacket) {
            // Updates a player from the players list
            Network.UpdatePlayerPacket packet = (Network.UpdatePlayerPacket) o;
            PlayerServer player = players.get(packet.id).playerServer;
            player.x = packet.x;
            player.y = packet.y;
            player.rotation = packet.rotation;
        }
    }

    public void update() {
        for (PlayerClient p : players.values()) {
            // Updates the bodies of the players to their network positions
            Body body = p.body.getBody();
            Vector2 position = body.getTransform().getPosition();
            /*body.setTransform(
                    p.playerServer.x - position.x,
                    p.playerServer.y,
                    p.playerServer.rotation
            );
            Vector2 position = body.getTransform().getPosition();
            */
            body.setTransform(
                    position.x + (p.playerServer.x - position.x) / 10f,
                    position.y + (p.playerServer.y - position.y) / 10f,
                    body.getAngle() + (p.playerServer.rotation - body.getAngle()) / 10f
            );

            // Updates the sprites of the players to their physics bodies positions
            p.sprite.setPosition(position.x * PPM - p.sprite.getWidth() / 2f, position.y * PPM - p.sprite.getHeight() / 2f);
            p.sprite.setOriginCenter();
            p.sprite.setRotation((float) Math.toDegrees(body.getAngle()));
        }
    }

    public void render(SpriteBatch batch) {
        for (PlayerClient p : players.values()) {
            p.sprite.draw(batch);
        }
    }

    public void sendTCP(Object o) {
        client.sendTCP(o);
    }
    
    public void sendUDP(Object o) {
    	client.sendUDP(o);
    }

    public void close() {
        client.stop();
        client.close();
        try {
            client.dispose();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}