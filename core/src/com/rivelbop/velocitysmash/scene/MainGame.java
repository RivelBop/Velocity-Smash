package com.rivelbop.velocitysmash.scene;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.rivelbop.rivelworks.map2d.OrthogonalMap;
import com.rivelbop.rivelworks.math.Interpolator;
import com.rivelbop.rivelworks.screen.Scene;
import com.rivelbop.rivelworks.ui.Font;
import com.rivelbop.velocitysmash.Player;
import com.rivelbop.velocitysmash.destructible.Barrel;
import com.rivelbop.velocitysmash.destructible.Crate;
import com.rivelbop.velocitysmash.destructible.Person;
import com.rivelbop.velocitysmash.networking.GameClient;
import com.rivelbop.velocitysmash.networking.GameServer;
import com.rivelbop.velocitysmash.networking.Network;
import com.rivelbop.velocitysmash.networking.PlayerClient;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import static com.rivelbop.velocitysmash.VelocitySmash.*;

public class MainGame extends Scene {
	private boolean isPaused;
    private float packetTimer, attackCooldown;
    private Interpolator fadeStart;
    private Sprite fadeBox;

    public GameServer server;
    public GameClient client;

    public static World world;
    public Box2DDebugRenderer debugRenderer;
    public Player player;
    public SpriteBatch batch;
    public OrthogonalMap map;

    public static Array<ParticleEffect> particles;
    public Array<Crate> crates;
    public Array<Barrel> barrels;
    public Array<Person> persons;
    
    public Font pauseFont;

    public MainGame(Game game) {
        super(game);
    }

    @Override
    public void show() {
        camera.position.setZero();
        camera.update();
        world = new World(new Vector2(0f, 0f), true);
        
        pauseFont = new Font(assets.get("VCR.ttf", FreeTypeFontGenerator.class), 54, Color.WHITE);
        
        fadeStart = new Interpolator(Interpolation.fade, 2f);
        fadeBox = new Sprite(assets.get("fadeBox.png", Texture.class));
        fadeBox.setSize(WIDTH, HEIGHT);
        fadeBox.setPosition(-WIDTH / 2f, -HEIGHT / 2f);
        fadeBox.setAlpha(1f);

        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                boolean isPlayer = contact.getFixtureA().getBody().getUserData() instanceof Sprite &&
                        contact.getFixtureA().getBody().getUserData() == player.sprite;

                if (isPlayer) {
                    boolean isSprite = contact.getFixtureB().getBody().getUserData() instanceof Sprite;
                    String id = isSprite ? ((Sprite) contact.getFixtureB().getBody().getUserData()).getTexture().toString() : "";
                    boolean isCrate = id.equals("crate.png");
                    boolean isPerson = id.equals("person.png");
                    boolean isBarrel = id.equals("barrel.png");
                    boolean isOtherPlayer = id.equals("carBomb.png");

                    if (isOtherPlayer) {
                        player.velocity *= -0.33f;
                        return;
                    }

                    if (!isCrate && !isPerson && !isBarrel) {
                        player.velocity *= -0.25f;
                        return;
                    }
                }

                boolean isPerson = contact.getFixtureA().getBody().getUserData() instanceof Sprite &&
                        ((Sprite) contact.getFixtureA().getBody().getUserData()).getTexture().toString().equals("person.png");

                if (isPerson) {
                    Sprite sprite = ((Sprite) contact.getFixtureA().getBody().getUserData());

                    for (Person p : persons) {
                        if (sprite == p.sprite) {
                            int direction = 0;
                            do {
                                direction = MathUtils.random(0, 3);
                            } while (direction == p.direction);
                            p.direction = direction;
                            break;
                        }
                    }
                }

                isPerson = contact.getFixtureB().getBody().getUserData() instanceof Sprite &&
                        ((Sprite) contact.getFixtureB().getBody().getUserData()).getTexture().toString().equals("person.png");

                if (isPerson) {
                    Sprite sprite = ((Sprite) contact.getFixtureB().getBody().getUserData());

                    for (Person p : persons) {
                        if (sprite == p.sprite) {
                            int direction = 0;
                            do {
                                direction = MathUtils.random(0, 3);
                            } while (direction == p.direction);
                            p.direction = direction;
                            return;
                        }
                    }
                }
            }

