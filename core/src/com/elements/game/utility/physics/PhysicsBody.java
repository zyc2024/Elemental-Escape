package com.elements.game.utility.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

/**
 * Base model class to support collisions.<br> Instances represent at least one Box2D body and
 * bodies can be grouped together. <br> This class exists to wrap the body and fixture information
 * into a single interface.
 */
public abstract class PhysicsBody {
    /**
     * The physics body for Box2D.
     */
    protected Body body;

    /**
     * Stores the body information for this body
     */
    protected BodyDef bodyInfo;

    /**
     * Stores the fixture information for this shape
     */
    protected FixtureDef fixture;

    /**
     * The mass data of this shape (which may override the fixture)
     */
    protected MassData massInfo;

    /**
     * Whether to use the custom mass data
     */
    protected boolean massEffect;

    /**
     * A cache value for when the user wants to access the body position
     */
    protected Vector2 positionCache = new Vector2();

    /**
     * A cache value for when the user wants to access the linear velocity
     */
    protected Vector2 velocityCache = new Vector2();

    /**
     * A cache value for when the user wants to access the center of mass
     */
    protected Vector2 centroidCache = new Vector2();

    /**
     * A tag for debugging purposes
     */
    private String nameTag;

    /**
     * Whether the object should be removed from the world on next pass
     */
    private boolean toRemove;

    /**
     * Whether the object has changed shape and needs a new fixture
     */
    private boolean isDirty;

    /**
     * Create a new physics object at the origin.
     */
    protected PhysicsBody() {
        this(0, 0);
    }

    /**
     * Create a new physics object
     *
     * @param x Initial x position in world coordinates
     * @param y Initial y position in world coordinates
     */
    protected PhysicsBody(float x, float y) {
        // Object has yet to be deactivated
        toRemove = false;

        // Allocate the body information
        bodyInfo = new BodyDef();
        bodyInfo.awake = true;
        bodyInfo.allowSleep = true;
        bodyInfo.gravityScale = 1.0f;
        bodyInfo.position.set(x, y);
        bodyInfo.fixedRotation = false;
        // Objects are physics objects unless otherwise noted
        bodyInfo.type = BodyType.DynamicBody;

        // Allocate the fixture information
        fixture = new FixtureDef();

        // Allocate the mass information, but turn it off
        massEffect = false;
        massInfo = new MassData();

        // body initially null, until physics body activated by adding to
        // physics world
        body = null;
    }

    /**
     * Returns the body type for Box2D physics <br>
     * <ul>
     *     <li>Static: locks a body in place (e.g. a platform, wall)</li>
     *     <li>Kinematic: body responds to velocity movements</li>
     *     <li>Dynamic: body responds to forces and velocity</li>
     * </ul>
     *
     * @return the body type for Box2D physics
     */
    public BodyType getBodyType() {
        return body != null ? body.getType() : bodyInfo.type;
    }

    /**
     * Updates the body type for Box2D physics body
     */
    public void setBodyType(BodyType value) {
        if (body != null) {
            body.setType(value);
        } else {
            bodyInfo.type = value;
        }
    }

    /**
     * The same vector each time is returned so this cannot be used as a memory allocator.
     *
     * @return the current position for this physics body
     */
    public Vector2 getPosition() {
        return body != null ? body.getPosition() : positionCache.set(bodyInfo.position);
    }

    /**
     * Sets the current position for this physics body <br> This method does not keep a reference to
     * the vector parameter.
     *
     * @param value the current position for this physics body
     */
    public void setPosition(Vector2 value) {
        if (body != null) {
            body.setTransform(value, body.getAngle());
        } else {
            bodyInfo.position.set(value);
        }
    }

    /**
     * Sets the current position for this physics body
     *
     * @param x the x-coordinate for this physics body
     * @param y the y-coordinate for this physics body
     */
    public void setPosition(float x, float y) {
        if (body != null) {
            positionCache.set(x, y);
            body.setTransform(positionCache, body.getAngle());
        } else {
            bodyInfo.position.set(x, y);
        }
    }

    /**
     * @return the x-coordinate for this physics body
     */
    public float getX() {
        return body != null ? body.getPosition().x : bodyInfo.position.x;
    }

    /**
     * Sets the x-coordinate for this physics body
     *
     * @param value the x-coordinate for this physics body
     */
    public void setX(float value) {
        if (body != null) {
            positionCache.set(value, body.getPosition().y);
            body.setTransform(positionCache, body.getAngle());
        } else {
            bodyInfo.position.x = value;
        }
    }

    /**
     * @return the y-coordinate for this physics body
     */
    public float getY() {
        return body != null ? body.getPosition().y : bodyInfo.position.y;
    }

