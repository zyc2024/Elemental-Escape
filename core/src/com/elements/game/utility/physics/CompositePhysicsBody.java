package com.elements.game.utility.physics;

import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

/**
 * Composite model class to support collisions. <br>
 * CompositePhysicsBody instances are built of one or many bodies, and can be
 * connected by joints. This is the class to use for chains, ropes, levers,
 * and so on. This class does not provide Shape information. <br>
 * CompositePhysicsBody has a root body which may or may not be attached to
 * the other bodies that make up this model. All physics methods in this
 * class apply to the root. To move the other bodies, they should either be
 * iterated over directly, or attached to the root via a joint.
 */
public abstract class CompositePhysicsBody extends PhysicsBody {

    /**
     * A complex physics object has multiple bodies
     */
    protected Array<PhysicsBody> physicsBodies;

    /**
     * Potential joints for connecting the multiple bodies
     */
    protected Array<Joint> joints;

    /// Physics Bodies

    /**
     * While the iterable does not allow you to modify the list, it is
     * possible to modify the individual objects.
     *
     * @return the collection of component physics bodies.
     */
    public Iterable<PhysicsBody> getBodies() {
        return physicsBodies;
    }

    /**
     * While the iterable does not allow you to modify the list, it is
     * possible to modify the individual joints.
     *
     * @return the collection of joints for this object (this may be empty).
     */
    public Iterable<Joint> getJoints() {
        return joints;
    }

    /**
     * Creates a new complex physics object at the origin.
     */
    protected CompositePhysicsBody() {
        this(0, 0);
    }

    /**
     * Creates a new complex physics object <br>
     * The position is the position of the root object.
     *
     * @param x Initial x position in world coordinates
     * @param y Initial y position in world coordinates
     */
    protected CompositePhysicsBody(float x, float y) {
        super(x, y);
        physicsBodies = new Array<>();
        joints = new Array<>();
    }

    /**
     * Creates the bodies for this object, adding them to the world. <br>
     * This method invokes ActivatePhysics for the individual Physics Bodies
     * in the list. It also calls the internal method
     * {@link #createJoints(World)} to link them all together. The
     * method {@link #createJoints(World)} must be implemented.
     *
     * @param world Box2D world to store body
     * @return true if object allocation succeeded
     */
    @Override
    public boolean activatePhysics(World world) {
        bodyInfo.active = true;
        boolean success = true;

        // Create all other bodies.
        for (PhysicsBody obj : physicsBodies) {
            success = success && obj.activatePhysics(world);
        }
        success = success && createJoints(world);

        // Clean up if we failed
        if (!success) {
            deactivatePhysics(world);
        }
        return success;
    }

    @Override
    public void deactivatePhysics(World world) {
        if (bodyInfo.active) {
            // Should be good for most (simple) applications.
            for (Joint joint : joints) {
                world.destroyJoint(joint);
            }
            joints.clear();
            for (PhysicsBody obj : physicsBodies) {
                obj.deactivatePhysics(world);
            }
            bodyInfo.active = false;
        }
    }

    @Override
    protected void createFixtures() {
        // do nothing, the needed fixtures are created through the physics activation of each of
        // the physics bodies.
    }

    @Override
    protected void releaseFixtures() {
        for (PhysicsBody obj : physicsBodies) {
            obj.releaseFixtures();
        }
    }

    /**
     * Creates the joints for this object. <br>
     * This method is executed as part of activePhysics. This method must be
     * implemented for proper initialization of composite physics bodies.
     *
     * @param world Box2D world to store joints
     * @return true if object allocation succeeded
     */
    protected abstract boolean createJoints(World world);

}
