package com.rivelbop.velocitysmash.scene;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.rivelbop.rivelworks.map2d.OrthogonalMap;
import com.rivelbop.rivelworks.screen.Scene;
import com.rivelbop.velocitysmash.Player;
import com.rivelbop.velocitysmash.VelocitySmash;
import com.rivelbop.velocitysmash.destructible.Crate;
import com.rivelbop.velocitysmash.destructible.Person;
import com.rivelbop.velocitysmash.networking.GameClient;
import com.rivelbop.velocitysmash.networking.GameServer;
import com.rivelbop.velocitysmash.networking.Network;

import static com.rivelbop.velocitysmash.VelocitySmash.PPM;
import static com.rivelbop.velocitysmash.VelocitySmash.camera;

public class MainGame extends Scene {
	private float packetTimer;
	
    public GameServer server;
    public GameClient client;

    public static World world;
    public Box2DDebugRenderer debugRenderer;
    public Player player;
    public Person person;
    public SpriteBatch batch;
    public OrthogonalMap map;

    public static Array<ParticleEffect> particles;
    public Array<Crate> crates;

    public MainGame(Game game) {
        super(game);
    }

    @Override
    public void show() {
        world = new World(new Vector2(0f, 0f), true);
        debugRenderer = new Box2DDebugRenderer();
        debugRenderer.SHAPE_STATIC.set(Color.LIME);

        player = new Player(world);
        player.body.getBody().setTransform(5f, 5f, 0f);
        person = new Person(world);

        batch = new SpriteBatch();
        
        map = new OrthogonalMap("carMap.tmx");
        map.boundingShapesToPhysicsWorld("collision", world, PPM);
        
        crates = new Array<Crate>();
       
        for(Rectangle r : map.getBoundingShapes(Rectangle.class, "crate")) {
        	Crate crate = new Crate(world);
        	crate.body.getBody().setTransform(r.x / PPM, r.y / PPM, 0f);
        	crates.add(crate);
        }
        
        particles = new Array<>();
        
        if(Network.isHosting) {
        	server = new GameServer();
        }
        client = new GameClient();
        client.connect();
    }

    @Override
    public void render(float delta) {
        player.update();
        for (int i = 0; i < particles.size; i++) {
        	ParticleEffect e = particles.get(i);
            e.update(Gdx.graphics.getDeltaTime());
            if(e.isComplete()) {
        	   particles.removeIndex(i);
        	   i--;
            }
        }
        
        for(Crate c : crates) {
        	c.update(player);
        }
        world.step(delta, 8, 3);
        
        packetTimer += Gdx.graphics.getDeltaTime();
        if(packetTimer >= Network.PACKET_UPDATE_INTERVAL) {
	        Network.UpdatePlayerPacket packet = new Network.UpdatePlayerPacket();
	        packet.x = player.body.getBody().getTransform().getPosition().x;
	        packet.y = player.body.getBody().getTransform().getPosition().y;
	        packet.rotation = player.body.getBody().getAngle();
	        client.sendUDP(packet);
	        
	        packetTimer = 0f;
        }
        
        camera.position.set(
                player.sprite.getX() + player.sprite.getWidth() / 2f,
                player.sprite.getY() + player.sprite.getHeight() / 2f,
                0f
        );
        camera.update();

        client.update();
        map.render(camera, 0, 1);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        ((VelocitySmash) game).fps.draw(batch, String.valueOf(Gdx.graphics.getFramesPerSecond()), 0f, 0f);
        player.render(batch);
        person.render(batch);
        for (ParticleEffect e : particles) {
            e.draw(batch);
        }
        for(Crate c : crates) {
        	c.render(batch);
        }
        client.render(batch);
        batch.end();
        debugRenderer.render(world, camera.combined);
    }

    @Override
    public void resize(int width, int height) {
        VelocitySmash.viewport.update(width, height);
    }

    @Override
    public void dispose() {
        client.close();
        if(Network.isHosting) {
        	server.close();
        }
        batch.dispose();
    }
}