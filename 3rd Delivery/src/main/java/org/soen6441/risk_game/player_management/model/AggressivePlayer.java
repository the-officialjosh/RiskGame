package org.soen6441.risk_game.player_management.model;

import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.model.Country;
import org.soen6441.risk_game.orders.model.Advance;
import org.soen6441.risk_game.orders.model.Deploy;
import org.soen6441.risk_game.player_management.strategy.PlayerStrategy;

import java.io.Serializable;
import java.util.List;

/**
 * This class represents an aggressive player strategy.
 * The aggressive player focuses on centralizing forces and attacking with the strongest country.
 */
public class AggressivePlayer implements PlayerStrategy, Serializable {

    private final Player d_player;
    private GameSession d_gameSession;

    /**
     * Constructor for the AggressivePlayer class.
     *
     * @param d_player The player object.
     * @param d_gameSession The game session object.
     */
    public AggressivePlayer(Player d_player, GameSession d_gameSession) {
        this.d_player = d_player;
        this.d_gameSession = d_gameSession;
    }

    /**
     * Issues orders for the aggressive player.
     * Deploys all reinforcements to the strongest country and attacks from the strongest country.
     */
    @Override
    public void issueOrder() {
        // Deploy all reinforcements to the strongest country
        List<Country> owned = d_player.getD_countries_owned();
        if (owned.isEmpty()) return;

        Country strongest = null;
        int maxArmies = -1;
        for (Country c : owned) {
            if (c.getExistingArmies() > maxArmies) {
                maxArmies = c.getExistingArmies();
                strongest = c;
            }
        }

        if (strongest != null && d_player.getNumberOfReinforcementsArmies() > 0) {
            Deploy deployOrder = new Deploy(d_player, d_player.getNumberOfReinforcementsArmies(), strongest.getCountryId());
            deployOrder.setD_gameSession(d_gameSession);
            d_player.setOrders(deployOrder);
            d_player.setNumberOfReinforcementsArmies(0);
        }

        // Attack from the strongest country
        if (strongest != null) {
            for (Country adj : strongest.getAdjacentCountries()) {
                if (!adj.getD_ownedBy().equals(d_player)) {
                    int armies = strongest.getExistingArmies() - 1;
                    if (armies > 0) {
                        Advance advanceOrder = new Advance(d_player, strongest, adj, armies);
                        advanceOrder.setD_gameSession(d_gameSession);
                        d_player.setOrders(advanceOrder);
                    }
                }
            }
        }
    }
}