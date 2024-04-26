package com.rivelbop.velocitysmash;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.rivelbop.rivelworks.ui.Font;
import com.rivelbop.rivelworks.utils.Utils;
import com.rivelbop.velocitysmash.scene.LoadingGame;

public class VelocitySmash extends Game {
    public static final int HEIGHT = 720, WIDTH = HEIGHT * 16 / 9;
    public static final float PPM = 100f;

    public static Viewport viewport;
    public static OrthographicCamera camera;
    public static AssetManager assets;

    public Font fps;

    @Override
    public void create() {
        viewport = new ScalingViewport(Scaling.stretch, WIDTH, HEIGHT);
        camera = (OrthographicCamera) viewport.getCamera();
        camera.update();

        assets = new AssetManager();
        assets.setLoader(
                FreeTypeFontGenerator.class,
                new FreeTypeFontGeneratorLoader(
                        new InternalFileHandleResolver()
                )
        );
        assets.setLoader(
                BitmapFont.class,
                ".ttf",
                new FreetypeFontLoader(
                        new InternalFileHandleResolver()
                )
        );
        assets.setLoader(
                ParticleEffect.class,
                new ParticleEffectLoader(
                        new InternalFileHandleResolver()
                )
        );

        fps = new Font(Gdx.files.internal("Nexa.ttf"), 32, Color.WHITE);
        setScreen(new LoadingGame(this));
    }

    @Override
    public void render() {
        Utils.clearScreen2D();
        if(!Gdx.graphics.isFullscreen()) {
        	//Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        }
        super.render();
    }

    @Override
    public void dispose() {
        getScreen().dispose();
        assets.dispose();
    }
}