package com.elements.game.view.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.elements.game.utility.assets.AssetDirectory;
import com.elements.game.view.GameCanvas;

/**
 * Applies a fade out and fade in transition between two screens.
 */
public class TransitionalScreen extends GameScreen {

    private static final float ALPHA_CHANGE = 0.01f;

    private final GameCanvas canvas;

    private final Color backgroundTint;

    private boolean shouldExit;

    /** the current screen to exit */
    private GameScreen exitScreen;

    /** the screen to transition into */
    private GameScreen enterScreen;

    private TextureRegion background;

    private float backgroundAlpha;

    private Phase phase;

    public TransitionalScreen(GameCanvas canvas) {
        this.canvas = canvas;
        backgroundTint = new Color();
    }

    public void gatherAssets(AssetDirectory assets) {
        this.background = new TextureRegion(assets.getEntry("transition:black", Texture.class));
    }

    @Override
    public void render(float delta) {
        if (phase == Phase.FADE_OUT) {
            backgroundAlpha += ALPHA_CHANGE;
            exitScreen.render(delta);
            if (backgroundAlpha >= 1) {
                backgroundAlpha = 1;
                phase = Phase.FADE_IN;
            }
        } else if (phase == Phase.FADE_IN){
            backgroundAlpha -= ALPHA_CHANGE;
            enterScreen.render(delta);
            if (backgroundAlpha < 0) {
                backgroundAlpha = 0;
                shouldExit = true;
            }
        }
        backgroundTint.set(0, 0, 0, backgroundAlpha);
        viewport.apply(true);
        canvas.begin(camera);
        canvas.draw(background, backgroundTint, 0, 0,
                    viewport.getWorldWidth(), viewport.getWorldHeight());
        canvas.end();

    }

    @Override
    public void dispose() {
        background = null;
    }

    @Override
    public boolean shouldExit() {
        return shouldExit;
    }

    @Override
    public int exitCode() {
        return 0;
    }

    /**
     * sets up configurations in order to perform transition between two screens
     *
     * @param exitScreen  screen to fade out
     * @param enterScreen screen to fade in
     */
    public void setTransition(GameScreen exitScreen, GameScreen enterScreen) {
        this.exitScreen = exitScreen;
        this.enterScreen = enterScreen;
        phase = Phase.FADE_OUT;
        backgroundAlpha = 0;
        shouldExit = false;
    }

    /**
     * @return the screen to switch to after transitions are complete
     */
    public GameScreen getEnterScreen() {
        return this.enterScreen;
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        enterScreen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    private enum Phase {
        FADE_OUT, FADE_IN
    }

}
