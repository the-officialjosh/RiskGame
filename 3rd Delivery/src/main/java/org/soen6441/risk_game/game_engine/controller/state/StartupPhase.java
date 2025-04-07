package org.soen6441.risk_game.game_engine.controller.state;

import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.controller.GameMapController;
import org.soen6441.risk_game.game_map.view.DisplayToUser;
import org.soen6441.risk_game.player_management.controller.PlayerController;

/**
 * Class for Startup phase of the game.
 * Handles map management, player loading, and country assignment.
 */
public class StartupPhase implements Phase {

    private final GameMapController gameMapController;
    private final PlayerController playerController;
    private final DisplayToUser displayToUser;

    /**
     * Constructor for StartupPhase.
     */
    public StartupPhase() {
        this.gameMapController = new GameMapController();
        this.playerController = new PlayerController();
        this.displayToUser = new DisplayToUser();
    }

    /**
     * Handles the startup phase of the game.
     *
     * @param gameSession The game session.
     */
    @Override
    public void handlePhase(GameSession gameSession) {
        // Display startup phase beginning message
        displayToUser.startupPhaseBeginningMessage();

        // Map management step
        gameMapController.handleMapManagementStep(gameSession);

        // Load players step
        playerController.loadPlayers(gameSession);

        // Assign countries step
        gameMapController.assignCountries(gameSession);

        // Display startup phase end message
        displayToUser.startupPhaseEndMessage();
    }
}