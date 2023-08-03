package com.elements.game.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class InputController {

    // TODO (later): custom key-binds

    /** Whether the reset button was just pressed. */
    private boolean resetToggled;

    /** Whether the debug toggle was just pressed. */
    private boolean debugToggled;

    /** whether the jump key was just pressed */
    private boolean jumpToggled;

    /** whether the ability key was just pressed */
    private boolean abilityToggled;

    /** horizontal movement */
    private float horizontal;

    /**
     * amount of sideways movement <br> -1 = left, 1 = right, 0 = still
     *
     * @return the amount of sideways movement.
     */
    public float getHorizontal() {
        return horizontal;
    }

    /**
     * @return whether reset button was toggled
     */
    public boolean resetToggled() {
        return resetToggled;
    }

    /**
     * @return whether debug button was toggled
     */
    public boolean debugToggled() {
        return debugToggled;
    }

    public boolean abilityToggled() { return abilityToggled; }

    /**
     * @return whether jump button was toggled
     */
    public boolean jumpToggled() {
        return jumpToggled;
    }

    /**
     * Creates a new input controller. <br> The input controller attempts to connect to the keyboard
     * control.
     */
    public InputController() {}

    /**
     * Reads the input for the player and converts the result into game logic.
     */
    public void readInput() {
        readKeyboard();
        // why make the other method private?
        // answer: in the event that input can be extended with xbox controllers, only one set of
        // inputs can be read but the user of the input controller need not understand the
        // distinction.
    }

    /**
     * Reads input from the keyboard.
     */
    private void readKeyboard() {
        // A/D for moving character
        horizontal = 0.0f;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            horizontal += 1.0f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            horizontal -= 1.0f;
        }

        resetToggled = Gdx.input.isKeyJustPressed(Input.Keys.R);
        debugToggled = Gdx.input.isKeyJustPressed(Input.Keys.F1);
        jumpToggled = Gdx.input.isKeyJustPressed(Input.Keys.SPACE);
        abilityToggled = Gdx.input.isKeyJustPressed(Input.Keys.J);

        // get mouse position
        // mousePos.x = Gdx.input.getX();
        // mousePos.y = Gdx.input.getY();
    }
}
