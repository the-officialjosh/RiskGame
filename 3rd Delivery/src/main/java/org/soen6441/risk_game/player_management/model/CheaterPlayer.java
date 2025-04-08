package org.soen6441.risk_game.player_management.model;

import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.model.Country;
import org.soen6441.risk_game.player_management.strategy.PlayerStrategy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a Cheater player strategy in the Risk game.
 * <p>
 * The Cheater strategy is overpowered and violates normal gameplay rules.
 * During its turn, it automatically conquers all adjacent enemy territories
 * and doubles the number of armies on its border countries.
 * </p>
 */
public class CheaterPlayer implements PlayerStrategy, Serializable {

    private final Player d_player;
    private final GameSession d_gameSession;

    /**
     * Constructs a CheaterPlayer strategy.
     *
     * @param p_player      the player using this strategy
     * @param p_gameSession the current game session
     */
    public CheaterPlayer(Player p_player, GameSession p_gameSession) {
        this.d_player = p_player;
        this.d_gameSession = p_gameSession;
    }

    /**
     * Issues orders for the current turn using the Cheater strategy.
     * The strategy includes:
     * <ol>
     *     <li>Automatically conquering all adjacent enemy territories.</li>
     *     <li>Doubling armies on any country that borders an enemy.</li>
     * </ol>
     */
    @Override
    public void issueOrder() {
        // Phase 1: Conquer all adjacent enemy territories
        conquerAdjacentEnemies();

        // Phase 2: Double armies on territories with remaining enemy neighbors
        reinforceBorderCountries();

        // Mark turn completion
        d_player.setDoneOrder(true);
    }

    /**
     * Identifies all enemy countries adjacent to the player's owned countries
     * and transfers their ownership to the cheater player.
     */
    private void conquerAdjacentEnemies() {
        List<Country> initialOwned = new ArrayList<>(d_player.getD_countries_owned());
        Set<Country> enemiesToConquer = new HashSet<>();

        // Identify all adjacent enemies
        initialOwned.forEach(country ->
                country.getAdjacentCountries().stream()
                        .filter(neighbor -> !neighbor.getD_ownedBy().equals(d_player))
                        .forEach(enemiesToConquer::add)
        );

        // Conquer territories and update ownership
        enemiesToConquer.forEach(this::transferOwnership);
    }

    /**
     * Doubles the number of armies on all countries owned by the player
     * that are adjacent to enemy territories.
     */
    private void reinforceBorderCountries() {
        d_player.getD_countries_owned().forEach(country -> {
            boolean hasEnemyNeighbor = country.getAdjacentCountries().stream()
                    .anyMatch(neighbor -> !neighbor.getD_ownedBy().equals(d_player));

            if (hasEnemyNeighbor) {
                country.setExistingArmies(country.getExistingArmies() * 2);
            }
        });
    }

    /**
     * Transfers ownership of the specified country to the cheater player,
     * removing it from the previous owner's list and resetting its army count.
     *
     * @param p_country the country to transfer
     */
    private void transferOwnership(Country p_country) {
        Player previousOwner = p_country.getD_ownedBy();

        // Remove from previous owner
        if (previousOwner != null) {
            previousOwner.getD_countries_owned().remove(p_country);
            checkPlayerElimination(previousOwner);
        }

        // Add to cheater's control
        p_country.setD_ownedBy(d_player);
        d_player.getD_countries_owned().add(p_country);
        p_country.setExistingArmies(1); // Reset armies after conquest
    }

    /**
     * Checks if a player has been eliminated (i.e., no countries owned).
     * If so, removes the player from the game session.
     *
     * @param p_player the player to check
     */
    private void checkPlayerElimination(Player p_player) {
        if (p_player.getD_countries_owned().isEmpty()) {
            d_gameSession.getPlayers().remove(p_player);
        }
    }

    @Override
    public String toString() {
        return "Cheater";
    }
}
