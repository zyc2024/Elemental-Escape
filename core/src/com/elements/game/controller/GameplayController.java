package com.elements.game.controller;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectSet;
import com.elements.game.model.BlockPlatform;
import com.elements.game.model.CollidableObject;
import com.elements.game.model.GameWorld;
import com.elements.game.model.Player;
import com.badlogic.gdx.physics.box2d.*;
import sun.jvm.hotspot.opto.Block;

import java.util.HashMap;
import java.util.List;

public class GameplayController implements ContactListener {

    private JsonValue gameConstants;

    /** reference to the world object */
    private GameWorld gameWorld;

    private Player player;

    /** controller to read gameplay inputs */
    private final InputController inputController;

    /** vector cache to be used for computations */
    private final Vector2 cache;

    private float jumpForceMagnitude;
    private float walkForceMagnitude;

    private ObjectSet<Fixture> groundSensorContacts;


    public GameplayController(){
        inputController = new InputController();
        cache = new Vector2();
        groundSensorContacts = new ObjectSet<>();
    }

    /**
     * loads game components from the given world.
     * @param gameWorld game world reference
     */
    public void setWorldComponents(GameWorld gameWorld){
        // grab what we need to CONTROL
        this.gameWorld = gameWorld;
        gameWorld.getWorld().setContactListener(this);
        this.gameConstants = gameWorld.getGameConstants();
        JsonValue playerConstants = gameConstants.get("player");
        jumpForceMagnitude = playerConstants.getFloat("jumpForce");
        walkForceMagnitude = playerConstants.getFloat("walkForce");
        player = gameWorld.getPlayer();
    }


    public void update(float deltaTime){
        // NOTE: if you want to see which keys to press to move player, go to InputController class
        inputController.readInput();
        if (inputController.jumpToggled() && player.isGrounded()){
            player.applyImpulse(cache.set(0, jumpForceMagnitude));
        }

        float horizontal = inputController.getHorizontal();
        if (Math.abs(horizontal) > 0){
            // there is left/right movement (horizontal is either -1 or 1)
            player.applyForce(cache.set(horizontal * walkForceMagnitude, 0));
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
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        Object fixDataA = fixtureA.getUserData();
        Object fixDataB = fixtureB.getUserData();
        Body bodyA = fixtureA.getBody();
        Body bodyB = fixtureB.getBody();
        CollidableObject objectA = (CollidableObject) bodyA.getUserData();
        CollidableObject objectB = (CollidableObject) bodyB.getUserData();

//        Player playerObj = player.equals(objectA) ? (Player) objectA : null;
//        playerObj = player.equals(objectB) ? (Player) objectB : playerObj;

        BlockPlatform platform = objectA instanceof BlockPlatform ? (BlockPlatform) objectA : null;
        platform = objectB instanceof BlockPlatform ? (BlockPlatform) objectB : platform;

        boolean isPlayerSensor =
                fixDataA == Player.GROUND_SENSOR_NAME || fixDataB == Player.GROUND_SENSOR_NAME;
        // our feet touched a platform
        // sensor is either one of fixtureA or fixture B, which implies the other one is the
        // platform fixture.
        if (isPlayerSensor && platform != null) {
            player.setGrounded(true);
            groundSensorContacts.add(fixDataA == Player.GROUND_SENSOR_NAME ? fixtureB : fixtureA );
        }
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        Object fixDataA = fixtureA.getUserData();
        Object fixDataB = fixtureB.getUserData();
        Body bodyA = fixtureA.getBody();
        Body bodyB = fixtureB.getBody();
        CollidableObject objectA = (CollidableObject) bodyA.getUserData();
        CollidableObject objectB = (CollidableObject) bodyB.getUserData();

        BlockPlatform platform = objectA instanceof BlockPlatform ? (BlockPlatform) objectA : null;
        platform = objectB instanceof BlockPlatform ? (BlockPlatform) objectB : platform;

        boolean isPlayerSensor =
                fixDataA == Player.GROUND_SENSOR_NAME || fixDataB == Player.GROUND_SENSOR_NAME;
        // our feet touched a platform
        if (isPlayerSensor && platform != null) {
            // if player's sensor moves way from a part of platform, remove platform (fixture) from
            // SET
            groundSensorContacts.remove(fixDataA == Player.GROUND_SENSOR_NAME ? fixtureB :
                                               fixtureA );
            if (groundSensorContacts.size == 0){
                player.setGrounded(false);
            }
        }
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