    /**
     * Sets the y-coordinate for this physics body
     *
     * @param value the y-coordinate for this physics body
     */
    public void setY(float value) {
        if (body != null) {
            positionCache.set(body.getPosition().x, value);
            body.setTransform(positionCache, body.getAngle());
        } else {
            bodyInfo.position.y = value;
        }
    }

    /**
     * The value returned is in radians
     *
     * @return the angle of rotation for this body (about the center of mass)
     */
    public float getAngle() {
        return (body != null ? body.getAngle() : bodyInfo.angle);
    }

    /**
     * Sets the angle of rotation for this body (about the center of mass).
     *
     * @param value the angle of rotation for this body (in radians)
     */
    public void setAngle(float value) {
        if (body != null) {
            body.setTransform(body.getPosition(), value);
        } else {
            bodyInfo.angle = value;
        }
    }

    /**
     * Returns the linear velocity for this physics body <br> The same vector each time is returned
     * so this cannot be used as a memory allocator.
     *
     * @return the linear velocity for this physics body
     */
    public Vector2 getLinearVelocity() {
        return body != null ? body.getLinearVelocity() : velocityCache.set(bodyInfo.linearVelocity);
    }

    /**
     * Sets the linear velocity for this physics body <br> This method does not keep a reference to
     * the vector parameter.
     *
     * @param value the linear velocity for this physics body
     */
    public void setLinearVelocity(Vector2 value) {
        if (body != null) {
            body.setLinearVelocity(value);
        } else {
            bodyInfo.linearVelocity.set(value);
        }

    }

    /**
     * @return the x-velocity for this physics body
     */
    public float getVX() {
        return (body != null ? body.getLinearVelocity().x : bodyInfo.linearVelocity.x);
    }

    /**
     * Sets the x-velocity for this physics body
     *
     * @param value the x-velocity for this physics body
     */
    public void setVX(float value) {
        if (body != null) {
            velocityCache.set(value, body.getLinearVelocity().y);
            body.setLinearVelocity(velocityCache);
        } else {
            bodyInfo.linearVelocity.x = value;
        }

    }

    /**
     * @return the y-velocity for this physics body
     */
    public float getVY() {
        return body != null ? body.getLinearVelocity().y : bodyInfo.linearVelocity.y;
    }

    /**
     * Sets the y-velocity for this physics body
     *
     * @param value the y-velocity for this physics body
     */
    public void setVY(float value) {
        if (body != null) {
            velocityCache.set(body.getLinearVelocity().x, value);
            body.setLinearVelocity(velocityCache);
        } else {
            bodyInfo.linearVelocity.y = value;
        }
    }

    /**
     * The rate of change is measured in radians per step
     *
     * @return the angular velocity for this physics body
     */
    public float getAngularVelocity() {

        return body != null ? body.getAngularVelocity() : bodyInfo.angularVelocity;
    }

    /**
     * Sets the angular velocity for this physics body
     *
     * @param value the angular velocity for this physics body (in radians)
     */
    public void setAngularVelocity(float value) {
        if (body != null) {
            body.setAngularVelocity(value);
        } else {
            bodyInfo.angularVelocity = value;
        }
    }

    /**
     * An inactive body not participate in collision or dynamics. This state is similar to sleeping
     * except the body will not be woken by other bodies and the body will not participate in
     * collisions, ray casts, etc.
     *
     * @return true if the body is active
     */
    public boolean isActive() {
        return (body != null ? body.isActive() : bodyInfo.active);
    }

    /**
     * Sets whether the body is active <br> See {@link #isActive()} for description of
     * Inactive/active state.
     *
     * @param value whether the body is active
     */
    public void setActive(boolean value) {

        if (body != null) {
            body.setActive(value);
        } else {
            bodyInfo.active = value;
        }

    }

    /**
     * A sleeping body is one that has come to rest and the physics engine has decided to stop
     * simulating it to save CPU cycles. If a body is awake and collides with a sleeping body, then
     * the sleeping body wakes up. Bodies will also wake up if a joint or contact attached to them
     * is destroyed. You can also wake a body manually.
     *
     * @return true if the body is awake
     */
    public boolean isAwake() {
        return (body != null ? body.isAwake() : bodyInfo.awake);
    }

    /**
     * Sets whether the body is awake <br> See {@link #isAwake()} for description of sleeping
     * state.
     *
     * @param value whether the body is awake
     */
    public void setAwake(boolean value) {
        if (body != null) {
            body.setAwake(value);
        } else {
            bodyInfo.awake = value;
        }
    }

