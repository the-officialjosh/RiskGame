package org.soen6441.risk_game.player_management.model;

import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.model.Country;
import org.soen6441.risk_game.player_management.model.Player;
import org.soen6441.risk_game.orders.model.Advance;
import org.soen6441.risk_game.orders.model.Airlift;
import org.soen6441.risk_game.orders.model.Deploy;
import org.soen6441.risk_game.player_management.strategy.PlayerStrategy;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Optional;

/**
 * Implements an aggressive player strategy that focuses on centralizing forces and attacking.
 * This strategy:
 * <ul>
 *   <li>Deploys all reinforcements to the strongest country</li>
 *   <li>Attacks from the strongest country to the weakest adjacent enemy</li>
 *   <li>Moves armies to consolidate forces in the strongest country</li>
 *   <li>Uses airlift cards when available to reinforce the strongest country</li>
 * </ul>
 *
 *
 * @see PlayerStrategy
 */
public class AggressivePlayer implements PlayerStrategy, Serializable {
    private final Player d_player;
    private final GameSession d_gameSession;
    private Country d_strongestCountry;

    /**
     * Constructs an AggressivePlayer with the specified player and game session.
     *
     * @param p_player The player using this strategy
     * @param p_gameSession The current game session
     */
    public AggressivePlayer(Player p_player, GameSession p_gameSession) {
        this.d_player = p_player;
        this.d_gameSession = p_gameSession;
    }

    /**
     * Issues orders for the aggressive player's turn in three phases:
     * <ol>
     *   <li>Deploys all reinforcements to the strongest country</li>
     *   <li>Attacks from the strongest country to weakest neighbor</li>
     *   <li>Moves armies to consolidate forces</li>
     * </ol>
     * Marks the player as done when finished.
     */
    @Override
    public void issueOrder() {
        updateStrongestCountry();

        // Phase 1: Deploy reinforcements (only once per turn)
        if (d_player.getNumberOfReinforcementsArmies() > 0) {
            deployReinforcements();
        }

        // Phase 2: Attack from strongest country
        attackFromStrongest();

        // Phase 3: Move armies to consolidate forces
        moveArmiesToStrongest();

        // Mark player as done for this turn
        d_player.setDoneOrder(true);
    }

    /**
     * Deploys all reinforcement armies to the strongest country.
     * Creates a deploy order and clears the player's reinforcement count.
     */
    private void deployReinforcements() {
        if (d_strongestCountry == null) return;

        Deploy deployOrder = new Deploy(
                d_player,
                d_player.getNumberOfReinforcementsArmies(),
                d_strongestCountry.getCountryId()
        );
        deployOrder.setD_gameSession(d_gameSession);
        d_player.setOrders(deployOrder);
        d_player.setNumberOfReinforcementsArmies(0);
    }

    /**
     * Attacks from the strongest country to the weakest adjacent enemy.
     * Creates an advance order with all but one army from the strongest country.
     */
    private void attackFromStrongest() {
        if (d_strongestCountry == null || d_strongestCountry.getExistingArmies() <= 1) return;

        getWeakestEnemyNeighbor().ifPresent(target -> {
            int attackArmies = d_strongestCountry.getExistingArmies() - 1;
            Advance attackOrder = new Advance(
                    d_player,
                    d_strongestCountry,
                    target,
                    attackArmies
            );
            attackOrder.setD_gameSession(d_gameSession);
            d_player.setOrders(attackOrder);
        });
    }

    /**
     * Finds the weakest adjacent enemy country to attack.
     *
     * @return Optional containing the weakest enemy neighbor, or empty if none exists
     */
    private Optional<Country> getWeakestEnemyNeighbor() {
        return d_strongestCountry.getAdjacentCountries().stream()
                .filter(neighbor -> !neighbor.getD_ownedBy().equals(d_player))
                .min(Comparator.comparingInt(Country::getExistingArmies));
    }

    /**
     * Moves armies to consolidate forces in the strongest country.
     * First attempts adjacent moves, then uses airlift if available.
     */
    private void moveArmiesToStrongest() {
        // Move from adjacent countries (one per turn)
        d_player.getD_countries_owned().stream()
                .filter(c -> c != d_strongestCountry)
                .filter(c -> c.getAdjacentCountries().contains(d_strongestCountry))
                .findFirst()
                .ifPresent(source -> {
                    if ( d_strongestCountry.getExistingArmies() != 0 && source.getExistingArmies() <= d_strongestCountry.getExistingArmies()) {
                        Advance moveOrder = new Advance(
                                d_player,
                                d_strongestCountry,
                                source,
                                d_strongestCountry.getExistingArmies() - 1
                        );
                        moveOrder.setD_gameSession(d_gameSession);
                        d_player.setOrders(moveOrder);
                    }
                });

        // Airlift non-adjacent (if card available)
        if (d_player.hasCard("airlift")) {
            d_player.getD_countries_owned().stream()
                    .filter(c -> c != d_strongestCountry)
                    .filter(c -> !c.getAdjacentCountries().contains(d_strongestCountry))
                    .findFirst()
                    .ifPresent(source -> {
                        if (source.getExistingArmies() > 1) {
                            Airlift airliftOrder = new Airlift(
                                    source,
                                    d_strongestCountry,
                                    source.getExistingArmies() - 1
                            );
                            airliftOrder.setD_gameSession(d_gameSession);
                            d_player.setOrders(airliftOrder);
                            d_player.useCard("airlift");
                        }
                    });
        }
    }

    /**
     * Updates the strongest country reference by finding the owned country
     * with the most armies.
     */
    private void updateStrongestCountry() {
        d_strongestCountry = d_player.getD_countries_owned().stream()
                .max(Comparator.comparingInt(Country::getExistingArmies))
                .orElse(null);
    }
}