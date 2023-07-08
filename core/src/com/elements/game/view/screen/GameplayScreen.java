package com.elements.game.view.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.video.VideoPlayer;
import com.badlogic.gdx.video.VideoPlayerCreator;
import com.elements.game.controller.InputController;
import com.elements.game.utility.assets.AssetDirectory;
import com.elements.game.view.GameCanvas;

import java.io.FileNotFoundException;

public class GameplayScreen extends GameScreen {

    /** Exit code to quit game */
    public static final int EXIT_GAME = 0;

    private final GameCanvas canvas;

    private TextureRegion background;

    private TextureRegion playerTexture;

    private InputController inputController;

    private boolean shouldExit;

    private int exitCode;

    public GameplayScreen(GameCanvas canvas) {
        this.canvas = canvas;
    }

    @Override
    public void gatherAssets(AssetDirectory assets) {
        background = new TextureRegion(assets.getEntry("game:background", Texture.class));
        playerTexture = new TextureRegion(assets.getEntry("game:player", Texture.class));
    }

    void update(float delta){
        // here, game objects are updated, such as enemy directions
        // win/lose state
    }

    @Override
    public void render(float delta) {
        update(delta);
        canvas.clear();
        viewport.apply(true);
        canvas.begin(camera);
        // this scaling is just for background to fit exactly onto the viewable screen
        float scaleX = viewport.getWorldWidth() / background.getRegionWidth();
        float scaleY = viewport.getWorldHeight() / background.getRegionHeight();
        canvas.draw(background, Color.WHITE, background.getRegionWidth() / 2f,
                    background.getRegionHeight() / 2f, camera.position.x, camera.position.y, 0,
                    scaleX, scaleY);

        canvas.draw(playerTexture, Color.WHITE, playerTexture.getRegionWidth() / 2f,
                    playerTexture.getRegionHeight() / 2f, camera.position.x, camera.position.y, 0,
                    1, 1);
        // 1 game unit = 30 px => 1 by 1 character => 30 by 30 px in viewport coordinate =>
        // 1600by900 => 4k by 4k you will see more than 30px, 1000 by 780 => less than 30px wide
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

    @Override
    public void show() {
        super.show();
    }
}
