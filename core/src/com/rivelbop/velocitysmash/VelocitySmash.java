package com.rivelbop.velocitysmash;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bitfire.postprocessing.PostProcessor;
import com.bitfire.postprocessing.effects.*;
import com.bitfire.postprocessing.filters.Combine;
import com.bitfire.postprocessing.filters.CrtScreen;
import com.bitfire.utils.ShaderLoader;
import com.rivelbop.rivelworks.utils.Utils;
import com.rivelbop.velocitysmash.scene.LoadingMenu;

public class VelocitySmash extends Game {
    public static final int HEIGHT = 720, WIDTH = HEIGHT * 16 / 9;
    public static final float PPM = 100f;

    public static Viewport viewport;
    public static OrthographicCamera camera;
    public static AssetManager assets;
    public static PostProcessor postProcessor;

    @Override
    public void create() {
        viewport = new ScalingViewport(Scaling.stretch, WIDTH, HEIGHT);
        camera = (OrthographicCamera) viewport.getCamera();
        camera.update();

        assets = new AssetManager(new InternalFileHandleResolver());
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

        ShaderLoader.BasePath = "shaders/";
        postProcessor = new PostProcessor(false, false, false);

        MotionBlur motionBlur = new MotionBlur();
        motionBlur.setBlurOpacity(0.5f);

        Vignette vignette = new Vignette(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        vignette.setIntensity(0.75f);

        Bloom bloom = new Bloom(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        bloom.setBloomIntesity(0.5f);

        Fxaa fxaa = new Fxaa(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        fxaa.setSpanMax(1f);

        int effects = CrtScreen.Effect.TweakContrast.v | CrtScreen.Effect.PhosphorVibrance.v | CrtScreen.Effect.Scanlines.v | CrtScreen.Effect.Tint.v;
        CrtMonitor crt = new CrtMonitor(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false, false, CrtScreen.RgbMode.RgbShift, effects);
        Combine combine = crt.getCombinePass();
        combine.setSource1Intensity(0f);
        combine.setSource2Intensity(1f);
        combine.setSource1Saturation(0f);
        combine.setSource2Saturation(1f);

        Curvature curvature = new Curvature();
        curvature.setDistortion(0.075f);

        postProcessor.addEffect(motionBlur);
        postProcessor.addEffect(vignette);
        postProcessor.addEffect(bloom);
        postProcessor.addEffect(fxaa);
        postProcessor.addEffect(crt);
        postProcessor.addEffect(curvature);

        setScreen(new LoadingMenu(this));
    }

    @Override
    public void render() {
        Utils.clearScreen2D();
        if (!Gdx.graphics.isFullscreen()) {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        }
        postProcessor.capture();
        super.render();
        postProcessor.render();
    }

    @Override
    public void dispose() {
        getScreen().dispose();
        LoadingMenu.music.dispose();
        assets.dispose();
        postProcessor.dispose();
    }
}