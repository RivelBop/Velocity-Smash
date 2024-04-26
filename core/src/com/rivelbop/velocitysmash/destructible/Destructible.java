package com.rivelbop.velocitysmash.destructible;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.rivelbop.rivelworks.physics2d.body.DynamicBody;
import com.rivelbop.velocitysmash.Player;
import com.rivelbop.velocitysmash.scene.MainGame;

import static com.rivelbop.velocitysmash.VelocitySmash.PPM;

public abstract class Destructible {
    public World world;
    public Sprite sprite;
    public DynamicBody body;

    public ParticleEffect effect;
    public String particle;

    public float speedHit;
    public boolean isHit;

    public Destructible(Texture texture, String particle, World world, float density, float friction, float bounciness, float speedHit) {
        sprite = new Sprite(texture);

        this.particle = particle;
        effect = new ParticleEffect();

        this.world = world;
        body = new DynamicBody(world, new PolygonShape() {{
            setAsBox(sprite.getWidth() / 2f / PPM, sprite.getHeight() / 2f / PPM);
        }}, density, friction, bounciness);
        body.getBody().setUserData(sprite);

        this.speedHit = speedHit;
    }

    public void render(SpriteBatch batch) {
        if (!isHit) {
            sprite.draw(batch);
        }
        effect.draw(batch);
    }

    public void update(Player player, float size) {
        effect.update(Gdx.graphics.getDeltaTime());
        if (!isHit && Math.abs(player.velocity) >= speedHit && sprite.getBoundingRectangle().overlaps(player.sprite.getBoundingRectangle())) {
            isHit = true;
            world.destroyBody(body.getBody());

            effect.load(Gdx.files.internal(particle), Gdx.files.internal(""));
            effect.setPosition(sprite.getX(), sprite.getY());
            effect.start();
            effect.scaleEffect(size);
            MainGame.particles.add(effect);
            
        }

        if (!isHit) {
            sprite.setPosition(body.getBody().getPosition().x * PPM - sprite.getWidth() / 2f, body.getBody().getPosition().y * PPM - sprite.getHeight() / 2f);
            sprite.setOriginCenter();
            sprite.setRotation((float) Math.toDegrees(body.getBody().getAngle()));
        }
    }
}