    /**
     * @return false if this body should never fall asleep
     */
    public boolean isSleepingAllowed() {
        return (body != null ? body.isSleepingAllowed() : bodyInfo.allowSleep);
    }

    /**
     * Sets whether the body should ever fall asleep
     *
     * @param value whether the body should ever fall asleep
     */
    public void setSleepingAllowed(boolean value) {
        if (body != null) {
            body.setSleepingAllowed(value);
        } else {
            bodyInfo.allowSleep = value;
        }
    }

    /**
     * By default, Box2D uses continuous collision detection (CCD) to prevent dynamic bodies from
     * tunneling through static bodies. Normally CCD is not used between dynamic bodies. This is
     * done to keep performance reasonable. In some game scenarios you need dynamic bodies to use
     * CCD. For example, you may want to shoot a high speed bullet at a stack of dynamic bricks.
     * Without CCD, the bullet might tunnel through the bricks. <br> Fast moving objects in Box2D
     * can be labeled as bullets. Bullets will perform CCD with both static and dynamic bodies.
     *
     * @return true if this body is a bullet
     */
    public boolean isBullet() {
        return (body != null ? body.isBullet() : bodyInfo.bullet);
    }

    /**
     * Sets whether this body is a bullet <br> See {@link #isBullet()} for description of bullet
     * body
     *
     * @param value whether this body is a bullet
     */
    public void setBullet(boolean value) {
        if (body != null) {
            body.setBullet(value);
        } else {
            bodyInfo.bullet = value;
        }
    }

    /**
     * This is very useful for characters that should remain upright.
     *
     * @return true if this body will be prevented from rotating
     */
    public boolean isFixedRotation() {
        return (body != null ? body.isFixedRotation() : bodyInfo.fixedRotation);
    }

    /**
     * Sets whether this body be prevented from rotating
     *
     * @param value whether this body be prevented from rotating
     */
    public void setFixedRotation(boolean value) {
        if (body != null) {
            body.setFixedRotation(value);
        } else {
            bodyInfo.fixedRotation = value;
        }
    }

    /**
     * @return the gravity scale to apply to this body
     */
    public float getGravityScale() {
        return (body != null ? body.getGravityScale() : bodyInfo.gravityScale);
    }

    /**
     * Sets the gravity scale to apply to this body <br> Be careful with this, since increased
     * gravity can decrease stability.
     *
     * @param value the gravity scale to apply to this body
     */
    public void setGravityScale(float value) {
        if (body != null) {
            body.setGravityScale(value);
        } else {
            bodyInfo.gravityScale = value;
        }
    }

    /**
     * Linear damping is used to reduce the linear velocity. Damping is different from friction
     * because friction only occurs upon contact. <br> Damping parameters should be between 0 and
     * infinity, with 0 meaning no damping and infinity meaning full damping. Normally you will use
     * a damping value between 0 and 0.1. <br> Linear damping is usually avoided because it makes
     * bodies look floaty.
     *
     * @return the linear damping for this body.
     */
    public float getLinearDamping() {
        return body != null ? body.getLinearDamping() : bodyInfo.linearDamping;
    }

    /**
     * Sets the linear damping for this body. <br> See {@link #getLinearDamping()} for description
     * of Linear Dampening
     *
     * @param value the linear damping for this body.
     */
    public void setLinearDamping(float value) {
        if (body != null) {
            body.setLinearDamping(value);
        } else {
            bodyInfo.linearDamping = value;
        }
    }

    /**
     * Angular damping is used to reduce the angular velocity. <br> Damping parameters should be
     * between 0 and infinity, with 0 meaning no damping, and infinity meaning full damping.
     * Normally you will use a damping value between 0 and 0.1.
     *
     * @return the angular damping for this body.
     */
    public float getAngularDamping() {
        return body != null ? body.getAngularDamping() : bodyInfo.angularDamping;
    }

    /**
     * Sets the angular damping for this body. <br> See {@link #getAngularDamping()} for description
     * of Angular Dampening
     *
     * @param value the angular damping for this body.
     */
    public void setAngularDamping(float value) {
        if (body != null) {
            body.setAngularDamping(value);
        } else {
            bodyInfo.angularDamping = value;
        }
    }

    /**
     * Copies the state from the given body to the body definition. <br> This is important if you
     * want to save the state of the body before removing it from the world.
     */
    protected void setBodyState(Body body) {
        bodyInfo.type = body.getType();
        bodyInfo.angle = body.getAngle();
        bodyInfo.active = body.isActive();
        bodyInfo.awake = body.isAwake();
        bodyInfo.bullet = body.isBullet();
        bodyInfo.position.set(body.getPosition());
        bodyInfo.linearVelocity.set(body.getLinearVelocity());
        bodyInfo.allowSleep = body.isSleepingAllowed();
        bodyInfo.fixedRotation = body.isFixedRotation();
        bodyInfo.gravityScale = body.getGravityScale();
        bodyInfo.angularDamping = body.getAngularDamping();
        bodyInfo.linearDamping = body.getLinearDamping();
    }

