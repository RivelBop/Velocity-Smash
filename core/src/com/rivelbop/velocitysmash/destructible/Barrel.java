package com.rivelbop.velocitysmash.destructible;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;
import com.rivelbop.velocitysmash.VelocitySmash;

import static com.rivelbop.velocitysmash.VelocitySmash.assets;

public class Barrel extends Destructible {
    public Barrel(World world) {
        super(assets.get("barrel.png", Texture.class), "explosion.p",
                world, 0.2f, 0.5f, 0.1f,
                375f / VelocitySmash.PPM);
    }
}