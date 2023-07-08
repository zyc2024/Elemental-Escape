package com.elements.game.model;

import com.elements.game.utility.physics.BoxPhysicsBody;
import com.elements.game.utility.physics.PhysicsBody;

public class Player {

    private final PhysicsBody hitbox;

    boolean isJumping;

    public Player(float width, float height) {
        // mass, size, position, velocity, angular rotation are all in the hitbox
        hitbox = new BoxPhysicsBody(width, height);
    }
}