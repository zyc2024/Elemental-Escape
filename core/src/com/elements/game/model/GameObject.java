package com.elements.game.model;

import com.badlogic.gdx.math.Vector2;
import com.elements.game.visitors.GameObjectVisitor;

/**
 * A Game Object is an object in the game world. It maintains dynamic attributes (position, size,
 * z-index, rotational angle) for rendering.
 */
public abstract class GameObject {

    /**
     * in single layer game, all objects belong to the same layer. This ID is reserved for that
     * particular scenario.
     */
    public static final int DEFAULT_Z_INDEX = 0;

    /**
     * z-index is an integer specifying the layer that the object appears on.
     */
    private final int z_index;

    private final Vector2 displayDimensions;

    private final Vector2 cache;

    public GameObject(int z_index, float displayWidth, float displayHeight) {
        this(z_index);
        this.displayDimensions.set(displayWidth, displayHeight);
    }

    public GameObject(int z_index) {
        this.z_index = z_index;
        displayDimensions = new Vector2();
        cache = new Vector2();
    }

    public GameObject() {
        this(DEFAULT_Z_INDEX);
    }

    public abstract float getX();

    public abstract float getY();

    public abstract float getAngle();

    /**
     * The same vector is returned each time so this cannot be used as a memory allocator.
     *
     * @return vector cache containing the object's rendering size expressed in game units.
     */
    public Vector2 getDisplayDimensions() {
        return cache.set(displayDimensions);
    }


    /**
     * game is broken up into layers (foreground, background, middle-ground comes to mind). In a
     * multilayer game, objects are placed in layers despite no active "z" coordinate is involved.
     *
     * @return the z-index of the layer in which this game object is located.
     */
    public int getZIndex() {
        return z_index;
    }

    /**
     * The act of accepting a visitor is to apply the underlying function specified in the visitor
     * to this entity. This allows common functionalities to be extracted into one class and
     * extensions (new methods) can be introduced without modifying all classes in the GameObject
     * hierarchy.
     *
     * @param v   the incoming visitor
     * @param <V> abstract return type of visitor
     * @return output data from application of visitor
     */
    public abstract <V> V accept(GameObjectVisitor<V> v);
}
