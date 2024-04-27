package com.rivelbop.velocitysmash.destructible;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.rivelbop.rivelworks.physics2d.body.DynamicBody;
import com.rivelbop.velocitysmash.Player;
import com.rivelbop.velocitysmash.scene.MainGame;

import static com.rivelbop.velocitysmash.VelocitySmash.assets;
import static com.rivelbop.velocitysmash.VelocitySmash.PPM;

public abstract class Destructible {
	public Sprite sprite;
    public World world;
    public DynamicBody body;
    
    public String particle;
    public float speedHit;
    public boolean isHit;

    public Destructible(Texture texture, String particle, World world, float density, float friction, float bounciness, float speedHit) {
        sprite = new Sprite(texture);

        this.world = world;
        body = new DynamicBody(world, new PolygonShape() {{
            setAsBox(sprite.getWidth() / 2f / PPM, sprite.getHeight() / 2f / PPM);
        }}, density, friction, bounciness);
        body.getBody().setUserData(sprite);

        this.particle = particle;
        this.speedHit = speedHit;
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
            
            ParticleEffect effect = assets.get(particle, ParticleEffect.class);
            effect.setPosition(sprite.getX(), sprite.getY());
            effect.start();
            effect.scaleEffect(0.3f);
            MainGame.particles.add(effect);
        }

        if (!isHit) {
            sprite.setPosition(body.getBody().getPosition().x * PPM - sprite.getWidth() / 2f, body.getBody().getPosition().y * PPM - sprite.getHeight() / 2f);
            sprite.setOriginCenter();
            sprite.setRotation((float) Math.toDegrees(body.getBody().getAngle()));
        }
    }
}