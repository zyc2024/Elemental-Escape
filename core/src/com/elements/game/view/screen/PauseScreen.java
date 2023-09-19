package com.elements.game.view.screen;

import com.elements.game.view.screen.GameScreen;

public class PauseScreen extends GameScreen {

    @Override
    public void render(float delta) {

    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean shouldExit() {
        return false;
    }

    @Override
    public int exitCode() {
        return 0;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }
}
