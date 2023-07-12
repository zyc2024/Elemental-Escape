package com.elements.game.view.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.*;
import com.elements.game.utility.assets.AssetDirectory;

/**
 * A GameScreen provides basic functionality to read user input and manage screen
 * statuses/transitions in application.
 * <br>
 * All Game Screens must implement the following set of methods:
 * <ul>
 *     <li>Dispose() : </li>
 *     <li>Render(float deltaTime)</li>
 *     <li>shouldExit()</li>
 *     <li>exitCode()</li>
 * </ul>
 */
public abstract class GameScreen implements Screen, InputProcessor {

    protected OrthographicCamera camera;

    protected Viewport viewport;

    protected int exitCode = -1;

    /**
     * Constructs a screen with the given viewport settings and camera. The virtual dimensions set
     * up a rectangular region in which the physical game world can be rendered on.
     *
     * @param vt            viewport type
     * @param camera        rendering camera (typically orthographic)
     * @param virtualWidth  (virtual world width)
     * @param virtualHeight (virtual world height)
     */
    public GameScreen(ViewportType vt, OrthographicCamera camera, int virtualWidth, int virtualHeight) {
        if (camera != null) {
            this.camera = camera;
        } else {
            this.camera = new OrthographicCamera(virtualWidth, virtualHeight);
            this.camera.setToOrtho(false);
        }
        switch (vt) {
            case FIT:
                viewport = new FitViewport(virtualWidth, virtualHeight, this.camera);
                break;
            case EXTEND:
                viewport = new ExtendViewport(virtualWidth, virtualHeight, this.camera);
                break;
            case FILL:
                viewport = new FillViewport(virtualWidth, virtualHeight, this.camera);
                break;
            case STRETCH:
                viewport = new StretchViewport(virtualWidth, virtualHeight, this.camera);
                break;
        }
        viewport.apply(true);
    }

    /**
     * Constructs a screen with the provided viewport and camera.
     *
     * @param viewport screen viewport
     * @param camera   rendering camera
     */
    public GameScreen(Viewport viewport, OrthographicCamera camera) {
        this.viewport = viewport;
        this.camera = camera;
    }

    /**
     * Constructs a screen with the given viewport settings. The viewport dimensions set up a
     * virtual rectangular region in which the physical game world can be rendered on.
     *
     * @param vt             viewport type
     * @param viewportWidth  viewport world width (virtual)
     * @param viewportHeight viewport world height (virtual)
     */
    public GameScreen(ViewportType vt, int viewportWidth, int viewportHeight) {
        this(vt, null, viewportWidth, viewportHeight);
    }

    /**
     * Construct a 1600x900 resolution screen with letter-boxing (FitViewport).
     */
    public GameScreen() {
        this(ViewportType.FIT, 1600, 900);
    }

    @Override
    public boolean keyDown(int keycode) {
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return true;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return true;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        viewport.apply();
    }

    /**
     * This is called when the application is minimized (hidden)
     */
    @Override
    public void pause() {}

    /**
     * This is called when the application returns from minimized state.
     */
    @Override
    public void resume() {}

    @Override
    public void show() {
        // direct all inputs to this screen
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    /**
     * @return whether this screen should return control of the view
     */
    public boolean shouldExit() {
        return exitCode >= 0;
    }

    /**
     * this should be used only when {@link #shouldExit()} returns true. A valid exit code is
     * non-negative.
     * @return the exit status code
     */
    public int exitCode() {return exitCode;}

    /**
     * stores and initializes all necessary assets (textures, fonts, audio) for this game screen.
     *
     * @param assets an asset manager
     */
    public void gatherAssets(AssetDirectory assets) {}

    /**
     * Available Viewport options based on Viewport class hierarchy
     */
    public enum ViewportType {
        FIT, EXTEND, FILL, STRETCH
    }
}
