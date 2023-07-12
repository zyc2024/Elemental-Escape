package com.elements.game.model;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.JsonValue;
import com.elements.game.utility.physics.BoxPhysicsBody;
import com.elements.game.utility.physics.PhysicsBody;
import com.elements.game.visitors.GameObjectVisitor;

public class BlockPlatform extends CollidableObject{

    private final BoxPhysicsBody hitBox;

    public BlockPlatform(JsonValue constants, JsonValue data, String objectNameTag){
        super(data.getFloat("width"), data.getFloat("height"));
        JsonValue hitBoxData = data.get("hit-box");
        float width = hitBoxData.getFloat("width");
        float height = hitBoxData.getFloat("height");
        float x = data.getFloat("x");
        float y = data.getFloat("y");
        hitBox = new BoxPhysicsBody(x,y, width, height);
        // this platform does not move
        hitBox.setBodyType(BodyDef.BodyType.StaticBody);
        hitBox.setDensity(constants.getFloat("density", 0.0f));
        hitBox.setFriction(constants.getFloat("friction", 0.0f));
        hitBox.setRestitution(constants.getFloat("restitution", 0.0f));
        hitBox.setName(objectNameTag);
    }

    @Override
    public BoxPhysicsBody getHitBox() {
        return hitBox;
    }

    @Override
    public <V> V accept(GameObjectVisitor<V> v) {
        return v.visit(this);
    }
}
