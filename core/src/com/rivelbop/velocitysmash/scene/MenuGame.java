package com.rivelbop.velocitysmash.scene;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.rivelbop.rivelworks.screen.Scene;
import com.rivelbop.velocitysmash.VelocitySmash;
import com.rivelbop.velocitysmash.networking.Network;

import static com.rivelbop.velocitysmash.VelocitySmash.*;

public class MenuGame extends Scene {
	private Skin skin;
	private Stage stage;
	
	public MenuGame(Game game) {
		super(game);
	}

	@Override
	public void show() {
		camera.position.setZero();
		camera.update();
		
		skin = new Skin(Gdx.files.internal("default/skin/uiskin.json"));
		stage = new Stage(viewport);
		
		Label ipAddressLabel = new Label("IP Address:", skin);
		centerUI(ipAddressLabel, 200f);
		stage.addActor(ipAddressLabel);
		
		TextField ipAddress = new TextField(Network.IP, skin);
		centerUI(ipAddress, 150f);
		stage.addActor(ipAddress);
		
		Label portLabel = new Label("Port:", skin);
		centerUI(portLabel, 100f);
		stage.addActor(portLabel);
		
		TextField port = new TextField(Integer.toString(Network.PORT), skin);
		centerUI(port, 50f);
		stage.addActor(port);
		
		TextButton host = new TextButton("HOST", skin);
		host.addListener(new EventListener() {
			@Override
			public boolean handle(Event event) {
				if(event.isHandled() && !ipAddress.getText().isEmpty() && !port.getText().isEmpty()) {
					Network.isHosting = true;
					Network.IP = ipAddress.getText();
					Network.PORT = Integer.valueOf(port.getText());
					game.setScreen(new MainGame(game));
				}
				return false;
			}
		});
		
		host.setBounds(0f, 0f, 100f, 33f);
		centerUI(host, 0f);
		stage.addActor(host);
		
		TextButton join = new TextButton("JOIN", skin);
		join.addListener(new EventListener() {
			@Override
			public boolean handle(Event event) {
				if(event.isHandled() && !ipAddress.getText().isEmpty() && !port.getText().isEmpty()) {
					Network.isHosting = false;
					Network.IP = ipAddress.getText();
					Network.PORT = Integer.valueOf(port.getText());
					game.setScreen(new MainGame(game));
				}
				return false;
			}
		});
		join.setBounds(0f, 0f, 100f, 33f);
		centerUI(join, -50f);
		stage.addActor(join);
		
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render(float delta) {
		camera.update();
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}

	@Override
	public void dispose() {
		stage.dispose();
	}
	
	private void centerUI(Actor actor, float yOffset) {
		actor.setPosition(VelocitySmash.viewport.getScreenWidth() / 2 - actor.getWidth() / 2,
				VelocitySmash.viewport.getScreenHeight() / 2 + actor.getHeight() / 2 + yOffset);
	}
}