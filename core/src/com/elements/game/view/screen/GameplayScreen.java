package com.elements.game.view.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.elements.game.utility.assets.AssetDirectory;
import com.elements.game.view.GameCanvas;

public class GameplayScreen extends GameScreen {

    /** Exit code to quit game */
    public static final int EXIT_GAME = 0;

    private final GameCanvas canvas;

    private TextureRegion background;

    private boolean shouldExit;

    private int exitCode;

    public GameplayScreen(GameCanvas canvas) {
        this.canvas = canvas;
    }

    @Override
    public void gatherAssets(AssetDirectory assets) {
        background = new TextureRegion(assets.getEntry("game:background", Texture.class));
    }

    @Override
    public void render(float delta) {
        canvas.clear();
        viewport.apply(true);
        canvas.begin(camera);
        float scaleX = viewport.getWorldWidth() / background.getRegionWidth();
        float scaleY = viewport.getWorldHeight() / background.getRegionHeight();
        canvas.draw(background, Color.WHITE, background.getRegionWidth() / 2f,
                    background.getRegionHeight() / 2f, camera.position.x, camera.position.y, 0,
                    scaleX, scaleY);
        canvas.end();
    }

    @Override
    public void dispose() {
        viewport = null;
        camera = null;
        background = null;
    }

    @Override
    public boolean shouldExit() {
        return shouldExit;
    }

    @Override
    public int exitCode() {
        return exitCode;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE) {
            exitCode = EXIT_GAME;
            shouldExit = true;
        }
        return true;
    }
}
