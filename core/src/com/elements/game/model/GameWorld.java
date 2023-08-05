package com.elements.game.model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.JsonValue;

import java.util.LinkedList;
import java.util.List;

/**
 * A GameWorld represents the collection of game objects, level-specific properties, and game
 * scenery. The game world is built on top of a Box2D world and provides direct access to game
 * object models.
 */
public class GameWorld implements Disposable {

    public static final float DEFAULT_GRAVITY = -4.9f;

    /** default constant values for all levels */
    private final JsonValue gameConstants;

    private final Vector2 gravity;

    /**
     * Box2D world object (to reset level, you destroy the world and rebuild)
     */
    private World world;

    /**
     * collection of all game objects. Invariant: objects are sorted by their z-index
     */
    private final List<CollidableObject> gameObjects;

    private Player player;

    /**
     * creates a game world.
     */
    public GameWorld(JsonValue gameConstants) {
        gravity = new Vector2(0, gameConstants.getFloat("gravity", DEFAULT_GRAVITY));
        world = new World(gravity, false);
        gameObjects = new LinkedList<>();
        this.gameConstants = gameConstants;
    }

    /**
     * empties out the level by disposing every component of the game world.
     */
    public void dispose() {
        for (CollidableObject obj : gameObjects) {
            obj.getHitBox().deactivatePhysics(world);
        }
        world.dispose();
        world = null;
        gameObjects.clear();
    }

    /**
     * Initializes the world and game objects according to the provided level data.
     *
     * @param levelData json data
     */
    public void populate(JsonValue levelData) {
        if (world != null) {
            // safety catch to dispose; prevent memory leaks
            dispose();
        }
        // TODO: may need to override gravity vector if level specifies its own gravity
        world = new World(gravity, false);
        if (levelData == null) {
            return;
        }

        JsonValue playerConstants = gameConstants.get("player");
        player = new Player(playerConstants, levelData.get("player"));
        addToPhysicsWorld(player);

        JsonValue platformDataSet = levelData.get("platform");
        for (int i = 0; i < platformDataSet.size; i++) {
            JsonValue data = platformDataSet.get(i);
            BlockPlatform platform = new BlockPlatform(gameConstants, data, "platform_" + i);
            addToPhysicsWorld(platform);
        }

        gameObjects.sort((o1, o2) -> o2.getZIndex() - o1.getZIndex());
    }

    /**
     * adds the given game object to the Box2D world by activating its hit-box.
     *
     * @param o game object
     */
    public void addToPhysicsWorld(CollidableObject o) {
        gameObjects.add(o);
        o.getHitBox().activatePhysics(world, o);
    }

    /**
     * adds a fireball to the game world based on current player state (position)
     * @param p the player instance
     */
    public Fireball summonFireBall(Player p){
        // make a new fireball (for now this is fine in terms of memory)
        Fireball fireball = new Fireball(gameConstants.get("fireball"), p);
        // add fireball to our list of objects so we can render
        addToPhysicsWorld(fireball);

        return fireball;
    }

    // BEGIN-REGION ======================== Accessors =======================================

    /**
     * The GameWorld owns this Box2D world object. Accessors of the world should not dispose of the
     * Box2D world directly. To do that correctly, call {@link #dispose()}.
     *
     * @return reference to the Box2D world.
     */
    public World getWorld() {
        return world;
    }

    public Player getPlayer() {
        return player;
    }

    public List<CollidableObject> getGameObjects() {
        return gameObjects;
    }

    public JsonValue getGameConstants() {
        return gameConstants;
    }

    // END-REGION ========================== Accessors =======================================

}
