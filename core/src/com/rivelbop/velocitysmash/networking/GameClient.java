package com.rivelbop.velocitysmash.networking;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.rivelbop.rivelworks.ui.Font;
import com.rivelbop.velocitysmash.VelocitySmash;
import com.rivelbop.velocitysmash.scene.MainGame;

import java.io.IOException;
import java.util.HashMap;

import static com.rivelbop.velocitysmash.VelocitySmash.PPM;
import static com.rivelbop.velocitysmash.VelocitySmash.assets;

public class GameClient implements Listener {
    public static Font font;
    private float smokeTimer;
    private Client client;
    
    public boolean empty;
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

        if (client.isConnected()) {
            System.out.println("Client: Connected!");
        }
        players = new HashMap<>();

        if (font == null) {
            font = new Font(VelocitySmash.assets.get("VCR.ttf", FreeTypeFontGenerator.class), 32, Color.WHITE);
        }
    }

    @Override
    public void received(Connection c, Object o) {
        if (o instanceof Network.AddPlayerPacket) {
            // Adds a player to the players list
            Network.AddPlayerPacket packet = (Network.AddPlayerPacket) o;
            players.put(packet.id, new PlayerClient(MainGame.world, packet.x, packet.y, packet.rotation, packet.health));
            System.out.println("Client: Received Add Player Packet (ID:" + packet.id + ")");
        } else if (o instanceof Network.RemovePlayerPacket) {
            // Removes a player from the players list
            Network.RemovePlayerPacket packet = (Network.RemovePlayerPacket) o;
            players.remove(packet.id);
            empty = players.isEmpty();
            System.out.println("Client: Received Remove Player Packet (ID:" + packet.id + ")");
        } else if (o instanceof Network.UpdatePlayerPacket) {
            // Updates a player from the players list
            Network.UpdatePlayerPacket packet = (Network.UpdatePlayerPacket) o;
            PlayerServer player = players.get(packet.id).playerServer;
            player.x = packet.x;
            player.y = packet.y;
            player.rotation = packet.rotation;
            
            if(player.health > packet.health) {
            	ParticleEffect effect = new ParticleEffect(assets.get("explosion.p", ParticleEffect.class));
                effect.setPosition(player.x * PPM, player.y * PPM);
                effect.start();
                effect.scaleEffect(0.4f);
                MainGame.particles.add(effect);
            }
            player.health = packet.health;
        }
    }

    public void update() {
        smokeTimer += Gdx.graphics.getDeltaTime();
        for (PlayerClient p : players.values()) {
            // Updates the bodies of the players to their network positions
            Body body = p.body.getBody();
            Vector2 position = body.getTransform().getPosition();

            if (smokeTimer >= 0.3f) {
                if (position.x != p.playerServer.x || position.y != p.playerServer.y) {
                    float xAngle = (float) Math.cos(body.getAngle() + Math.toRadians(90f));
                    float yAngle = (float) Math.sin(body.getAngle() + Math.toRadians(90f));

                    ParticleEffect effect = new ParticleEffect();
                    effect.load(Gdx.files.internal("carParticle.p"), Gdx.files.internal(""));
                    effect.start();
                    if (yAngle < 0f) {
                        effect.setPosition(p.sprite.getX() + p.sprite.getWidth() / 2f - (xAngle * p.sprite.getWidth()), p.sprite.getY() - yAngle * p.sprite.getHeight() + Math.abs(xAngle * p.sprite.getWidth()));
                    } else {
                        effect.setPosition(p.sprite.getX() + p.sprite.getWidth() / 2f - (xAngle * p.sprite.getWidth()), p.sprite.getY() + Math.abs(xAngle * p.sprite.getHeight() / 2f));
                    }

                    effect.scaleEffect(0.1f);
                    MainGame.particles.add(effect);
                }
            }

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

        if (smokeTimer >= 0.3f) {
            smokeTimer = 0f;
        }
    }

    public void render(SpriteBatch batch) {
        for (PlayerClient p : players.values()) {
            p.sprite.draw(batch);

            Vector2 position = p.body.getBody().getTransform().getPosition();
            font.drawCenter(batch, String.valueOf(p.playerServer.health), position.x * PPM, position.y * PPM);
        }
    }

    public void sendUDP(Object o) {
        client.sendUDP(o);
    }

    public void close() {
        client.stop();
        try {
            client.dispose();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}