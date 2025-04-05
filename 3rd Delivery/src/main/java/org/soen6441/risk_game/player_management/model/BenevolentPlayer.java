package org.soen6441.risk_game.player_management.model;

import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.model.Country;
import org.soen6441.risk_game.player_management.model.Player;
import org.soen6441.risk_game.orders.model.Advance;
import org.soen6441.risk_game.orders.model.Deploy;
import org.soen6441.risk_game.player_management.strategy.PlayerStrategy;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class BenevolentPlayer implements PlayerStrategy, Serializable {
    private final Player d_player;
    private final GameSession d_gameSession;
    private Country d_weakestCountry;

    public BenevolentPlayer(Player p_player, GameSession p_gameSession) {
        this.d_player = p_player;
        this.d_gameSession = p_gameSession;
    }

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

    private void updateWeakestCountry() {
        d_weakestCountry = d_player.getD_countries_owned().stream()
                .min(Comparator.comparingInt(Country::getExistingArmies))
                .orElse(null);
    }

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

    private Optional<Country> getStrongestAdjacentAlly() {
        return d_weakestCountry.getAdjacentCountries().stream()
                .filter(neighbor -> neighbor.getD_ownedBy().equals(d_player))
                .max(Comparator.comparingInt(Country::getExistingArmies));
    }
}