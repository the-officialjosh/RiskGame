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

public class AggressivePlayer implements PlayerStrategy, Serializable {
    private final Player d_player;
    private final GameSession d_gameSession;
    private Country d_strongestCountry;

    public AggressivePlayer(Player p_player, GameSession p_gameSession) {
        this.d_player = p_player;
        this.d_gameSession = p_gameSession;
    }

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

    private void deployReinforcements() {
        if (d_strongestCountry == null) return;

        Deploy deployOrder = new Deploy(
                d_player,
                d_player.getNumberOfReinforcementsArmies(),
                d_strongestCountry.getCountryId() // Corrected method name
        );
        deployOrder.setD_gameSession(d_gameSession);
        d_player.setOrders(deployOrder);
        d_player.setNumberOfReinforcementsArmies(0);
    }

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

    private Optional<Country> getWeakestEnemyNeighbor() {
        return d_strongestCountry.getAdjacentCountries().stream()
                .filter(neighbor -> !neighbor.getD_ownedBy().equals(d_player))
                .min(Comparator.comparingInt(Country::getExistingArmies));
    }

    private void moveArmiesToStrongest() {
        // Move from adjacent countries (one per turn)
        d_player.getD_countries_owned().stream()
                .filter(c -> c != d_strongestCountry)
                .filter(c -> c.getAdjacentCountries().contains(d_strongestCountry))
                .findFirst()
                .ifPresent(source -> {
                    if (source.getExistingArmies() > 1) {
                        Advance moveOrder = new Advance(
                                d_player,
                                source,
                                d_strongestCountry,
                                source.getExistingArmies() - 1
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

    private void updateStrongestCountry() {
        d_strongestCountry = d_player.getD_countries_owned().stream()
                .max(Comparator.comparingInt(Country::getExistingArmies))
                .orElse(null);
    }
}