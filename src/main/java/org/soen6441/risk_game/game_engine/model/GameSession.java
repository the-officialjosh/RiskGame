package org.soen6441.risk_game.game_engine.model;

import org.soen6441.risk_game.game_map.model.GameMap;
import org.soen6441.risk_game.game_map.model.Country;
import org.soen6441.risk_game.player_management.model.Player;

import java.util.List;
import java.util.Map;

/**
 * GameSession class which represents the actual game state and holds
 * all the information about the game.
 */
public class GameSession {
    private static final GameSession instance = new GameSession();
    private List<Player> d_players;
    private GameMap d_map;
    //private Map<Country, Player> d_countriesControllers;

    private GameSession() {}

    public static GameSession getInstance() {
        return instance;
    }

    /**
     * Returns the list of players.
     * @return
     */
    public List<Player> getPlayers() {
        return d_players;
    }

    /**
     * Sets the list of players
     * @param p_players
     */
    public void setPlayers(List<Player> p_players) {
        this.d_players = p_players;
    }

    /**
     * Returns the game global map.
     * @return d_map The game map.
     */
    public GameMap getMap() {
        return d_map;
    }

    /**
     * Sets the game map.
     * @param p_map The game map.
     */
    public void setMap(GameMap p_map) {
        this.d_map = p_map;
    }

    /**
     * Returns the countries controllers.
     * @return The countries controllers.
     */
    /*public Map<Country, Player> getCountriesControllers() {
        return d_countriesControllers;
    }*/

    /**
     * Sets the countries controllers.
     * @param p_territoriesControllers The countries controllers.
     */
    /*public void setCountriesControllers(Map<Country, Player> p_territoriesControllers) {
        this.d_countriesControllers = p_territoriesControllers;
    }*/
}
