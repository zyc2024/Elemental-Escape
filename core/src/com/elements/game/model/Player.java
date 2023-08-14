package com.elements.game.model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.JsonValue;
import com.elements.game.utility.physics.PlayerHitBox;
import com.elements.game.visitors.GameObjectVisitor;

public class Player extends CollidableObject {

    public static final String GROUND_SENSOR_NAME = "playerSensor";

    private final PlayerHitBox hitBox;

    private boolean isGrounded;

    private int facing;

    public Player(JsonValue playerConstants, JsonValue playerData) {
        super(playerData.getFloat("width"), playerData.getFloat("height"));
        JsonValue hitBoxData = playerData.get("hit-box");
        float width = hitBoxData.getFloat("width", 0.5f);
        float height = hitBoxData.getFloat("height", 1);
        float x = playerData.getFloat("x", 0);
        float y = playerData.getFloat("y", 0);
        float density = playerConstants.getFloat("density", 1);
        float friction = playerConstants.getFloat("friction", 0);
        // mass, size, position, velocity, angular rotation are all in the hit box
        hitBox = new PlayerHitBox(x, y, width, height);
        hitBox.setDensity(density);
        hitBox.setFriction(friction);
        hitBox.setFixedRotation(true);  // helps keep the player standing upright
        hitBox.setBodyType(BodyDef.BodyType.DynamicBody);
        // assign name to sensor to help assist determining if we're on ground
        hitBox.setGroundSensorName(GROUND_SENSOR_NAME);
        // assign name to hit-box for debugging
        hitBox.setName("playerHitBox");
        facing = 1;
    }

    /**
     * applies the given force onto the player at the center of the player's body.
     *
     * @param force force vector
     */
    public void applyForce(Vector2 force) {
        // a Body (from package gdx.physics.box2d) is the object that receives forces, impulses,
        // changes to velocity, etc. The physics hit-box classes already provides methods to
        // update velocities, positions, etc. to shorten code. However, it does not provide
        // direct methods to apply forces because NOT all hit-boxes are subject to forces since
        // game borders never move.
        Body body = hitBox.getBody();
        // here: wake should be set to "true" so the hit-box remains active in the world. Box2D
        // engine optimizes by sleeping some physical objects that aren't moving.
        body.applyForce(force, hitBox.getPosition(), true);
    }

    /**
     * applies the given impulse onto the player at the center of the player's body.
     *
     * @param impulse undocumented
     */
    public void applyImpulse(Vector2 impulse) {
        Body body = hitBox.getBody();
        body.applyLinearImpulse(impulse, hitBox.getPosition(), true);
    }

    @Override
    public PlayerHitBox getHitBox() {
        return hitBox;
    }

    @Override
    public <V> V accept(GameObjectVisitor<V> v) {
        return v.visit(this);
    }

    /**
     * Gets whether the player is on the ground.
     *
     * @return on ground
     */
    public boolean isGrounded() {
        return isGrounded;
    }

    /**
     * Sets the player ground state.
     *
     * @param grounded is on ground
     */
    public void setGrounded(boolean grounded) {
        isGrounded = grounded;
    }

    /**
     * Gets the horizontal velocity of the player.
     *
     * @return horizontal velocity
     */
    public float getHorizontalVelocity() {
        return hitBox.getBody().getLinearVelocity().x;
    }

    /**
     * Gets the vertical velocity of the player.
     *
     * @return vertical velocity
     */
    public float getVerticalVelocity() { return hitBox.getBody().getLinearVelocity().y; }

    /**
     * Gets the direction the player is facing.
     * 1 for right
     * -1 for left
     *
     * @return facing
     */
    public int getFacing() { return facing; }

    /**
     * Sets player facing direction.
     *
     * @param face the direction to face
     */
    public void setFacing(int face) { facing = face; }
}