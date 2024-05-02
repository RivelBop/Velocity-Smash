package com.rivelbop.velocitysmash.scene;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.rivelbop.rivelworks.math.Interpolator;
import com.rivelbop.rivelworks.screen.Scene;
import com.rivelbop.rivelworks.ui.Font;
import com.rivelbop.velocitysmash.networking.Network;

import static com.rivelbop.velocitysmash.VelocitySmash.*;

public class MainMenu extends Scene {
    private Skin skin;
    private Stage stage;
    private Font menuTextHeader;
    private SpriteBatch batch;
    private Interpolator fadeStart, fadeIn;
    private Sprite fadeBox;
    private boolean buttonPressed;
    private Texture background;

    public MainMenu(Game game) {
        super(game);
    }

    @Override
    public void show() {
        camera.position.setZero();
        camera.update();

        background = assets.get("main.png", Texture.class);

        fadeStart = new Interpolator(Interpolation.fade, 1f);
        fadeIn = new Interpolator(Interpolation.fade, 1f);
        fadeBox = new Sprite(assets.get("fadeBox.png", Texture.class));
        fadeBox.setSize(WIDTH, HEIGHT);
        fadeBox.setPosition(-WIDTH / 2f, -HEIGHT / 2f);

        menuTextHeader = new Font(assets.get("VCR.ttf", FreeTypeFontGenerator.class), 50, Color.WHITE);
        batch = new SpriteBatch();

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

        TextButton hostButton = new TextButton("HOST", skin);
        hostButton.addListener(e -> {
            if (e.isHandled() && !ipAddress.getText().isEmpty() && !port.getText().isEmpty()) {
                Network.isHosting = true;
                Network.IP = ipAddress.getText();
                Network.PORT = Integer.valueOf(port.getText());
                buttonPressed = true;
            }
            return false;
        });
        hostButton.setBounds(0f, 0f, 100f, 33f);
        centerUI(hostButton, 0f);
        stage.addActor(hostButton);

        TextButton join = new TextButton("JOIN", skin);
        join.addListener(e -> {
            if (e.isHandled() && !ipAddress.getText().isEmpty() && !port.getText().isEmpty()) {
                Network.isHosting = false;
                Network.IP = ipAddress.getText();
                Network.PORT = Integer.parseInt(port.getText());
                buttonPressed = true;
            }
            return false;
        });
        join.setBounds(0f, 0f, 100f, 33f);
        centerUI(join, -50f);
        stage.addActor(join);

        Gdx.input.setInputProcessor(stage);
        stage.unfocusAll();
    }

    @Override
    public void render(float delta) {
        fadeBox.setAlpha(-fadeStart.update());
        if (buttonPressed) {
            fadeBox.setAlpha(fadeIn.update());
        }

        camera.update();
        batch.setProjectionMatrix(camera.projection);
        batch.begin();
        batch.setColor(1f, 1f, 1f, 0.65f);
        batch.draw(background, -WIDTH / 2f, -HEIGHT / 2f);
        batch.setColor(Color.WHITE);
        menuTextHeader.drawCenter(batch, "Velocity Smash", 0f, 325f);
        batch.end();

        stage.act();
        stage.draw();

        batch.setProjectionMatrix(camera.projection);
        batch.begin();
        fadeBox.draw(batch);
        batch.end();

        if (fadeIn.isComplete()) {
            game.setScreen(new MainGame(game));
        } else if(Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
        	Gdx.app.exit();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        batch.dispose();
    }

    private void centerUI(Actor actor, float yOffset) {
        actor.setPosition(WIDTH / 2f - actor.getWidth() / 2f,
                HEIGHT / 2f + actor.getHeight() / 2f + yOffset);
    }
}