package com.elements.game.utility.physics;

import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;

/**
 * Circle-shaped model to support collisions (non-hollow). <br>
 * Unless otherwise specified, the center of mass is as the center.
 */
public class CirclePhysicsBody extends PhysicsBody {
    /**
     * Shape information for this circle
     */
    protected CircleShape shape;
    /**
     * A cache value for the fixture (for resizing)
     */
    private Fixture geometry;

    /**
     * @return the radius of this circled body
     */
    public float getRadius() {
        return shape.getRadius();
    }

    /**
     * Sets the radius of this circle
     *
     * @param value the radius of this circle
     */
    public void setRadius(float value) {
        shape.setRadius(value);
        markDirty(true);
    }

    /**
     * Creates a new circle at the origin.
     *
     * @param radius The circle radius
     */
    public CirclePhysicsBody(float radius) {
        this(0, 0, radius);
    }

    /**
     * Creates a new circle body
     *
     * @param x      Initial x position of the circle center
     * @param y      Initial y position of the circle center
     * @param radius The circle radius
     */
    public CirclePhysicsBody(float x, float y, float radius) {
        super(x, y);
        shape = new CircleShape();
        shape.setRadius(radius);
    }

    @Override
    protected void createFixtures() {
        if (body == null) {
            return;
        }

        releaseFixtures();

        // Create the fixture
        fixture.shape = shape;
        geometry = body.createFixture(fixture);
        markDirty(false);
    }

    @Override
    protected void releaseFixtures() {
        if (geometry != null) {
            body.destroyFixture(geometry);
            geometry = null;
        }
    }
}