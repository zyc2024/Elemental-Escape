package com.elements.game.controller;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.elements.game.utility.assets.AssetDirectory;
import com.elements.game.view.GameCanvas;
import com.elements.game.view.screen.GameScreen;
import com.elements.game.view.screen.GameplayScreen;
import com.elements.game.view.screen.LoadingScreen;

public class GDXRoot extends Game {

    private AssetDirectory assetDirectory;

    private GameScreen activeScreen;

    private LoadingScreen loadingScreen;

    private GameplayScreen gameplayScreen;

    //private TransitionalScreen transitionScreen;

    private GameCanvas canvas;

    /**
     * Sets the current screen, {@link Screen#hide()} is called on any old screen, and
     * {@link Screen#show()} is called on the new screen, if any. The active screen is updated.
     *
     * @param screen game screen
     */
    public void setScreen(GameScreen screen) {
        activeScreen = screen;
        super.setScreen(screen);
    }

    @Override
    public void create() {
        canvas = new GameCanvas();
        loadingScreen = new LoadingScreen("catalog/assets.json", canvas, 1);
        gameplayScreen = new GameplayScreen(canvas);
        //transitionScreen = new TransitionalScreen(canvas);
        setScreen(loadingScreen);
    }

    @Override
    public void render() {
        super.render();
        if (activeScreen != null && activeScreen.shouldExit()) {
            // it is time to switch screens or quit game
            switchScreen(activeScreen, activeScreen.exitCode());
        }
    }

    @Override
    public void dispose() {
        loadingScreen.dispose();
        loadingScreen = null;
        if (assetDirectory != null) {
            assetDirectory.unloadAssets();
            assetDirectory.dispose();
        }
        assetDirectory = null;
        gameplayScreen.dispose();
        gameplayScreen = null;
    }

    /**
     * exit the given screen and apply transitions to exit/other screens.
     *
     * @param screen   screen to exit
     * @param exitCode associated exit code
     */
    private void switchScreen(Screen screen, int exitCode) {
        if (screen == loadingScreen) {
            assetDirectory = loadingScreen.getAssets();
            // shift focus to another screen
            gameplayScreen.gatherAssets(assetDirectory);
            //transitionScreen.gatherAssets(assetDirectory);
            //transitionScreen.setTransition(loadingScreen, gameplayScreen);
            //setScreen(transitionScreen);
            // TODO (later): this is obviously temporary, with a level selector, the loading
            //  screen would quit to main menu followed by some level selector and finally the
            //  selected level would be passed to the gameplay screen to load.
            gameplayScreen.setLevel(0);
            setScreen(gameplayScreen);
        } else if (screen == gameplayScreen) {
            switch (exitCode) {
                case GameplayScreen.EXIT_GAME:
                    Gdx.app.exit();
                    break;
                case 11010001:
                    // just some random number to demonstrate use of switch cases instead of
                    // if-else-if statements
                default:
                    break;
            }
        }
        //        else if (screen == transitionScreen){
        //            setScreen(transitionScreen.getEnterScreen());
        //        }
    }

    @Override
    public void resize(int width, int height) {
        Gdx.gl.glViewport(0, 0, width, height);
        canvas.resize();
        super.resize(width, height);
    }
}
