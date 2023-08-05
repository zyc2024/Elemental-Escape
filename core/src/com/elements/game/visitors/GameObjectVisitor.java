package com.elements.game.visitors;

import com.elements.game.model.*;

/**
 * A (GameObject) visitor implements a function F for which objects belonging to the GameObject
 * class hierarchy can be used as inputs to generate outputs. Concrete examples of visitors include:
 * <ul>
 *     <li>RenderingVisitor: applies a draw() method on every object</li>
 *     <li>SoundEffectVisitor: checks each object, plays sound effects based on object state</li>
 *     <li>DebuggingVisitor: applies a debug version of draw() on every object</li>
 * </ul>
 * none of the functions in the above example visitors would return any values (void) so the
 * class should implement with type {@link Void}. The visitor provides a set of overloaded
 * methods named {@link #visit} which is the universal name for generic F.
 * @param <V> the return type of the extension
 */
public abstract class GameObjectVisitor<V> {

    public V visit(GameObject ignoredO){
        return null;
    }

    public V visit(Player player){
        return visit((GameObject) player);
    }

    public V visit(BlockPlatform platform){
        return visit((GameObject) platform);
    }

    public V visit(Fireball fireball){
        return visit((GameObject) fireball);
    }

    // add visit methods for other new classes, with default implementation returning null or
    // upcasting argument and using another visit method defined in file.

}
