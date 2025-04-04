package org.soen6441.risk_game.player_management.model;

import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.model.Country;
import org.soen6441.risk_game.orders.model.Advance;
import org.soen6441.risk_game.orders.model.Deploy;
import org.soen6441.risk_game.player_management.strategy.PlayerStrategy;

import java.io.Serializable;
import java.util.List;

public class BenevolentPlayer implements PlayerStrategy, Serializable {

    private final Player d_player;
    private GameSession d_gameSession;

    public BenevolentPlayer(Player d_player, GameSession d_gameSession) {
        this.d_player = d_player;
        this.d_gameSession = d_gameSession;
    }

    @Override
    public void issueOrder() {
        // Deploy to weakest country
        List<Country> owned = d_player.getD_countries_owned();
        if (owned.isEmpty()) return;

        Country weakest = null;
        int minArmies = Integer.MAX_VALUE;
        for (Country c : owned) {
            if (c.getExistingArmies() < minArmies) {
                minArmies = c.getExistingArmies();
                weakest = c;
            }
        }

        if (weakest != null && d_player.getNumberOfReinforcementsArmies() > 0) {
            Deploy deployOrder = new Deploy(d_player, d_player.getNumberOfReinforcementsArmies(), weakest.getCountryId());
            deployOrder.setD_gameSession(d_gameSession);
            d_player.setOrders(deployOrder);
            d_player.setNumberOfReinforcementsArmies(0);
        }

        // Reinforce weakest country by moving armies
        if (weakest != null) {
            Country strongestAlly = null;
            int maxArmiesAlly = -1;
            for (Country c : owned) {
                if (c != weakest && c.getExistingArmies() > maxArmiesAlly) {
                    maxArmiesAlly = c.getExistingArmies();
                    strongestAlly = c;
                }
            }

            if (strongestAlly != null && strongestAlly.getExistingArmies() > 1) {
                int armiesToMove = strongestAlly.getExistingArmies() - 1;
                Advance advanceOrder = new Advance(d_player, strongestAlly, weakest, armiesToMove);
                advanceOrder.setD_gameSession(d_gameSession);
                d_player.setOrders(advanceOrder);
            }
        }
    }
}