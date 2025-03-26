package org.soen6441.risk_game.orders.model;

import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.model.Country;
import org.soen6441.risk_game.player_management.model.Player;

import java.util.HashMap;

/**
 * The type Airlift.
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

        int l_sourceCountryExistingArmies = d_sourceCountry.getExistingArmies().get(d_sourceCountry.getD_ownedBy());
        int l_targetCountryExistingArmies = d_targetCountry.getExistingArmies().get(d_targetCountry.getD_ownedBy());

        HashMap<Player, Integer> sourceMap = new HashMap<>();
        HashMap<Player, Integer> targetMap = new HashMap<>();

        sourceMap.put(d_sourceCountry.getD_ownedBy(), l_sourceCountryExistingArmies - d_numberOfArmies);
        targetMap.put(d_targetCountry.getD_ownedBy(), l_targetCountryExistingArmies + d_numberOfArmies);

        l_gameSession.getMap().getCountriesById(d_sourceCountry.getCountryId()).setExistingArmies(sourceMap);
        l_gameSession.getMap().getCountriesById(d_targetCountry.getCountryId()).setExistingArmies(targetMap);
    }
}
