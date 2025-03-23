package org.soen6441.risk_game.orders.model;

import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.model.Country;
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
        int l_currentArmies = l_gameSession.getMap().getCountriesById(l_country.getCountryId()).getExistingArmies().get(l_player);
        HashMap<Player, Integer> map = new HashMap<>();
        map.put(l_player, (l_currentArmies * 3));
        l_gameSession.getMap().getCountriesById(l_country.getCountryId()).setExistingArmies(map);
        l_gameSession.getMap().getCountriesById(l_country.getCountryId()).setD_isTerritoryNeutral(true);
    }
}
