package com.elements.game.visitors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.elements.game.model.BlockPlatform;
import com.elements.game.model.Fireball;
import com.elements.game.model.Player;
import com.elements.game.model.WoodBlock;
import com.elements.game.utility.assets.AssetDirectory;
import com.elements.game.utility.textures.FilmStrip;
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

    private Texture woodenTexture;

    /**
     * Animations for base form.
     * Index 0 = idling
     * Index 1 = walking
     * Index 2 = rising
     * Index 3 = falling
     */
    private FilmStrip[] base_animations;

    // END-REGION ==================== ASSETS ==================================

    private static final int ANIMATION_FPS = 6;

    private static final float TOLERANCE = 0.5F;

    private final Vector2 drawScale;

    private final GameCanvas canvas;

    private int frameCounter;

    private int animator;

    public GameObjectRenderer(GameCanvas canvas) {
        this.canvas = canvas;
        drawScale = new Vector2();
        base_animations = new FilmStrip[4];
        animator = 0;
    }

    /**
     * updates the renderer's drawing scale
     *
     * @param sx scaling of x dimension (1 unit wide in game -> sx pixels in screen space)
     * @param sy scaling of y dimension (1 unit tall in game -> sy pixels in screen space)
     */
    public void setDrawScale(float sx, float sy) {drawScale.set(sx, sy);}

    /**
     * updates the renderer's drawing scale
     *
     * @param scale scaling of x,y dimensions, see {@link #setDrawScale(float, float)}
     */
    public void setDrawScale(Vector2 scale) {drawScale.set(scale);}

    /**
     * stores and initializes all necessary assets to render game objects
     *
     * @param assets asset directory
     */
    public void gatherAssets(AssetDirectory assets) {
        playerTexture = new TextureRegion(assets.getEntry("game:player", Texture.class));
        grassTexture = assets.getEntry("game:grass_block", Texture.class);
        woodenTexture = assets.getEntry("game:wooden_block", Texture.class);

        base_animations[0] = new FilmStrip(assets.getEntry("game:player_base_idling", Texture.class), 1, 10);
        base_animations[1] = new FilmStrip(assets.getEntry("game:player_base_walking", Texture.class), 1, 8);
        base_animations[2] = new FilmStrip(assets.getEntry("game:player_base_rising", Texture.class), 1, 6);
        base_animations[3] = new FilmStrip(assets.getEntry("game:player_base_falling", Texture.class), 1, 8);
    }

    @Override
    public Void visit(Player p) {
        Vector2 dimensions = p.getDisplayDimensions();
        int textureWidth = playerTexture.getRegionWidth() * 4;
        int textureHeight = playerTexture.getRegionHeight() * 4;
        // TODO: clean this code up
        // change to falling/rising animation when not on ground
        if (!p.isGrounded()) {
            int prevAnimator = animator;
            // determines rise or fall using player's vertical velocity
            animator = p.getVerticalVelocity() > TOLERANCE ? 2 : 3;
            // resets filmstrip frame initially
            if (prevAnimator != animator) {
                base_animations[animator].setFrame(0);
            }
        } else if (Math.abs(p.getHorizontalVelocity()) > TOLERANCE) {
            // changes to walking animation when horizontal velocity exceeds certain threshold
            animator = 1;
        } else if (animator != 0) {
            // otherwise idling
            animator = 0;
        }

        // draws
        canvas.draw(base_animations[animator], Color.WHITE, textureWidth / 1.4F, textureHeight / 2F,
                    p.getX() * drawScale.x, p.getY() * drawScale.y, p.getHitBox().getAngle(),
                    p.getFacing() * dimensions.x * drawScale.x / textureWidth,
                    dimensions.y * drawScale.y / textureHeight);

        // increment frame depending on animation fps
        if (frameCounter % ANIMATION_FPS == 0) {
            base_animations[animator].setFrame((base_animations[animator].getFrame() + 1) % base_animations[animator].getSize());
            frameCounter = 0;
        }
        frameCounter++;

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

    @Override
    public Void visit(WoodBlock moveBlock) {
        Vector2 dimensions = moveBlock.getDisplayDimensions();
        int textureWidth = woodenTexture.getWidth();
        int textureHeight = woodenTexture.getHeight();
        canvas.draw(woodenTexture, Color.WHITE, textureWidth / 2f, textureHeight / 2f,
                    moveBlock.getX() * drawScale.x, moveBlock.getY() * drawScale.y,
                    moveBlock.getHitBox().getAngle(), dimensions.x * drawScale.x / textureWidth,
                    dimensions.y * drawScale.y / textureHeight);
        return null;
    }

    public Void visit(Fireball fireball) {
        // draw fireball
        return null;
    }

}
