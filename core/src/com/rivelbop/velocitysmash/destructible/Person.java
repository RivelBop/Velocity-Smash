package com.rivelbop.velocitysmash.destructible;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.World;
import com.rivelbop.velocitysmash.Player;
import com.rivelbop.velocitysmash.VelocitySmash;

import static com.rivelbop.velocitysmash.VelocitySmash.assets;

public class Person extends Destructible {
    public int direction;

    public Person(World world) {
        super(assets.get("person.png", Texture.class), "blood.p",
                world, 0f, 0f, 0f,
                200f / VelocitySmash.PPM);
        direction = MathUtils.random(0, 3);
        body.getBody().setFixedRotation(true);
    }

    @Override
    public void update(Player player) {
        float xAngle = (float) Math.cos(Math.PI * direction / 2f);
        float yAngle = (float) Math.sin(Math.PI * direction / 2f);

        if (!isHit) {
            body.getBody().setLinearVelocity(0.5f * xAngle, 0.5f * yAngle);
        }
        super.update(player);
    }
}