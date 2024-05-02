package com.rivelbop.velocitysmash.destructible;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.rivelbop.rivelworks.physics2d.body.DynamicBody;
import com.rivelbop.velocitysmash.Player;
import com.rivelbop.velocitysmash.scene.MainGame;

import static com.rivelbop.velocitysmash.VelocitySmash.PPM;
import static com.rivelbop.velocitysmash.VelocitySmash.assets;

public abstract class Destructible implements Disposable {
    public Sprite sprite;
    public World world;
    public DynamicBody body;

    public String particle;
    public float speedHit;
    public boolean isHit;
    public Sound hitSound;

    public Destructible(Texture texture, String particle, World world, float density, float friction, float bounciness, float speedHit) {
        sprite = new Sprite(texture);
        if (texture.toString().equals("person.png")) {
            sprite.setSize(sprite.getWidth() * 2f, sprite.getHeight() * 2f);
        }

        body = new DynamicBody(world, new PolygonShape() {{
            setAsBox(sprite.getWidth() / 2f / PPM, sprite.getHeight() / 2f / PPM);
        }}, density, friction, bounciness);
        body.getBody().setUserData(sprite);

        this.world = world;
        this.particle = particle;
        this.speedHit = speedHit;

        hitSound = Gdx.audio.newSound(Gdx.files.internal("crateHit.mp3"));
    }

    public void render(SpriteBatch batch) {
        if (!isHit) {
            sprite.draw(batch);
        }
    }

    public void update(Player player) {
        if (!isHit && Math.abs(player.velocity) >= speedHit && sprite.getBoundingRectangle().overlaps(player.sprite.getBoundingRectangle())) {
            isHit = true;
            world.destroyBody(body.getBody());

            ParticleEffect effect = new ParticleEffect(assets.get(particle, ParticleEffect.class));
            effect.setPosition(sprite.getX(), sprite.getY());
            effect.start();
            effect.scaleEffect(0.4f);
            MainGame.particles.add(effect);
            hitSound.setPitch(hitSound.play(), MathUtils.random(0.3f, 1f));
        }

        if (!isHit) {
            sprite.setPosition(body.getBody().getPosition().x * PPM - sprite.getWidth() / 2f, body.getBody().getPosition().y * PPM - sprite.getHeight() / 2f);
            sprite.setOriginCenter();
            sprite.setRotation((float) Math.toDegrees(body.getBody().getAngle()));
        }
    }

    @Override
    public void dispose() {
        hitSound.dispose();
    }
}