            @Override
            public void endContact(Contact contact) {
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
            }
        });
        debugRenderer = new Box2DDebugRenderer();
        debugRenderer.SHAPE_STATIC.set(Color.LIME);

        player = new Player(world);
        player.body.getBody().setTransform(5f, 5f, 0f);

        batch = new SpriteBatch();

        map = new OrthogonalMap("carMapUpgrade.tmx");
        map.boundingShapesToPhysicsWorld("collision", world, PPM);

        crates = new Array<>();
        barrels = new Array<>();
        persons = new Array<>();

        for (Rectangle r : map.getBoundingShapes(Rectangle.class, "crate")) {
            Crate crate = new Crate(world);
            crate.body.getBody().setTransform(r.x / PPM, r.y / PPM, 0f);
            crates.add(crate);
        }
        for (Rectangle r : map.getBoundingShapes(Rectangle.class, "barrel")) {
            Barrel barrel = new Barrel(world);
            barrel.body.getBody().setTransform(r.x / PPM, r.y / PPM, 0f);
            barrels.add(barrel);
        }
        for (Rectangle r : map.getBoundingShapes(Rectangle.class, "people")) {
            Person person = new Person(world);
            person.body.getBody().setTransform(r.x / PPM, r.y / PPM, 0f);
            persons.add(person);
        }

        particles = new Array<>();

        if (Network.isHosting) {
            server = new GameServer();
        }
        client = new GameClient();
        client.connect();
    }

    @Override
    public void render(float delta) {
    	if(Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
    		isPaused = !isPaused;
    	}
        fadeBox.setAlpha(-fadeStart.update());

        if (attackCooldown != 0f) {
            attackCooldown += Gdx.graphics.getDeltaTime();
        }

        if (attackCooldown >= 3f) {
            attackCooldown = 0f;
        }

        player.update();
        for (PlayerClient p : client.players.values()) {
            if (p.sprite.getBoundingRectangle().overlaps(player.back)) {
                player.velocity = 2000f;
                if (attackCooldown == 0f) {
                    player.health--;
                    attackCooldown += Gdx.graphics.getDeltaTime();
                    
                    ParticleEffect effect = new ParticleEffect(assets.get("explosion.p", ParticleEffect.class));
                    effect.setPosition(player.sprite.getX(), player.sprite.getY());
                    effect.start();
                    effect.scaleEffect(0.4f);
                    MainGame.particles.add(effect);
                }
                break;
            }
        }

        for (int i = 0; i < particles.size; i++) {
            ParticleEffect e = particles.get(i);
            e.update(Gdx.graphics.getDeltaTime());
            if (e.isComplete()) {
                e.dispose();
                particles.removeIndex(i);
                i--;
            }
        }

        for (int i = 0; i < crates.size; i++) {
            Crate c = crates.get(i);
            c.update(player);
            if (c.isHit) {
                crates.removeIndex(i);
                i--;
            }
        }

        for (int i = 0; i < barrels.size; i++) {
            Barrel b = barrels.get(i);
            b.update(player);
            if (b.isHit) {
                barrels.removeIndex(i);
                i--;
            }
        }

        for (int i = 0; i < persons.size; i++) {
            Person p = persons.get(i);
            p.update(player);
            if (p.isHit) {
                persons.removeIndex(i);
                i--;
            }
        }
        world.step(Gdx.graphics.getDeltaTime(), 8, 3);

        packetTimer += Gdx.graphics.getDeltaTime();
        if (packetTimer >= Network.PACKET_UPDATE_INTERVAL) {
            Network.UpdatePlayerPacket packet = new Network.UpdatePlayerPacket();
            packet.x = player.body.getBody().getTransform().getPosition().x;
            packet.y = player.body.getBody().getTransform().getPosition().y;
            packet.rotation = player.body.getBody().getAngle();
            packet.health = player.health;
            client.sendUDP(packet);

            packetTimer = 0f;
        }

        camera.position.set(
                player.sprite.getX() + player.sprite.getWidth() / 2f,
                player.sprite.getY() + player.sprite.getHeight() / 2f,
                0f
        );
        client.update();

        camera.update();
        map.render(camera);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        player.render(batch);
        for (ParticleEffect e : particles) {
            e.draw(batch);
        }
        for (Crate c : crates) {
            c.render(batch);
        }
        for (Barrel b : barrels) {
            b.render(batch);
        }
        for (Person p : persons) {
            p.render(batch);
        }
        client.render(batch);
        batch.end();

        debugRenderer.render(world, camera.projection.cpy().scl(5f).translate(85f, 30f, 0f));

        batch.setProjectionMatrix(camera.projection);
        batch.begin();
        
        GameClient.font.getBitmapFont().setColor(Color.GREEN);
        GameClient.font.drawCenter(batch, "LIVES: " + player.health, 0f, 300f);
        GameClient.font.getBitmapFont().setColor(Color.WHITE);
        
        if(isPaused) {
        	batch.setColor(1f, 1f, 1f, 0.33f);
        	batch.draw(fadeBox.getTexture(), -WIDTH / 2f, -HEIGHT / 2f, WIDTH, HEIGHT);
        	batch.setColor(Color.WHITE);
            pauseFont.drawCenter(batch, "PRESS SPACE TO QUIT", 0f, 0f);
        }
        fadeBox.draw(batch);
        batch.end();

        if(player.health <= 0) {
            game.setScreen(new AfterGameMenu(game, false));
        } else if(client.empty) {
        	game.setScreen(new AfterGameMenu(game, true));
        } else if(isPaused && Gdx.input.isKeyJustPressed(Keys.SPACE)) {
        	game.setScreen(new MainMenu(game));
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        client.close();
        if (Network.isHosting) {
            server.close();
        }

        batch.dispose();
        debugRenderer.dispose();
        map.dispose();

        for(Disposable d : crates) {
            d.dispose();
        }
        for(Disposable d : barrels) {
            d.dispose();
        }
        for(Disposable d : persons) {
            d.dispose();
        }
        for(ParticleEffect e : particles) {
            e.dispose();
        }

        world.dispose();
    }
}