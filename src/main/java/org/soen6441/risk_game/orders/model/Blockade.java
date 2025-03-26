package org.soen6441.risk_game.orders.model;

import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.model.Country;
import org.soen6441.risk_game.monitoring.LogEntryBuffer;
import org.soen6441.risk_game.player_management.model.Player;
import java.util.HashMap;

public class Blockade implements Order {
    private Player l_player;
    private Country l_country;

    public Blockade(Player p_player, Country p_country) {
        this.l_player = p_player;
        this.l_country = p_country;
    }

    @Override
    public void execute() {
        GameSession l_gameSession = GameSession.getInstance();
        Country targetCountry = l_gameSession.getMap().getCountriesById(l_country.getCountryId());

        if (targetCountry == null || l_player == null) {
            return; // Invalid state, do nothing
        }

        Integer currentArmies = targetCountry.getExistingArmies().get(l_player);
        if (currentArmies == null || currentArmies <= 0) {
            return; // No armies to blockade
        }

        HashMap<Player, Integer> newArmyMap = new HashMap<>();
        newArmyMap.put(l_player, currentArmies * 3);
        targetCountry.setExistingArmies(newArmyMap);
        targetCountry.setD_isTerritoryNeutral(true);
        targetCountry.setD_ownedBy(null);

        LogEntryBuffer.getInstance().setValue("🛡️ Blockade: " + l_country.getName() + " tripled to " + (currentArmies * 3) + " armies and set to neutral.");
    }
}
