package org.soen6441.risk_game.player_management.model;

import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.model.Country;
import org.soen6441.risk_game.player_management.model.Player;
import org.soen6441.risk_game.orders.model.Advance;
import org.soen6441.risk_game.orders.model.Deploy;
import org.soen6441.risk_game.player_management.strategy.PlayerStrategy;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Optional;

/**
 * Represents a Benevolent player strategy in the Risk game.
 * <p>
 * A Benevolent player focuses on reinforcing and protecting their weakest countries
 * rather than attacking others. It avoids aggression and aims to strengthen its vulnerable areas.
 * </p>
 * <p>
 * The strategy performs the following actions during a turn:
 * <ol>
 *     <li>Identifies the weakest country owned by the player</li>
 *     <li>Deploys all reinforcements to that country</li>
 *     <li>Moves armies from the strongest adjacent friendly country to reinforce it</li>
 * </ol>
 */
public class BenevolentPlayer implements PlayerStrategy, Serializable {

    private final Player d_player;
    private final GameSession d_gameSession;
    private Country d_weakestCountry;

    /**
     * Constructs a BenevolentPlayer strategy.
     *
     * @param p_player      the player using this strategy
     * @param p_gameSession the current game session
     */
    public BenevolentPlayer(Player p_player, GameSession p_gameSession) {
        this.d_player = p_player;
        this.d_gameSession = p_gameSession;
    }

    /**
     * Issues orders for the current turn based on the benevolent strategy.
     * The strategy includes deploying reinforcements to the weakest country
     * and reinforcing it from nearby friendly countries.
     */
    @Override
    public void issueOrder() {
        updateWeakestCountry();

        // Phase 1: Deploy all reinforcements to weakest country
        deployReinforcements();

        // Phase 2: Reinforce weakest from strongest adjacent ally
        reinforceWeakest();

        // Mark turn completion
        d_player.setDoneOrder(true);
    }

    /**
     * Updates the reference to the weakest country owned by the player.
     * The weakest country is the one with the fewest armies.
     */
    private void updateWeakestCountry() {
        d_weakestCountry = d_player.getD_countries_owned().stream()
                .min(Comparator.comparingInt(Country::getExistingArmies))
                .orElse(null);
    }

    /**
     * Deploys all available reinforcement armies to the weakest country.
     * If no reinforcements are available or no weakest country is found, nothing is done.
     */
    private void deployReinforcements() {
        if (d_weakestCountry == null) return;

        int reinforcements = d_player.getNumberOfReinforcementsArmies();
        if (reinforcements > 0) {
            Deploy deployOrder = new Deploy(
                    d_player,
                    reinforcements,
                    d_weakestCountry.getCountryId()
            );
            deployOrder.setD_gameSession(d_gameSession);
            d_player.setOrders(deployOrder);
            d_player.setNumberOfReinforcementsArmies(0);
        }
    }

    /**
     * Reinforces the weakest country by moving armies from the strongest adjacent ally.
     * Only executes the move if the source has more than one army.
     */
    private void reinforceWeakest() {
        if (d_weakestCountry == null) return;

        getStrongestAdjacentAlly().ifPresent(source -> {
            int movableArmies = source.getExistingArmies() - 1;
            if (movableArmies > 0) {
                Advance advanceOrder = new Advance(
                        d_player,
                        source,
                        d_weakestCountry,
                        movableArmies
                );
                advanceOrder.setD_gameSession(d_gameSession);
                d_player.setOrders(advanceOrder);
            }
        });
    }

    /**
     * Finds the strongest adjacent friendly country to the weakest country.
     * The strongest ally is determined by the number of armies.
     *
     * @return an Optional containing the strongest adjacent friendly country, or empty if none found
     */
    private Optional<Country> getStrongestAdjacentAlly() {
        return d_weakestCountry.getAdjacentCountries().stream()
                .filter(neighbor -> neighbor.getD_ownedBy().equals(d_player))
                .max(Comparator.comparingInt(Country::getExistingArmies));
    }
}
