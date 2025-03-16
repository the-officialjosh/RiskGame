package org.soen6441.risk_game.game_engine.controller.state;

import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.player_management.controller.PlayerController;

/**
 * {@inheritDoc}
 */
public class ExecuteOrderPhase implements Phase {
    private PlayerController d_playerController;

    /**
     * Constructor for class.
     */
    public ExecuteOrderPhase() {
        d_playerController = new PlayerController();
    }

    /**
     * {@inheritDoc}
     */
    public void handlePhase(GameSession p_gameSession) {
        // Execute order phase
        d_playerController.executeOrder(p_gameSession);
    }
}
