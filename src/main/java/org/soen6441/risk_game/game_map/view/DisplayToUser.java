package org.soen6441.risk_game.game_map.view;

/**
 * Handles user display messages for the game.
 */
public class DisplayToUser {

    /**
     * Displays a welcome message to the user.
     */
    public void welcomeMessage() {
        displayMessage("\n---- Welcome to the CODING_HOLICS SOEN6441 Risk Game ----\n");

    }

    /**
     * Displays the startup phase beginning message.
     */
    public void startupPhaseBeginningMessage() {
        displayMessage("----------------- STARTUP PHASE BEGINS -----------------\n");

    }

    /**
     * Displays the startup phase end message.
     */
    public void startupPhaseEndMessage() {
        displayMessage("----------------- STARTUP PHASE ENDED -----------------\n");

    }



    /**
     * Displays an instruction message.
     *
     * @param message The message to be displayed.
     */
    public void instructionMessage(String message) {
        displayMessage(message);
    }

    /**
     * A general method for displaying messages.
     *
     * @param message The message to be printed.
     */
    private void displayMessage(String message) {
        System.out.println(message);
    }
}