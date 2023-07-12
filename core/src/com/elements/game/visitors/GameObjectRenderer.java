package com.elements.game.visitors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.elements.game.model.BlockPlatform;
import com.elements.game.model.Player;
import com.elements.game.utility.assets.AssetDirectory;
import com.elements.game.view.GameCanvas;

/**
 * GameObjectRenderer provides functionality to render all kinds of Game objects. This is a
 * collection of draw functionality to keep models free from directly handling textures.
 */
public class GameObjectRenderer extends GameObjectVisitor<Void> {

    // BEGIN-REGION ================== ASSETS ==================================

    // TODO: depending on number of assets here, might be wise to set up a
    //  texture hashmap or data structure to group textures. In this class,
    //  we primarily need textures

    private TextureRegion playerTexture;

    private Texture grassTexture;

    // END-REGION ==================== ASSETS ==================================

    private final Vector2 drawScale;

    private final GameCanvas canvas;

    public GameObjectRenderer(GameCanvas canvas) {
        this.canvas = canvas;
        drawScale = new Vector2();
    }


    /**
     * updates the renderer's drawing scale
     *
     * @param sx scaling of x dimension (1 unit wide in game -> sx pixels in screen space)
     * @param sy scaling of y dimension (1 unit tall in game -> sy pixels in screen space)
     */
    public void setDrawScale(float sx, float sy) {drawScale.set(sx, sy);}

    /**
     * stores and initializes all necessary assets to render game objects
     *
     * @param assets asset directory
     */
    public void gatherAssets(AssetDirectory assets) {
        playerTexture = new TextureRegion(assets.getEntry("game:player", Texture.class));
        grassTexture = assets.getEntry("game:grass_block", Texture.class);
    }

    @Override
    public Void visit(Player p) {
        Vector2 dimensions = p.getDisplayDimensions();
        int textureWidth = playerTexture.getRegionWidth();
        int textureHeight = playerTexture.getRegionHeight();
        canvas.draw(playerTexture, Color.WHITE, textureWidth / 2f, textureHeight / 2f,
                    p.getX() * drawScale.x, p.getY() * drawScale.y, p.getHitBox().getAngle(),
                    dimensions.x * drawScale.x / textureWidth,
                    dimensions.y * drawScale.y / textureHeight);
        return null;
    }

    @Override
    public Void visit(BlockPlatform platform) {
        Vector2 dimensions = platform.getDisplayDimensions();
        int textureWidth = grassTexture.getWidth();
        int textureHeight = grassTexture.getHeight();
        canvas.draw(grassTexture, Color.WHITE, textureWidth / 2f, textureHeight / 2f,
                    platform.getX() * drawScale.x, platform.getY() * drawScale.y,
                    platform.getHitBox().getAngle(), dimensions.x * drawScale.x / textureWidth,
                    dimensions.y * drawScale.y / textureHeight);
        return null;
    }

}
