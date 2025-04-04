package org.soen6441.risk_game.player_management.model;

import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.model.Country;
import org.soen6441.risk_game.player_management.strategy.PlayerStrategy;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class represents a cheater player strategy.
 * The cheater player conquers all immediate neighboring enemy countries and doubles the number of armies on its countries with enemy neighbors.
 */
public class CheaterPlayer implements PlayerStrategy, Serializable {
    private final Player d_player;
    private GameSession d_gameSession;

    /**
     * Constructor for the CheaterPlayer class.
     *
     * @param d_player The player object.
     * @param d_gameSession The game session object.
     */
    public CheaterPlayer(Player d_player, GameSession d_gameSession) {
        this.d_player = d_player;
        this.d_gameSession = d_gameSession;
    }

    /**
     * Issues orders for the cheater player.
     * Conquers all immediate neighboring enemy countries and doubles the number of armies on its countries with enemy neighbors.
     */
    @Override
    public void issueOrder() {
        // Conquer all adjacent enemy countries
        for (Country country : new ArrayList<>(d_player.getD_countries_owned())) {
            for (Country neighbor : country.getAdjacentCountries()) {
                if (neighbor.getD_ownedBy() != null && !neighbor.getD_ownedBy().equals(d_player)) {
                    // Transfer ownership
                    neighbor.getD_ownedBy().getD_countries_owned().remove(neighbor);
                    neighbor.setD_ownedBy(d_player);
                    d_player.getD_countries_owned().add(neighbor);
                    neighbor.setExistingArmies(1);
                }
            }
        }

        // Double armies on countries with enemy neighbors
        for (Country country : d_player.getD_countries_owned()) {
            boolean hasEnemy = false;
            for (Country neighbor : country.getAdjacentCountries()) {
                if (neighbor.getD_ownedBy() != null && !neighbor.getD_ownedBy().equals(d_player)) {
                    hasEnemy = true;
                    break;
                }
            }
            if (hasEnemy) {
                country.setExistingArmies(country.getExistingArmies() * 2);
            }
        }
    }
}