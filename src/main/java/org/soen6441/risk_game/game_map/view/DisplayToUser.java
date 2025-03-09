package org.soen6441.risk_game.game_map.view;

/**
 * Handles user display messages for the game.
 *
 * @author Ahmed Fakhir
 * @author Joshua Onyema
 * @author Kawshik Kumar Ghosh
 *
 * @version 1.0
 */
public class DisplayToUser {

    /**
     * Displays a welcome message to the user.
     */
    public void welcomeMessage() {
        instructionMessage("\n---- Welcome to the CODING_HOLICS SOEN6441 Risk Game ----\n");

    }

    /**
     * Displays the startup phase beginning message.
     */
    public void startupPhaseBeginningMessage() {
        instructionMessage("----------------- STARTUP PHASE BEGINS -----------------\n");

    }

    /**
     * Displays the startup phase end message.
     */
    public void startupPhaseEndMessage() {
        instructionMessage("----------------- STARTUP PHASE ENDED -----------------\n");

    }



    /**
     * Displays an instruction message.
     *
     * @param message The message to be displayed.
     */
    public void instructionMessage(String message) {
        System.out.println(message);
    }


}