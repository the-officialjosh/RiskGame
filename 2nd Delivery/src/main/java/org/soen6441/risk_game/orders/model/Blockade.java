package org.soen6441.risk_game.orders.model;

import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.model.Country;
import org.soen6441.risk_game.monitoring.LogEntryBuffer;


/**
 * This class represents the Blockade order.
 * @author Joshua Onyema
 * @author Safin Mahesania
 * @version 1.0
 */
public class Blockade implements Order {
    private Country l_country;

    public Blockade(Country p_country) {
        this.l_country = p_country;
    }

    @Override
    public void execute() {
        GameSession l_gameSession = GameSession.getInstance();

        int currentArmies = l_gameSession.getMap().getCountriesById(l_country.getCountryId()).getExistingArmies();
        l_gameSession.getMap().getCountriesById(l_country.getCountryId()).setExistingArmies(currentArmies * 3);
        l_gameSession.getMap().getCountriesById(l_country.getCountryId()).setD_isTerritoryNeutral(true);
        l_gameSession.getMap().getCountriesById(l_country.getCountryId()).setD_ownedBy(null);

        LogEntryBuffer.getInstance().setValue("🛡️ Blockade: " + l_country.getName() + " tripled to " + (currentArmies * 3) + " armies and set to neutral.");
    }
}
