package org.soen6441.risk_game.game_engine.controller.state;

import org.soen6441.risk_game.game_engine.model.GameSession;

/**
 * Interface representing the game phases.
 * @author Ahmed Fakhir
 */
public interface Phase {
    /**
     * Contains the implementation details for a specific phase.
     * @param p_gameSession
     */
    void handlePhase(GameSession p_gameSession);
}
