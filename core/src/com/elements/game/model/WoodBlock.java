package com.elements.game.model;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.JsonValue;
import com.elements.game.visitors.GameObjectVisitor;

public class WoodBlock extends BlockPlatform {
    public WoodBlock(JsonValue constants, JsonValue data, String objectNameTag) {
        super(constants, data, objectNameTag);

        hitBox.setBodyType(BodyDef.BodyType.DynamicBody);
        hitBox.setDensity(constants.getFloat("density", 1.0f));
        hitBox.setFriction(constants.getFloat("friction", 0.4f));
        hitBox.setRestitution(constants.getFloat("restitution", 0.1f));
        hitBox.setName(objectNameTag);
    }

    @Override
    public <V> V accept(GameObjectVisitor<V> v) {
        return v.visit(this);
    }
}
