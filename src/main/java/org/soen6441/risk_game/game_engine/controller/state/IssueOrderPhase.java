package org.soen6441.risk_game.game_engine.controller.state;

import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.controller.GameMapController;
import org.soen6441.risk_game.player_management.controller.PlayerController;

/**
 * {@inheritDoc}
 */
public class IssueOrderPhase implements Phase {
    private GameMapController d_gameMapController;
    private PlayerController d_playerController;

    /**
     * Constructor for class.
     */
    public IssueOrderPhase() {
        d_gameMapController = new GameMapController();
        d_playerController = new PlayerController();
    }

    /**
     * {@inheritDoc}
     */
    public void handlePhase(GameSession p_gameSession) {
        // Assign reinforcements step
        d_gameMapController.assignReinforcements(p_gameSession);

        // Issue order step
        d_playerController.issueOrderPhase(p_gameSession);
    }
}
