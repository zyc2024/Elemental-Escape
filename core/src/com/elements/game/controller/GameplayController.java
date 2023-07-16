package com.elements.game.controller;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.JsonValue;
import com.elements.game.model.CollidableObject;
import com.elements.game.model.GameWorld;
import com.elements.game.model.Player;

import java.util.List;

public class GameplayController implements ContactListener {

    private JsonValue gameConstants;

    /** reference to the world object */
    private GameWorld gameWorld;

    /** controller to read gameplay inputs */
    private final InputController inputController;

    /** vector cache to be used for computations */
    private final Vector2 cache;

    private float jumpForceMagnitude;
    private float walkForceMagnitude;


    public GameplayController(){
        inputController = new InputController();
        cache = new Vector2();
    }

    /**
     * loads game components from the given world.
     * @param gameWorld game world reference
     */
    public void setWorld(GameWorld gameWorld){
        // grab what we need to CONTROL
        this.gameWorld = gameWorld;
        this.gameConstants = gameWorld.getGameConstants();
        JsonValue playerConstants = gameConstants.get("player");
        jumpForceMagnitude = playerConstants.getFloat("jumpForce");
        walkForceMagnitude = playerConstants.getFloat("walkForce");
    }


    public void update(float deltaTime){
        // NOTE: if you want to see which keys to press to move player, go to InputController class
        inputController.readInput();
        // we can consider putting player into a field and setting the pointer in loadWorld() but
        // for now, unless absolutely necessary, avoid blowing up the number of fields.
        Player player = gameWorld.getPlayer();
        if (inputController.jumpToggled()){
                player.applyImpulse(cache.set(0, jumpForceMagnitude));
        }
        float horizontal = inputController.getHorizontal();
        if (Math.abs(horizontal) > 0){
            // there is left/right movement (horizontal is either -1 or 1)
            // TODO (task): something is wrong with this, play the game and figure it out
            player.applyForce(cache.set(horizontal * walkForceMagnitude, 0));
            // TODO (task): modify input (simple)
        }
        postUpdate(deltaTime);
    }

    /**
     * at the end of each update loop, the game
     * @param deltaTime time spent in last game loop
     */
    private void postUpdate(float deltaTime){
        // (may not be necessary) update physics state of hit-boxes
        // TODO (later): method name needs renaming because not all game objects WILL be collidable.
        gameWorld.getWorld().step(1/60f, 6, 2);
        for (CollidableObject obj : gameWorld.getGameObjects()){
            obj.getHitBox().update(deltaTime);
        }
    }

    @Override
    public void beginContact(Contact contact) {
        System.out.println("touch me");
//        Player player = gameWorld.getPlayer();
//        Fixture a = contact.getFixtureA();
//        Fixture b = contact.getFixtureB();
//
//        if (a == null || b == null) return;
//
//        System.out.println(a.getBody().getUserData() + " " + b.getBody().getUserData());
    }

    @Override
    public void endContact(Contact contact) {
        Player player = gameWorld.getPlayer();
        Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();

        System.out.println(true);
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        // ignore for now
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        // usually ignorable
    }
}



