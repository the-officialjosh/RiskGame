package org.soen6441.risk_game.player_management.model;

import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.model.Country;
import org.soen6441.risk_game.orders.model.Advance;
import org.soen6441.risk_game.orders.model.Deploy;
import org.soen6441.risk_game.player_management.strategy.PlayerStrategy;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

/**
 * This class represents a random player strategy.
 * The random player deploys, attacks, and moves armies randomly.
 */
public class RandomPlayer implements PlayerStrategy, Serializable {
    private final Player d_player;
    private GameSession d_gameSession;

    /**
     * Constructor for the RandomPlayer class.
     *
     * @param d_player The player object.
     * @param d_gameSession The game session object.
     */
    public RandomPlayer(Player d_player, GameSession d_gameSession) {
        this.d_player = d_player;
        this.d_gameSession = d_gameSession;
    }

    /**
     * Issues orders for the random player.
     * Deploys reinforcements to a random country, attacks random neighboring countries, and moves armies randomly.
     */
    @Override
    public void issueOrder() {
        Random rand = new Random();
        List<Country> owned = d_player.getD_countries_owned();
        if (owned.isEmpty()) return;

        // Deploy randomly
        if (d_player.getNumberOfReinforcementsArmies() > 0) {
            Country randomCountry = owned.get(rand.nextInt(owned.size()));
            Deploy deployOrder = new Deploy(d_player, d_player.getNumberOfReinforcementsArmies(), randomCountry.getCountryId());
            deployOrder.setD_gameSession(d_gameSession);
            d_player.setOrders(deployOrder);
            d_player.setNumberOfReinforcementsArmies(0);
        }

        // Random advance order
        Country from = owned.get(rand.nextInt(owned.size()));
        List<Country> adjacent = from.getAdjacentCountries();
        if (!adjacent.isEmpty()) {
            Country to = adjacent.get(rand.nextInt(adjacent.size()));
            int armies = rand.nextInt(from.getExistingArmies()) + 1;
            Advance advanceOrder = new Advance(d_player, from, to, armies);
            advanceOrder.setD_gameSession(d_gameSession);
            d_player.setOrders(advanceOrder);
        }
    }
}