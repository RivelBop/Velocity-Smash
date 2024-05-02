package com.rivelbop.velocitysmash.networking;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.rivelbop.rivelworks.physics2d.body.StaticBody;

import static com.rivelbop.velocitysmash.VelocitySmash.PPM;
import static com.rivelbop.velocitysmash.VelocitySmash.assets;

public class PlayerClient {
    public PlayerServer playerServer;
    public Sprite sprite;
    public StaticBody body;

    public PlayerClient(World world, float x, float y, float rotation, int health) {
        sprite = new Sprite(assets.get("carBomb.png", Texture.class));

        body = new StaticBody(world, new PolygonShape() {{
            setAsBox(
                    sprite.getWidth() / 2f / PPM,
                    sprite.getHeight() / 2f / PPM
            );
        }});
        body.getBody().setUserData(sprite);
        body.getBody().getTransform().setPosition(new Vector2(x, y));
        body.getBody().getTransform().setRotation(rotation);

        playerServer = new PlayerServer();
        playerServer.x = x;
        playerServer.y = y;
        playerServer.rotation = rotation;
        playerServer.health = health;
    }
}