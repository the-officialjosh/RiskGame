package org.soen6441.risk_game.orders.model;

import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.model.Country;

/**
 * The type Airlift.
 * @author Irfan Maknojia
 * @author Safin Mahesania
 */
public class Airlift implements Order {
    private Country d_sourceCountry;
    private Country d_targetCountry;
    private int d_numberOfArmies;

    /**
     * Instantiates a new Airlift.
     *
     * @param p_sourceCountry  the p source country
     * @param p_targetCountry  the p target country
     * @param p_numberOfArmies the p number of armies
     */
    public Airlift(Country p_sourceCountry, Country p_targetCountry, int p_numberOfArmies) {
        this.d_sourceCountry = p_sourceCountry;
        this.d_targetCountry = p_targetCountry;
        this.d_numberOfArmies = p_numberOfArmies;
    }

    @Override
    public void execute() {
        GameSession l_gameSession = GameSession.getInstance();
        if (d_sourceCountry.getExistingArmies() < d_numberOfArmies) {
            l_gameSession.getMap().getCountriesById(d_sourceCountry.getCountryId()).getD_ownedBy().setD_cards_owned(3);
            return;
        }

        int l_sourceCountryExistingArmies = d_sourceCountry.getExistingArmies();
        int l_targetCountryExistingArmies = d_targetCountry.getExistingArmies();

        l_gameSession.getMap().getCountriesById(d_sourceCountry.getCountryId()).setExistingArmies(l_sourceCountryExistingArmies - d_numberOfArmies);
        l_gameSession.getMap().getCountriesById(d_targetCountry.getCountryId()).setExistingArmies(l_targetCountryExistingArmies + d_numberOfArmies);
    }
}
