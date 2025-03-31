package org.soen6441.risk_game.player_management.model;

import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.orders.model.Order;

import java.util.List;

public class ComputerPlayer extends Player {
    private String d_playerBehavior;
    public ComputerPlayer(String p_name, int p_numberOfReinforcementsArmies, List<Order> p_orders, GameSession p_gameSession, String p_playerBehavior) {
        super(p_name, p_numberOfReinforcementsArmies, p_orders, p_gameSession);
        d_playerBehavior = p_playerBehavior;
    }

    public String getD_playerBehavior() {
        return d_playerBehavior;
    }

    public void setD_playerBehavior(String d_playerBehavior) {
        this.d_playerBehavior = d_playerBehavior;
    }
}