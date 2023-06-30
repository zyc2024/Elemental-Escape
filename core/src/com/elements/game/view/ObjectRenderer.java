package com.elements.game.view;

import com.badlogic.gdx.math.Vector2;
import com.elements.game.model.Player;

/**
 * ObjectRenderer provides functionality to render all kinds of Game objects.
 * This is a collection of draw functionality to keep models free from
 * directly handling textures.
 */
public class ObjectRenderer {

    // BEGIN-REGION ================== ASSETS ==================================

    // TODO: depending on number of assets here, might be wise to set up a
    //  texture hashmap or data structure to group textures. In this class,
    //  we primaryly need textures

    // END-REGION ==================== ASSETS ==================================

    private GameCanvas canvas;

    private final Vector2 drawScale = new Vector2();

    public ObjectRenderer(GameCanvas canvas) {this.canvas = canvas;}

    public void setCanvas(GameCanvas canvas) {this.canvas = canvas;}

    public void setDrawScale(Vector2 scale) {drawScale.set(scale);}

    public void draw(Player p) {
        // just an example
        canvas.drawText("null", null, 0, 0);
    }

}
