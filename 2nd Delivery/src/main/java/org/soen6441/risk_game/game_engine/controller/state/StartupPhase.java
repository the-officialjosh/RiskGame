package org.soen6441.risk_game.game_engine.controller.state;

import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.controller.GameMapController;
import org.soen6441.risk_game.game_map.view.DisplayToUser;
import org.soen6441.risk_game.player_management.controller.PlayerController;

/**
 * Class for Startup phase
 */
public class StartupPhase implements Phase {
    private GameMapController d_gameMapController;
    private PlayerController d_playerController;
    private DisplayToUser d_displayToUser;

    /**
     * Constructor for class.
     */
    public StartupPhase() {
        d_gameMapController = new GameMapController();
        d_playerController = new PlayerController();
        d_displayToUser = new DisplayToUser();
    }

    /**
     * {@inheritDoc}
     */
    public void handlePhase(GameSession p_gameSession) {
        d_displayToUser.startupPhaseBeginningMessage();

        // Map management step
        d_gameMapController.handleMapManagementStep(p_gameSession);

        // Load countries step
        d_playerController.loadPlayers(p_gameSession);

        // Assign countries step
        d_gameMapController.assignCountries(p_gameSession);

        // Show startup end message
        d_displayToUser.startupPhaseEndMessage();
    }
}
