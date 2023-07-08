package com.elements.game.controller;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.math.*;

public class InputController {

    // Fields to manage buttons
    /** Whether the reset button was just pressed. */
    private boolean resetPressed;
    private boolean resetPrevious;

    /** Whether the debug toggle was just pressed. */
    private boolean debugPressed;
    private boolean debugPrevious;

    /** How much did we move horizontally? */
    private float horizontal;

    /** whether the jump key was just pressed */
    private boolean jumpToggled;

    /**
     * Returns the amount of sideways movement <br>
     * -1 = left, 1 = right, 0 = still
     * @return the amount of sideways movement.
     */
    public float getHorizontal() {
        return horizontal;
    }

    /**
     * @return true if the reset button was pressed.
     */
    public boolean didReset() {
        return resetPressed && !resetPrevious;
    }

    /**
     * @return true if the player wants to go toggle the debug mode.
     */
    public boolean didDebug() {
        return debugPressed && !debugPrevious;
    }

    /**
     * Creates a new input controller <p></p>
     * The input controller attempts to connect to the keyboard control.
     */
    public InputController() {
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.None);
    }


    /**
     * Reads input from the keyboard.
     */
    private void readKeyboard(Rectangle bounds, Vector2 scale) {
        resetPressed = resetPressed || (Gdx.input.isKeyPressed(Input.Keys.R));
        debugPressed = debugPressed || (Gdx.input.isKeyPressed(Input.Keys.F1));

        // A/D for moving character
        horizontal = 0.0f;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            horizontal += 1.0f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            horizontal -= 1.0f;
        }
        jumpToggled = Gdx.input.isKeyJustPressed(Input.Keys.W);

        // get mouse position
//        mousePos.x = Gdx.input.getX();
//        mousePos.y = Gdx.input.getY();
    }
}
