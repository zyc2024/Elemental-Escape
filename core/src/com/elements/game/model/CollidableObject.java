package com.elements.game.model;

import com.badlogic.gdx.math.Vector2;
import com.elements.game.utility.physics.PhysicsBody;

/**
 * A Collidable Object is a {@link GameObject} with a {@link PhysicsBody} (hit-box).
 */
public abstract class CollidableObject extends GameObject {

    public CollidableObject(float displayWidth, float displayHeight){
        super(DEFAULT_Z_INDEX, displayWidth, displayHeight);
    }

    public CollidableObject(int z_index, float displayWidth, float displayHeight){
        super(z_index, displayWidth, displayHeight);
    }

    public abstract PhysicsBody getHitBox();

    @Override
    public float getX() {
        return getHitBox().getX();
    }

    @Override
    public float getY() {
        return getHitBox().getY();
    }

    @Override
    public float getAngle() {
        return getHitBox().getAngle();
    }
}
