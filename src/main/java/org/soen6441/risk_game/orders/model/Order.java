package org.soen6441.risk_game.orders.model;

import org.soen6441.risk_game.game_engine.model.GameSession;

/**
 * This interface represents the generic Order that players
 * can instruct.
 */
public interface Order {
    /**
     * This class executes the order by updating
     * accordingly the provided gameSession.
     */
    void execute();
}
