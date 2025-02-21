package org.soen6441.risk_game.game_map.view;

public class DisplayToUser {
    /**
     * Display the welcome message to the user.
     */
    public void welcomeMessage() {
        System.out.println("Welcome to the 'SOEN6441 Risk Game'!");
    }

    /**
     * Display the startup phase beginning message to the user.
     */
    public void startupPhaseBeginningMessage() {
        System.out.println("*********************** Startup Phase ***********************\n");
    }

    /**
     * Display the startup phase end message to the user.
     */
    public void startupPhaseEndMessage() {
        System.out.println("*********************** Startup Phase Successfully Ended ***********************\n");
    }
}
