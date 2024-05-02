package com.rivelbop.velocitysmash.scene;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.rivelbop.rivelworks.screen.Scene;

import static com.rivelbop.velocitysmash.VelocitySmash.assets;
import static com.rivelbop.velocitysmash.VelocitySmash.viewport;

public class LoadingMenu extends Scene {
    public static Music music;

    public LoadingMenu(Game game) {
        super(game);
    }

    @Override
    public void show() {
        assets.load("badlogic.jpg", Texture.class);
        assets.load("carBomb.png", Texture.class);
        assets.load("crate.png", Texture.class);
        assets.load("person.png", Texture.class);
        assets.load("barrel.png", Texture.class);
        assets.load("fadeBox.png", Texture.class);
        assets.load("main.png", Texture.class);
        assets.load("death.png", Texture.class);
        assets.load("crateBreak.p", ParticleEffect.class);
        assets.load("explosion.p", ParticleEffect.class);
        assets.load("blood.p", ParticleEffect.class);
        assets.load("VCR.ttf", FreeTypeFontGenerator.class);

        music = Gdx.audio.newMusic(Gdx.files.internal("music.wav"));
        music.setLooping(true);
        music.play();
    }


    @Override
    public void render(float delta) {
        if (assets.update()) {
            game.setScreen(new MainMenu(game));
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