package org.soen6441.risk_game.orders.model;

import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.model.Country;
import org.soen6441.risk_game.monitoring.LogEntryBuffer;

import java.io.Serializable;

/**
 * This class represents the Blockade order.
 *
 * @author Joshua Onyema
 * @author Safin Mahesania
 * @version 1.0
 */
public class Blockade implements Order, Serializable {
    private Country l_country;
    private GameSession d_gameSession;

    public Blockade(Country p_country) {
        this.l_country = p_country;
    }

    public GameSession getD_gameSession() {
        return d_gameSession;
    }

    public void setD_gameSession(GameSession p_gameSession) {
        this.d_gameSession = p_gameSession;
    }

    @Override
    public void execute() {
        int currentArmies = d_gameSession.getMap().getCountriesById(l_country.getCountryId()).getExistingArmies();
        d_gameSession.getMap().getCountriesById(l_country.getCountryId()).setExistingArmies(currentArmies * 3);
        d_gameSession.getMap().getCountriesById(l_country.getCountryId()).setD_isTerritoryNeutral(true);
        d_gameSession.getMap().getCountriesById(l_country.getCountryId()).setD_ownedBy(null);

        LogEntryBuffer.getInstance().setValue("🛡️ Blockade: " + l_country.getName() + " tripled to " + (currentArmies * 3) + " armies and set to neutral.");
    }
}
