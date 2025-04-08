package org.soen6441.risk_game.player_management.model;

import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.model.Country;
import org.soen6441.risk_game.orders.model.Order;
import org.soen6441.risk_game.player_management.strategy.PlayerStrategy;

import java.io.Serializable;
import java.util.List;

/**
 * Represents a computer player in the game.
 *
 * @author Irfan Maknojia
 * @author Joshua Onyema
 * @author Ahmed Fakhir
 * @version 1.0
 */
public class ComputerPlayer extends Player implements Serializable {
    public ComputerPlayer(String p_name, int p_numberOfReinforcementsArmies, List<Order> p_orders, GameSession p_gameSession) {
        super(p_name, p_numberOfReinforcementsArmies, p_orders, p_gameSession);
    }
}