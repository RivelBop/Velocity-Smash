package com.rivelbop.velocitysmash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.rivelbop.rivelworks.physics2d.body.DynamicBody;
import com.rivelbop.velocitysmash.scene.MainGame;

import static com.rivelbop.velocitysmash.VelocitySmash.PPM;

public class Player {
    public Rectangle back;
    public Sprite sprite;
    public DynamicBody body;
    public float velocity, acceleration, deceleration, rotationSpeed, maxSpeed, driveParticleTimer;
    public boolean isMoving;
    public int health = 3;

    public Player(World world) {
        sprite = new Sprite(VelocitySmash.assets.get("carBomb.png", Texture.class));
        back = new Rectangle(0f, 0f, 5f, 5f);

        body = new DynamicBody(world,
                new PolygonShape() {{
                    setAsBox(sprite.getWidth() / 2f / PPM, sprite.getHeight() / 2f / PPM);
                }},
                0f, 0f, 0f);
        body.getBody().setUserData(sprite);

        acceleration = 175f / PPM;
        deceleration = 150f / PPM;
        maxSpeed = 450f / PPM;
        rotationSpeed = 150f;
    }

    public void render(SpriteBatch batch) {
        sprite.draw(batch);
    }

    public void update() {
        Body physicsBody = body.getBody();
        Vector2 position = physicsBody.getTransform().getPosition();

        isMoving = false;

        float xAngle = (float) Math.cos(physicsBody.getAngle() + Math.toRadians(90f));
        float yAngle = (float) Math.sin(physicsBody.getAngle() + Math.toRadians(90f));

        if (Gdx.input.isKeyPressed(Keys.W)) {
            velocity += acceleration * Gdx.graphics.getDeltaTime();
            isMoving = true;
            createDriveParticle();
        }

        if (Gdx.input.isKeyPressed(Keys.S)) {
            velocity -= acceleration * Gdx.graphics.getDeltaTime();
            isMoving = true;
            createDriveParticle();
        }

        if (!isMoving && velocity != 0f) {
            velocity += (velocity < 0f ? 1f : -1f) * deceleration * Gdx.graphics.getDeltaTime();
            velocity = (velocity < 0f ? -1f : 1f) * Math.max(Math.abs(velocity), 0f);

            if (Math.abs(velocity) < 0.01f) {
                velocity = 0f;
            }
        }

        velocity = (velocity < 0f ? -1f : 1f) * Math.min(Math.abs(velocity), maxSpeed);
        physicsBody.setLinearVelocity(xAngle * velocity, yAngle * velocity);

        if (velocity != 0f && Gdx.input.isKeyPressed(Keys.A)) {
            physicsBody.setTransform(position.x, position.y, (float) (physicsBody.getAngle() + Math.toRadians(rotationSpeed) * Gdx.graphics.getDeltaTime()));
        }

        if (velocity != 0f && Gdx.input.isKeyPressed(Keys.D)) {
            physicsBody.setTransform(position.x, position.y, (float) (physicsBody.getAngle() - Math.toRadians(rotationSpeed) * Gdx.graphics.getDeltaTime()));
        }

        sprite.setPosition(position.x * PPM - sprite.getWidth() / 2f, position.y * PPM - sprite.getHeight() / 2f);
        sprite.setOriginCenter();
        sprite.setRotation((float) Math.toDegrees(physicsBody.getAngle()));

        back.setPosition(sprite.getX() + sprite.getWidth() / 2f - back.width / 2f - sprite.getWidth() * xAngle, sprite.getY() + sprite.getHeight() / 2f - back.height / 2f - sprite.getWidth() * yAngle);
    }

    private void createDriveParticle() {
        driveParticleTimer += Gdx.graphics.getDeltaTime();
        if (isMoving && driveParticleTimer >= 0.3f) {
            float xAngle = (float) Math.cos(body.getBody().getAngle() + Math.toRadians(90f));
            float yAngle = (float) Math.sin(body.getBody().getAngle() + Math.toRadians(90f));

            ParticleEffect effect = new ParticleEffect();
            effect.load(Gdx.files.internal("carParticle.p"), Gdx.files.internal(""));
            effect.start();
            if (yAngle < 0f) {
                effect.setPosition(sprite.getX() + sprite.getWidth() / 2f - (xAngle * sprite.getWidth()), sprite.getY() - yAngle * sprite.getHeight() + Math.abs(xAngle * sprite.getWidth()));
            } else {
                effect.setPosition(sprite.getX() + sprite.getWidth() / 2f - (xAngle * sprite.getWidth()), sprite.getY() + Math.abs(xAngle * sprite.getHeight() / 2f));
            }

            effect.scaleEffect(0.1f);
            MainGame.particles.add(effect);
            driveParticleTimer = 0f;
        }
    }
}