    /**
     * The density is typically measured in usually in kg/m^2. The density can be zero or positive.
     * You should generally use similar densities for all your fixtures. This will improve stacking
     * stability.
     *
     * @return the density of this body
     */
    public float getDensity() {
        return fixture.density;
    }

    /**
     * Sets the density of this body
     *
     * @param value the density of this body
     */
    public void setDensity(float value) {
        fixture.density = value;
        if (body != null) {
            for (Fixture f : body.getFixtureList()) {
                f.setDensity(value);
            }
        }
    }

    /**
     * @return the friction coefficient of this body
     */
    public float getFriction() {
        return fixture.friction;
    }

    /**
     * Sets the friction coefficient of this body <br> The friction parameter is usually set between
     * 0 and 1, but can be any non-negative value. A friction value of 0 turns off friction and a
     * value of 1 makes the friction strong. <br> When the friction force is  computed between two
     * shapes, Box2D must combine the friction parameters of the two parent fixtures. This is done
     * with the geometric mean.
     *
     * @param value the friction coefficient of this body
     */
    public void setFriction(float value) {
        fixture.friction = value;
        if (body != null) {
            for (Fixture f : body.getFixtureList()) {
                f.setFriction(value);
            }
        }
    }

    /**
     * @return the restitution of this body
     */
    public float getRestitution() {
        return fixture.restitution;
    }

    /**
     * Sets the restitution of this body <br> Restitution is used to make objects bounce. The
     * restitution value is usually set to be between 0 and 1. Consider dropping a ball on a table.
     * A value of zero means the ball won't bounce. This is called an inelastic collision. A value
     * of one means the ball's velocity will be exactly reflected. This is called a perfectly
     * elastic collision.
     *
     * @param value the restitution of this body
     */
    public void setRestitution(float value) {
        fixture.restitution = value;
        if (body != null) {
            for (Fixture f : body.getFixtureList()) {
                f.setRestitution(value);
            }
        }
    }

    /**
     * @return true if this object is a sensor.
     */
    public boolean isSensor() {
        return fixture.isSensor;
    }

    /**
     * Sets whether this object is a sensor. <br> Sometimes game logic needs to know when two
     * entities overlap yet there should be no collision response. This is done by using sensors. A
     * sensor is an entity that detects collision but does not produce a response.
     *
     * @param value whether this object is a sensor.
     */
    public void setSensor(boolean value) {
        fixture.isSensor = value;
        if (body != null) {
            for (Fixture f : body.getFixtureList()) {
                f.setSensor(value);
            }
        }
    }

    /**
     * @return the filter data for this object (or null if there is none)
     */
    public Filter getFilterData() {
        return fixture.filter;
    }

    /**
     * Sets the filter data for this object <br> Collision filtering allows you to prevent collision
     * between fixtures. For example, say you make a character that rides a bicycle. You want the
     * bicycle to collide with the terrain and the character to collide with the terrain, but you
     * don't want the character to collide with the bicycle (because they must overlap). <br> Box2D
     * supports such collision filtering using categories and groups. <br>
     *
     * @param value the filter data for this object, A value of null removes all collision filters.
     */
    public void setFilterData(Filter value) {
        if (value != null) {
            fixture.filter.categoryBits = value.categoryBits;
            fixture.filter.groupIndex = value.groupIndex;
            fixture.filter.maskBits = value.maskBits;
        } else {
            fixture.filter.categoryBits = 0x0001;
            fixture.filter.groupIndex = 0;
            fixture.filter.maskBits = -1;
        }
        if (body != null) {
            for (Fixture f : body.getFixtureList()) {
                f.setFilterData(value);
            }
        }
    }

    /**
     * Returns the center of mass of this body <br> This method returns the same  vector each time
     * so this cannot be used as a memory allocator.
     *
     * @return the center of mass for this physics body
     */
    public Vector2 getCentroid() {
        return (body != null ? body.getLocalCenter() : centroidCache.set(massInfo.center));
    }

    /**
     * Sets the center of mass for this physics body <br> This method does not keep a reference to
     * the vector parameter.
     *
     * @param value the center of mass for this physics body
     */
    public void setCentroid(Vector2 value) {
        if (!massEffect) {
            massEffect = true;
            massInfo.I = getInertia();
            massInfo.mass = getMass();
        }
        massInfo.center.set(value);
        if (body != null) {
            body.setMassData(massInfo);
        }
    }

