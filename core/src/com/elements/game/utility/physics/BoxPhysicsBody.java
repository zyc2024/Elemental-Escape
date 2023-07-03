package com.elements.game.utility.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;

/**
 * Box-shaped model to support collisions. <br>
 * Unless otherwise specified, the center of mass is the center of box.
 */
public class BoxPhysicsBody extends PhysicsBody {
    /**
     * Shape information for this box
     */
    protected PolygonShape shape;

    /**
     * The width and height of the box
     */
    private final Vector2 dimension;

    /**
     * A cache value for when the user wants to access the dimensions
     */
    private final Vector2 sizeCache;

    /**
     * A cache value for the fixture (for resizing)
     */
    private Fixture geometry;

    /**
     * Cache of the polygon vertices (for resizing)
     */
    private final float[] vertices;

    /**
     * This cannot be used as a memory allocator.
     *
     * @return the dimensions of this box
     */
    public Vector2 getDimension() {
        return sizeCache.set(dimension);
    }

    /**
     * Sets the dimensions of this box. <br>
     * This method does not keep a reference to the vector parameter.
     *
     * @param value the dimensions of this box
     */
    public void setDimension(Vector2 value) {
        setDimension(value.x, value.y);
    }

    /**
     * Sets the dimensions of this box
     *
     * @param width  The width of this box
     * @param height The height of this box
     */
    public void setDimension(float width, float height) {
        dimension.set(width, height);
        markDirty(true);
        resize(width, height);
    }

    /**
     * @return the box width
     */
    public float getWidth() {
        return dimension.x;
    }

    /**
     * Sets the box width
     *
     * @param value the box width
     */
    public void setWidth(float value) {
        sizeCache.set(value, dimension.y);
        setDimension(sizeCache);
    }

    /**
     * @return the box height
     */
    public float getHeight() {
        return dimension.y;
    }

    /**
     * Sets the box height
     *
     * @param value the box height
     */
    public void setHeight(float value) {
        sizeCache.set(dimension.x, value);
        setDimension(sizeCache);
    }

    /**
     * Creates a new box at the origin.
     *
     * @param width  The object width in physics units
     * @param height The object width in physics units
     */
    public BoxPhysicsBody(float width, float height) {
        this(0, 0, width, height);
    }

    /**
     * Creates a new box object.
     *
     * @param x      Initial x position of the box center
     * @param y      Initial y position of the box center
     * @param width  The object width in physics units
     * @param height The object width in physics units
     */
    public BoxPhysicsBody(float x, float y, float width, float height) {
        super(x, y);
        dimension = new Vector2(width, height);
        sizeCache = new Vector2();
        shape = new PolygonShape();
        vertices = new float[8];
        geometry = null;

        // Initialize
        resize(width, height);
    }

    /**
     * Reset the polygon vertices in the shape to match the dimension.
     */
    private void resize(float width, float height) {
        // Make the box with the center of mass in the center of rectangle
        vertices[0] = -width / 2.0f;
        vertices[1] = -height / 2.0f;
        vertices[2] = -width / 2.0f;
        vertices[3] = height / 2.0f;
        vertices[4] = width / 2.0f;
        vertices[5] = height / 2.0f;
        vertices[6] = width / 2.0f;
        vertices[7] = -height / 2.0f;
        shape.set(vertices);
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