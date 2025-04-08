package org.soen6441.risk_game.player_management.model;

import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.model.Country;
import org.soen6441.risk_game.orders.model.Advance;
import org.soen6441.risk_game.orders.model.Deploy;
import org.soen6441.risk_game.player_management.strategy.PlayerStrategy;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * A strategy implementation for a player that issues orders randomly.
 * <p>
 * The RandomPlayer behaves unpredictably and performs the following on each turn:
 * <ul>
 *     <li>Randomly selects one of its owned countries and deploys all available reinforcements to it.</li>
 *     <li>Has a 70% probability of issuing an Advance order (either attack or movement),
 *         using randomly chosen valid source and target countries.</li>
 * </ul>
 * This strategy does not consider long-term planning or optimization and is mainly used to simulate non-deterministic
 * behavior for AI-controlled players.
 *
 * @author Kawshik
 */
public class RandomPlayer implements PlayerStrategy, Serializable {

    private final Player d_player;
    private final GameSession d_gameSession;
    private final Random d_random = new Random();

    /**
     * Constructs a new RandomPlayer strategy with the specified player and game session.
     *
     * @param p_player      the player associated with this strategy
     * @param p_gameSession the game session the player is part of
     */
    public RandomPlayer(Player p_player, GameSession p_gameSession) {
        this.d_player = p_player;
        this.d_gameSession = p_gameSession;
    }

    /**
     * Issues one order per turn for the player following the Random strategy.
     * <p>
     * The method follows this turn sequence:
     * <ol>
     *     <li>If the player owns no countries, the turn ends immediately.</li>
     *     <li>Randomly deploys all reinforcements to one of the owned countries.</li>
     *     <li>With 70% chance, performs an Advance order using randomly selected valid countries.</li>
     *     <li>Marks the player's turn as completed.</li>
     * </ol>
     */
    @Override
    public void issueOrder() {
        List<Country> ownedCountries = d_player.getD_countries_owned();
        if (ownedCountries.isEmpty()) {
            d_player.setDoneOrder(true);
            return;
        }

        // Phase 1: Deploy all reinforcements randomly
        deployRandomly(ownedCountries);

        // Phase 2: 70% chance to advance/attack
        if (d_random.nextDouble() < 0.7) {
            advanceRandomly(ownedCountries);
        }

        // Phase 3: Mark player turn as completed
        d_player.setDoneOrder(true);
    }

    /**
     * Randomly deploys all available reinforcement armies to one of the player's owned countries.
     * <p>
     * If the player has reinforcements available, a country is randomly selected from the list of owned
     * countries, and all reinforcements are deployed there. After the deployment, the reinforcement count is reset to 0.
     *
     * @param p_owned list of countries owned by the player
     */
    private void deployRandomly(List<Country> p_owned) {
        if (d_player.getNumberOfReinforcementsArmies() > 0) {
            Country target = p_owned.get(d_random.nextInt(p_owned.size()));

            Deploy deployOrder = new Deploy(
                    d_player,
                    d_player.getNumberOfReinforcementsArmies(),
                    target.getCountryId()
            );

            deployOrder.setD_gameSession(d_gameSession);
            d_player.setOrders(deployOrder);
            d_player.setNumberOfReinforcementsArmies(0);
        }
    }

    /**
     * Randomly issues an Advance order from a valid source country to a valid target country.
     * <p>
     * The source country is randomly chosen from the list of owned countries that have more than one army.
     * The target is selected randomly from its adjacent countries. The number of armies to move is randomly
     * selected between 1 and (existing armies - 1).
     * <p>
     * The Advance order can either result in movement or an attack depending on the ownership of the target country.
     *
     * @param p_owned list of countries owned by the player
     */
    private void advanceRandomly(List<Country> p_owned) {
        // Find source countries with more than one army
        List<Country> validSources = p_owned.stream()
                .filter(c -> c.getExistingArmies() > 1)
                .toList();

        if (validSources.isEmpty()) return;

        Country source = validSources.get(d_random.nextInt(validSources.size()));

        // Get adjacent countries to the selected source
        List<Country> adjacent = source.getAdjacentCountries().stream()
                .filter(Objects::nonNull)
                .toList();

        if (adjacent.isEmpty()) return;

        Country target = adjacent.get(d_random.nextInt(adjacent.size()));

        // Select a random number of armies to move (at least 1, at most armies-1)
        int maxArmies = source.getExistingArmies() - 1;
        int armiesToMove = d_random.nextInt(maxArmies) + 1;

        Advance advanceOrder = new Advance(d_player, source, target, armiesToMove);
        advanceOrder.setD_gameSession(d_gameSession);
        d_player.setOrders(advanceOrder);
    }

    @Override
    public String toString() {
        return "Random";
    }
}
