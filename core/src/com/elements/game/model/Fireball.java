package com.elements.game.model;

import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.JsonValue;
import com.elements.game.utility.physics.CirclePhysicsBody;
import com.elements.game.utility.physics.PhysicsBody;
import com.elements.game.visitors.GameObjectVisitor;

public class Fireball extends CollidableObject {

    CirclePhysicsBody hitbox;

    public Fireball(JsonValue fireballConstants, Player p) {
        super(0.5f, 0.5f);
        hitbox = new CirclePhysicsBody(p.getX() + fireballConstants.getFloat("offset_x"),
                                       p.getY() + fireballConstants.getFloat("offset_y"), 0.25f);
//        hitbox.setBodyType(BodyDef.BodyType.KinematicBody);
        hitbox.setBodyType(BodyDef.BodyType.DynamicBody);
    }

    public void applyVelocity(Vector2 force) {
        Body body = hitbox.getBody();
        body.setGravityScale(0);
        body.setLinearVelocity(force);
    }

    @Override
    public PhysicsBody getHitBox() {
        return hitbox;
    }

    @Override
    public <V> V accept(GameObjectVisitor<V> v) {
        return v.visit(this);
    }
}
