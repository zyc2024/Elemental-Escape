package com.elements.game.utility.physics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.elements.game.view.GameCanvas;

/**
 * A player hit-box is a custom capsule-shaped physics body. Usually a player is represented in the
 * shape of a capsule with a rectangular sensor attached to the base for collision detection with
 * ground.
 */
public class PlayerHitBox extends CapsulePhysicsBody {

    private static final float DEFAULT_SENSOR_WIDTH_RATIO = 0.5f;

    private static final float DEFAULT_SENSOR_HEIGHT = 0.04f;

    protected PolygonShape groundSensorShape;

    private Fixture groundSensorFixture;

    private String groundSensorName;

    private float groundSensorWidthRatio;

    private float groundSensorHeight;

    /**
     * Creates a new vertical player hit-box at the given position and with the given dimensions.
     * The size is expressed in physics units.
     *
     * @param x      Initial x position of the box center
     * @param y      Initial y position of the box center
     * @param width  The object width in physics units
     * @param height The object width in physics units
     */
    public PlayerHitBox(float x, float y, float width, float height) {
        super(x, y, width, height, Orientation.VERTICAL);
        groundSensorWidthRatio = DEFAULT_SENSOR_WIDTH_RATIO;
        groundSensorHeight = DEFAULT_SENSOR_HEIGHT;
    }

    public void setGroundSensorName(String groundSensorName) {
        this.groundSensorName = groundSensorName;
        if (groundSensorFixture != null) {
            // sensors can detect collision, we use the sensor name as its data to check which
            // entity the sensor belongs to.
            groundSensorFixture.setUserData(this.groundSensorName);
        }
    }

    /**
     * the ground sensor's actual height is a percentage of the player's height.
     *
     * @param groundSensorHeight a ratio from interval (0,1]
     */
    public void setGroundSensorHeight(float groundSensorHeight) {
        this.groundSensorHeight = groundSensorHeight;
        if (groundSensorFixture != null) {
            markDirty(true);
        }
    }

    /**
     * the ground sensor's actual width is a percentage of the player's width.
     *
     * @param groundSensorWidthRatio a ratio from interval (0,1]
     */
    public void setGroundSensorWidthRatio(float groundSensorWidthRatio) {
        this.groundSensorWidthRatio = groundSensorWidthRatio;
        if (groundSensorFixture != null) {
            markDirty(true);
        }
    }

    @Override
    protected void createFixtures() {
        super.createFixtures();
        if (body == null) {
            return;
        }
        // Ground Sensor
        // -------------
        // To determine whether the player is on the ground,
        // we create a thin sensor under the feet, which reports
        // collisions with the world but has no collision response.
        Vector2 sensorCenter = new Vector2(0, -getHeight() / 2 + groundSensorHeight / 1.5F);
        FixtureDef sensorDef = new FixtureDef();
        sensorDef.density = getDensity();
        sensorDef.isSensor = true;
        groundSensorShape = new PolygonShape();
        groundSensorShape.setAsBox(groundSensorWidthRatio * getWidth() / 2.0f, groundSensorHeight,
                                   sensorCenter, 0.0f);
        sensorDef.shape = groundSensorShape;

        // Ground sensor to represent our feet
        groundSensorFixture = body.createFixture(sensorDef);
        groundSensorFixture.setUserData(groundSensorName);
    }

    @Override
    protected void releaseFixtures() {
        super.releaseFixtures();
        if (groundSensorFixture != null) {
            body.destroyFixture(groundSensorFixture);
            groundSensorFixture = null;
        }
    }

    @Override
    public void debug(GameCanvas canvas, Vector2 drawScale) {
        super.debug(canvas, drawScale);
        canvas.drawPhysics(groundSensorShape, Color.GREEN, getX(), getY(), getAngle(), drawScale.x,
                           drawScale.y);
    }
}