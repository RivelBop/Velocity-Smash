package com.rivelbop.rivelworks.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public abstract class Scene implements Screen {
    protected Game game;

    public Scene(Game game) {
        this.game = game;
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        dispose();
    }
}