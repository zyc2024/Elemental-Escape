package com.elements.game.model;

/**
 * Scenery Objects do not have hit-boxes. They are purely visual objects.
 */
public abstract class SceneryObject extends GameObject {

    public enum SceneryType {
        STILL,
        ANIMATED
    }

    public abstract SceneryType getSceneryType();

}
