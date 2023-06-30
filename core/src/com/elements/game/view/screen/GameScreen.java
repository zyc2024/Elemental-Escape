package com.elements.game.view.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.*;

/**
 * A GameScreen provides basic functionality to read user input and manage
 * screen statuses/transitions in application.
 * <br>
 * All Game Screens must implement the following set of methods:
 * <ul>
 *     <li>Dispose() : </li>
 *     <li>Render(float dt)</li>
 * </ul>
 */
public abstract class GameScreen implements Screen, InputProcessor {

    protected OrthographicCamera camera;

    protected Viewport viewport;

    /**
     * Available Viewport options based on Viewport class hierarchy
     */
    public enum ViewportType {
        FIT, EXTEND, FILL, STRETCH
    }

    /**
     * Constructs a screen with the given viewport settings and camera. The
     * virtual dimensions set up a rectangular region in which the physical
     * game world can be rendered on.
     *
     * @param vt            viewport type
     * @param camera        rendering camera (typically orthographic)
     * @param virtualWidth  (virtual world width)
     * @param virtualHeight (virtual world height)
     */
    public GameScreen(ViewportType vt, OrthographicCamera camera,
                      int virtualWidth, int virtualHeight) {
        switch (vt) {
            case FIT:
                viewport = new FitViewport(virtualWidth, virtualHeight, camera);
                break;
            case EXTEND:
                viewport = new ExtendViewport(virtualWidth, virtualHeight,
                        camera);
                break;
            case FILL:
                viewport = new FillViewport(virtualWidth, virtualHeight,
                        camera);
                break;
            case STRETCH:
                viewport = new StretchViewport(virtualWidth, virtualHeight,
                        camera);
                break;
        }
        if (camera != null) {
            this.camera = camera;
        } else {
            this.camera = new OrthographicCamera(virtualWidth, virtualHeight);
            viewport.setCamera(this.camera);
        }
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
     * Constructs a screen with the given viewport settings. The
     * viewport dimensions set up a virtual rectangular region in which the
     * physical game world can be rendered on.
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
    public GameScreen(){
        this(ViewportType.FIT, 1600, 900);
    }


    // BEGIN-REGION ============== INPUT-PROCESSOR =============================
    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer,
                             int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
    // END-REGION ================ INPUT-PROCESSOR =============================


    // BEGIN-REGION ============== LIBGDX SCREEN ===============================
    @Override
    public void resize(int width, int height) {}

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
    // END-REGION ================ LIBGDX SCREEN ===============================
}
