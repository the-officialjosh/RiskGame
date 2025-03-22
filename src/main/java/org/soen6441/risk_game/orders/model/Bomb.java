package org.soen6441.risk_game.orders.model;

import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.model.Country;
import org.soen6441.risk_game.player_management.model.Player;
import java.util.HashMap;

/**
 * The type Bomb.
 */
public class Bomb implements Order {

    /**
     * The D player.
     */
    Player d_player;
    /**
     * The D source country.
     */
    Country d_sourceCountry;
    /**
     * The D target country.
     */
    Country d_targetCountry;

    /**
     * Instantiates a new Bomb.
     *
     * @param p_sourceCountry the p source country
     * @param p_player        the p player
     * @param p_targetCountry the p target country
     */
    public Bomb(Country p_sourceCountry, Player p_player, Country p_targetCountry) {
        this.d_sourceCountry = p_sourceCountry;
        this.d_player = p_player;
        this.d_targetCountry = p_targetCountry;
    }

    @Override
    public void execute() {
        GameSession l_gameSession = GameSession.getInstance();
        Player targetCountryOwnedBy = l_gameSession.getMap().getCountriesById(d_targetCountry.getCountryId()).getD_ownedBy();
        int l_armies = (l_gameSession.getMap().getCountriesById(d_targetCountry.getCountryId()).getExistingArmies().get(targetCountryOwnedBy)) / 2;
        HashMap<Player, Integer> map = new HashMap<>();
        map.put(targetCountryOwnedBy, l_armies);
        l_gameSession.getMap().getCountriesById(d_targetCountry.getCountryId()).setExistingArmies(map);
    }
}