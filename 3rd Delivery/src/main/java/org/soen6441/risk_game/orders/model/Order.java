package org.soen6441.risk_game.orders.model;


/**
 * This interface represents the generic Order that players
 * can instruct.
 *
 * @author Ahmed Fakhir
 * @author Safin Mahesania
 * @version 1.0
 *
 */
public interface Order {
    /**
     * This class executes the order by updating
     * accordingly the provided gameSession.
     */
    void execute();
}
