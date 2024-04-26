package com.rivelbop.velocitysmash.destructible;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.World;
import com.rivelbop.velocitysmash.Player;

public class Person extends Destructible {
    public int direction;

    public Person(World world) {
        super(new Texture("badlogic.jpg"), "explosion.p",
                world, 0.2f, 0.5f, 1f,
                1f);
        direction = MathUtils.random(0, 3);
    }

    public void update(Player player) {
        float xAngle = (float) Math.cos(Math.PI * direction / 2f);
        float yAngle = (float) Math.sin(Math.PI * direction / 2f);

        if (!isHit) {
            body.getBody().setLinearVelocity(0.5f * xAngle, 0.5f * yAngle);
        }
        super.update(player, 0.3f);
    }
}