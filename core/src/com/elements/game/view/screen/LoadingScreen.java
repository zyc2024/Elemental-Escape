package com.elements.game.view.screen;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.elements.game.utility.assets.AssetDirectory;
import com.elements.game.view.GameCanvas;

/**
 * A loading screen renders content to the user while loading requested assets.
 */
public class LoadingScreen extends GameScreen {
    /**
     * Default budget for asset loader (do nothing but load 60 fps)
     */
    private static final int DEFAULT_BUDGET = 15;

    /**
     * Internal assets for this loading screen (must be immediately load)
     */
    private final AssetDirectory internal;

    /**
     * The actual game assets to be loaded
     */
    private final AssetDirectory assets;

    /**
     * Reference to GameCanvas for rendering
     */
    private final GameCanvas canvas;

    /** loading throbber texture */
    private final Texture throbber;

    /** loading progress text font */
    private final BitmapFont font;

    private final Color throbberColor;

    /**
     * Current progress (0 to 1) of the assets asynchronous loading
     */
    private float progress;

    /**
     * The amount of time to devote to loading assets (as opposed to screen rendering)
     */
    private int budget;

    /** angular rotation of throbber (radians) */
    private float throbberAngle;

    private float throbberTransparency;

    private float throbberRadius;

    /** number of frames until automatically switch to next screen */
    private static final int EXIT_TIMER = 150;

    /** number of frames remaining until automatically switch to next screen */
    private int exitCountDown;

    /**
     * Creates a LoadingScreen with the default loading budget.
     *
     * @param file   The asset directory to load in the background
     * @param canvas The game canvas to draw to
     */
    public LoadingScreen(String file, GameCanvas canvas) {
        this(file, canvas, DEFAULT_BUDGET);
    }

    /**
     * Creates a LoadingScreen with the given loading budget.
     * <p></p>
     * The budget is the number of milliseconds to spend loading assets each animation frame.  This
     * allows you to do something other than load assets.  An animation frame is ~16 milliseconds.
     * So if the budget is 10, you have 6 milliseconds to do something else.  This is how game
     * companies animate their loading screens.
     *
     * @param file   The asset directory to load in the background
     * @param canvas The game canvas to draw to
     * @param millis The loading budget in milliseconds
     */
    public LoadingScreen(String file, GameCanvas canvas, int millis) {
        this.canvas = canvas;
        budget = millis;

        // We need these files loaded immediately
        internal = new AssetDirectory("catalog/loading.json");
        internal.loadAssets();
        internal.finishLoading();   // synchronous (blocking) loading

        // TODO: get all textures
        throbber = internal.getEntry("throbber", Texture.class);
        font = internal.getEntry("progressFont", BitmapFont.class);
        throbberColor = new Color(Color.WHITE);
        throbberRadius = 1;

        // No progress so far.
        progress = 0;

        // Start loading the real assets
        assets = new AssetDirectory(file);
        assets.loadAssets();
    }

    /**
     * The budget is the number of milliseconds to spend loading assets each animation frame. An
     * animation frame is ~16 milliseconds. So if the budget is 10, you have 6 milliseconds to do
     * something else.
     *
     * @return the budget in milliseconds
     */
    public int getBudget() {
        return budget;
    }

    /**
     * Sets the budget for the asset loader.
     * <p>
     * The budget is the number of milliseconds to spend loading assets each animation frame.  This
     * allows you to do something other than load assets.  An animation frame is ~16 milliseconds.
     * So if the budget is 10, you have 6 milliseconds to do something else.  This is how game
     * companies animate their loading screens.
     *
     * @param millis the budget in milliseconds
     */
    public void setBudget(int millis) {
        budget = millis;
    }

    /**
     * This asset loader persists even after the scene is disposed so the game must unload the
     * assets in this directory prior to quitting.
     *
     * @return the asset directory produced by this loading screen
     */
    public AssetDirectory getAssets() {
        return assets;
    }

    @Override
    public void render(float delta) {
        update(delta);
        draw();
    }

    @Override
    public void dispose() {
        internal.unloadAssets();
        internal.dispose();
    }

    /**
     * Update the status of the loading mode.
     *
     * @param delta Number of seconds since last animation frame
     */
    private void update(float delta) {
        assets.update(budget);
        if (progress < 1){
            progress = assets.getProgress();
            if (progress >= 1.0f) {
                progress = 1.0f;
                exitCountDown = EXIT_TIMER;
            }
        }
        // todo: constants need to be defined at the top of file
        throbberAngle -= 0.02;
        throbberRadius = (float) Math.min(throbberRadius + 0.01, 2);
        throbberTransparency = (float) (0.1 + progress * 0.9);
        exitCountDown = Math.max(exitCountDown - 1, 0);
    }

    /**
     * Draw the status of the loading mode.
     */
    private void draw() {
        canvas.clear();
        viewport.apply(true);
        canvas.begin(camera);
        throbberColor.a = throbberTransparency;
        canvas.draw(throbber, throbberColor, throbber.getWidth() / 2f, throbber.getHeight() / 2f,
                    camera.position.x, camera.position.y, throbberAngle, throbberRadius,
                    throbberRadius);
        font.setColor(Color.WHITE);
        canvas.drawTextCentered(Math.round(progress * 100) + "%", font, 0, viewport.getWorldWidth(),
                                viewport.getWorldHeight());
        //
        canvas.end();
    }

    @Override
    public boolean shouldExit() {
        return isReady();
    }

    /**
     * @return true if all assets are loaded
     */
    private boolean isReady() {
        return progress >= 1 && exitCountDown == 0;
    }

    @Override
    public int exitCode() {
        return 0;
    }

}