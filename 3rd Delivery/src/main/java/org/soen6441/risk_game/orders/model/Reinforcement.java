package org.soen6441.risk_game.orders.model;

import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.player_management.model.Player;

/**
 * The type Reinforcement.
 * @author Safin Mahesania
 * @version 1.0
 */
public class Reinforcement implements Order {
    private Player d_player;
    private GameSession d_gameSession;

    /**
     * Instantiates a new Reinforcement.
     *
     * @param d_player the d player
     */
    public Reinforcement(Player d_player) {
        this.d_player = d_player;
    }

    public GameSession getD_gameSession() {
        return d_gameSession;
    }

    public void setD_gameSession(GameSession p_gameSession) {
        this.d_gameSession = p_gameSession;
    }

    @Override
    public void execute() {
        int l_currentNumberOfReinforcement = d_gameSession.getPlayerByName(d_player.getName()).getNumberOfReinforcementsArmies();
        d_gameSession.getPlayerByName(d_player.getName()).setNumberOfReinforcementsArmies(l_currentNumberOfReinforcement + 5);
    }
}