package org.soen6441.risk_game.game_engine.controller;

import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.controller.GameMapController;
import org.soen6441.risk_game.game_map.view.DisplayToUser;
import org.soen6441.risk_game.player_management.controller.PlayerController;
import org.soen6441.risk_game.player_management.model.Player;

/**
 * GameEngine class which is serving as the game entry point as well as handling
 * the global game logic.
 */
public class GameEngine {
    /**
     * Main method which is the entry point method of the game.
     * @param args
     */
    public static void main(String[] args) {
        // Game Session
        GameSession l_gameSession = GameSession.getInstance();

        // Controllers
        GameMapController l_gameMapController = new GameMapController();
        PlayerController l_playerController = new PlayerController();

        // View
        DisplayToUser l_displayToUser = new DisplayToUser();

        l_displayToUser.welcomeMessage();
        l_displayToUser.startupPhaseBeginningMessage();
        // Map management step
        l_gameMapController.handleMapManagementStep(l_gameSession);
        // Load players step
        l_playerController.loadPlayers(l_gameSession);
        // Assign countries step
        l_gameMapController.assignCountries(l_gameSession);
        l_displayToUser.startupPhaseEndMessage();

        // Game loop
        while(true) {
            // Assign reinforcements step
            l_gameMapController.assignReinforcements(l_gameSession);

            // Issue order phase TODO...
            l_playerController.issueOrderPhase(l_gameSession);
            // Execute order phase TODO...

        }
    }


}