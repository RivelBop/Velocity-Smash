package com.rivelbop.velocitysmash.destructible;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;
import com.rivelbop.velocitysmash.VelocitySmash;

public class Crate extends Destructible {
    public Crate(World world) {
        super(VelocitySmash.assets.get("crate.png", Texture.class), "crateBreak.p",
                world, 0.2f, 0.5f, 0.1f,
                2.5f);
        sprite.setSize(24f, 24f);
       
    }
}