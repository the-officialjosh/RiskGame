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

    /**
     * Instantiates a new Reinforcement.
     *
     * @param d_player the d player
     */
    public Reinforcement(Player d_player) {
        this.d_player = d_player;
    }

    @Override
    public void execute() {
        GameSession l_gameSession = GameSession.getInstance();
        int l_currentNumberOfReinforcement = l_gameSession.getPlayerByName(d_player.getName()).getNumberOfReinforcementsArmies();
        l_gameSession.getPlayerByName(d_player.getName()).setNumberOfReinforcementsArmies(l_currentNumberOfReinforcement + 5);
    }
}