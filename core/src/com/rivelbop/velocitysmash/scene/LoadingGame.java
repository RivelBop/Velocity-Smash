package com.rivelbop.velocitysmash.scene;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.rivelbop.rivelworks.screen.Scene;

import static com.rivelbop.velocitysmash.VelocitySmash.assets;
import static com.rivelbop.velocitysmash.VelocitySmash.viewport;

public class LoadingGame extends Scene {
    public LoadingGame(Game game) {
        super(game);
    }

    @Override
    public void show() {
        assets.load("badlogic.jpg", Texture.class);
        assets.load("car.png", Texture.class);
        assets.load("crate.png", Texture.class);
       
    }

    @Override
    public void render(float delta) {
        if (assets.update()) {
            game.setScreen(new MenuGame(game));
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {

    }
}