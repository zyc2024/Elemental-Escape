package com.elements.game.view.screen;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.JsonValue;
import com.elements.game.controller.GameplayController;
import com.elements.game.model.CollidableObject;
import com.elements.game.model.GameObject;
import com.elements.game.model.GameWorld;
import com.elements.game.model.Player;
import com.elements.game.utility.assets.AssetDirectory;
import com.elements.game.view.GameCanvas;
import com.elements.game.visitors.GameObjectRenderer;

public class GameplayScreen extends GameScreen {

    /** Exit code to quit game */
    public static final int EXIT_GAME = 0;

    private AssetDirectory assetDirectory;

    private JsonValue levelData;

    private final GameCanvas canvas;

    /** game background image (optional) */
    private TextureRegion background;

    /** renderer to draw game objects */
    private final GameObjectRenderer renderer;

    private final GameplayController gameplayController;

    private GameWorld gameWorld;


    public GameplayScreen(GameCanvas canvas) {
        this.canvas = canvas;
        this.renderer = new GameObjectRenderer(canvas);
        this.gameplayController = new GameplayController();
    }

    @Override
    public void gatherAssets(AssetDirectory assets) {
        this.assetDirectory = assets;
        background = new TextureRegion(assets.getEntry("game:background", Texture.class));
        renderer.gatherAssets(assets);
        JsonValue gameConstants = assets.getEntry("constants", JsonValue.class);
        gameWorld = new GameWorld(gameConstants);
        gameplayController.setWorld(gameWorld);
    }

    private void update(float delta) {
        // here, game objects are updated, such as enemy directions, we will update the objects
        // using a gameplay controller (to control the components of the game world). The rest of
        // the update loop should focus on high-level updates, such as reading whether game has
        // lost or won to transition to another screen (by setting exit-code).
        gameplayController.update(delta);
    }

    @Override
    public void render(float delta) {
        update(delta);
        canvas.clear();
        viewport.apply(true);
        canvas.begin(camera);
        // this background to fit exactly onto the viewable screen
        canvas.draw(background, Color.WHITE, background.getRegionWidth() / 2f,
                    background.getRegionHeight() / 2f, camera.position.x, camera.position.y, 0,
                    viewport.getWorldWidth() / background.getRegionWidth(),
                    viewport.getWorldHeight() / background.getRegionHeight());
        // TODO (task): get game objects from level, render game objects (at most 3 lines of code)
        gameWorld.getPlayer().accept(renderer);
        gameWorld.getGameObjects().forEach( (CollidableObject co) -> {co.accept(renderer);} );
        // TODO (task): code above this line
        canvas.end();
    }

    /**
     * sets the current level to be played. This also resets the game screen.
     *
     * @param level game level id
     */
    public void setLevel(int level) {
        levelData = assetDirectory.getEntry("level" + level, JsonValue.class);
        reset();
    }

    /**
     * Resets the current gameplay (level)
     */
    public void reset() {
        gameWorld.dispose();
        gameWorld.populate(levelData);
        // TODO (later): set draw scale (conversion from 1 unit of game to number of pixels based
        //  on the desired number of game units to render). For instance, right now the
        //  denominators indicate that we split the screen into 16 columns and 9 rows.
        this.renderer.setDrawScale(viewport.getWorldWidth() / 16, viewport.getWorldHeight() / 9);
    }


    @Override
    public void dispose() {
        viewport = null;
        camera = null;
        background = null;
        gameWorld.dispose();
        gameWorld = null;
    }

    @Override
    public boolean keyDown(int keycode) {
        // this is an example of a screen specific input.
        // realistically, you should never exit game by a press of a button (accidental events).
        if (keycode == Input.Keys.ESCAPE) {
            exitCode = EXIT_GAME;
        }
        return true;
    }
}
