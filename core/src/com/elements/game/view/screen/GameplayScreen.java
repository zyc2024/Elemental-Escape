package com.elements.game.view.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.video.VideoPlayer;
import com.badlogic.gdx.video.VideoPlayerCreator;
import com.elements.game.utility.assets.AssetDirectory;
import com.elements.game.view.GameCanvas;

import java.io.FileNotFoundException;

public class GameplayScreen extends GameScreen {

    /** Exit code to quit game */
    public static final int EXIT_GAME = 0;

    private final GameCanvas canvas;

    private TextureRegion background;

    private boolean shouldExit;

    private int exitCode;

    private boolean playVideo;

    private VideoPlayer videoPlayer;

    public GameplayScreen(GameCanvas canvas) {
        this.canvas = canvas;
    }

    @Override
    public void gatherAssets(AssetDirectory assets) {
        background = new TextureRegion(assets.getEntry("game:background", Texture.class));
        videoPlayer = VideoPlayerCreator.createVideoPlayer();
        videoPlayer.setOnCompletionListener(new VideoPlayer.CompletionListener() {
            @Override
            public void onCompletionListener(FileHandle file) {
                // video finished
            }
        });

        try {
            videoPlayer.play(Gdx.files.internal("v.webm"));
        } catch (FileNotFoundException e) {
            Gdx.app.error("gdx-video", "video file cannot be loaded");
        }
    }

    @Override
    public void render(float delta) {
        canvas.clear();
        viewport.apply(true);
        canvas.begin(camera);
        if (playVideo) {
            videoPlayer.update();
            Texture frame = videoPlayer.getTexture();
            if (frame != null) canvas.draw(frame, Color.WHITE, 0, 0, viewport.getWorldWidth(),
                                           viewport.getWorldHeight());
        } else {
            float scaleX = viewport.getWorldWidth() / background.getRegionWidth();
            float scaleY = viewport.getWorldHeight() / background.getRegionHeight();
            canvas.draw(background, Color.WHITE, background.getRegionWidth() / 2f,
                        background.getRegionHeight() / 2f, camera.position.x, camera.position.y, 0,
                        scaleX, scaleY);
        }
        canvas.end();
    }

    @Override
    public void dispose() {
        viewport = null;
        camera = null;
        background = null;
        videoPlayer.dispose();
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
        this.playVideo = true;
    }
}