    /**
     * @return the rotational inertia of this body
     */
    public float getInertia() {
        return body != null ? body.getInertia() : massInfo.I;
    }

    /**
     * Sets the rotational inertia of this body <br> For static bodies, the mass and rotational
     * inertia are set to zero. When a body has fixed rotation, its rotational inertia is zero.
     *
     * @param value the rotational inertia of this body
     */
    public void setInertia(float value) {
        if (!massEffect) {
            massEffect = true;
            massInfo.center.set(getCentroid());
            massInfo.mass = getMass();
        }
        massInfo.I = value;
        if (body != null) {
            body.setMassData(massInfo); // Protected accessor?
        }
    }

    /**
     * @return the mass of this body
     */
    public float getMass() {
        return (body != null ? body.getMass() : massInfo.mass);
    }

    /**
     * Sets the mass of this body <br> The value is usually in kilograms.
     *
     * @param value the mass of this body
     */
    public void setMass(float value) {
        if (!massEffect) {
            massEffect = true;
            massInfo.center.set(getCentroid());
            massInfo.I = getInertia();
        }
        massInfo.mass = value;
        if (body != null) {
            body.setMassData(massInfo);
        }
    }

    /**
     * Resets this body to use the mass computed from its shape and density
     */
    public void resetMass() {
        massEffect = false;
        if (body != null) {
            body.resetMassData();
        }
    }

    /**
     * @return true if our object has been flagged for garbage collection
     */
    public boolean isRemoved() {
        return toRemove;
    }

    /**
     * Sets whether our object has been flagged for garbage collection <br> Garbage collected object
     * will be removed from the physics world at the next time step.
     *
     * @param value whether our object has been flagged for garbage collection
     */
    public void markRemoved(boolean value) {
        toRemove = value;
    }

    /**
     * @return true if the shape information must be updated.
     */
    public boolean isDirty() {
        return isDirty;
    }


    /**
     * Sets whether the shape information must be updated. <br> Attributes tied to the geometry (and
     * not just forces/position) must wait for collisions to complete before they are reset. Shapes
     * (and their properties) are reset in the update method.
     *
     * @param value whether the shape information must be updated.
     */
    public void markDirty(boolean value) {
        isDirty = value;
    }

    /**
     * @return the Box2D body for this object.
     */
    public Body getBody() {
        return body;
    }

    /**
     * @return the physics object tag.
     */
    public String getName() {
        return nameTag;
    }

    /**
     * Sets the physics object tag. <br> A tag is a string attached to an object, in order to
     * identify it in debugging.
     *
     * @param value the physics object tag
     */
    public void setName(String value) {
        nameTag = value;
    }

    // Physics Initialization

    /**
     * Creates the physics Body(s) for this object, adding them to the world.
     *
     * @param world    Box2D world to store body
     * @param userData object to act as listener to physics collisions
     * @return true if object allocation succeeded
     */
    public boolean activatePhysics(World world, Object userData) {
        // Make a body, if possible
        bodyInfo.active = true;
        body = world.createBody(bodyInfo);
        body.setUserData(userData);

        // Only initialize if a body was created.
        if (body != null) {
            createFixtures();
            return true;
        }

        bodyInfo.active = false;
        return false;
    }

    /**
     * Creates the physics Body(s) for this object, adding them to the world.
     *
     * @param world Box2D world to store body
     * @return true if object allocation succeeded
     */
    public boolean activatePhysics(World world) {
        return activatePhysics(world, this);
    }

    /**
     * Destroys the physics Body(s) of this object if applicable, removing them from the world.
     *
     * @param world Box2D world that stores body
     */
    public void deactivatePhysics(World world) {
        // Should be good for most (simple) applications.
        if (body != null) {
            // Snapshot the values
            setBodyState(body);
            world.destroyBody(body);
            body = null;
            bodyInfo.active = false;
        }
    }

    /**
     * Create new fixtures for this body, defining the shape <br>
     */
    protected abstract void createFixtures();

    /**
     * Release the fixtures for this body, resetting the shape <br>
     */
    protected abstract void releaseFixtures();

    /**
     * Updates the body's physics state. This method should be called after collisions have been
     * resolved. Collisions may result in changes to physics geometry so fixtures need to be
     * updated.
     *
     * @param delta Timing values from game loop
     */
    public void update(float delta) {
        // Recreate the fixture object if dimensions changed.
        if (isDirty()) {
            createFixtures();
        }
    }
}