package com.rivelbop.velocitysmash.scene;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.rivelbop.rivelworks.screen.Scene;
import com.rivelbop.rivelworks.ui.Font;

import static com.rivelbop.velocitysmash.VelocitySmash.*;

public class AfterGameMenu extends Scene {
    private Texture background;
    private boolean hasWon;
    public Font deathTextHeader, deathText;
    public SpriteBatch batch;
    public Sound hurt;
    public String text;

    public AfterGameMenu(Game game, boolean hasWon) {
        super(game);
        this.hasWon = hasWon;
    }

    @Override
    public void show() {
        camera.position.setZero();
        camera.update();

        background = assets.get("death.png", Texture.class);
        deathTextHeader = new Font(assets.get("VCR.ttf", FreeTypeFontGenerator.class), 50, hasWon ? Color.GREEN : Color.RED);
        deathText = new Font(assets.get("VCR.ttf", FreeTypeFontGenerator.class), 35, Color.YELLOW);
        batch = new SpriteBatch();
        hurt = Gdx.audio.newSound(Gdx.files.internal("deathSound.wav"));
        hurt.setPitch(hurt.play(), 0.2f);
        
        text = hasWon ? "YOU WIN!!!" : "YOU SUCK!!!";
    }

    @Override
    public void render(float delta) {
        camera.update();
        batch.setProjectionMatrix(camera.projection);
        batch.begin();
        batch.setColor(1f, 1f, 1f, 0.65f);
        batch.draw(background, -WIDTH / 2f, -HEIGHT / 2f);
        batch.setColor(Color.WHITE);
        deathTextHeader.drawCenter(batch, text, 0, 100f);
        deathText.drawCenter(batch, "Press Space To Play Again", 0f, -100f);
        batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            game.setScreen(new MainMenu(game));
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
        hurt.dispose();
    }
}