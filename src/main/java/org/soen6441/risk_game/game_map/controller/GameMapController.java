package org.soen6441.risk_game.game_map.controller;
import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.model.Continent;
import org.soen6441.risk_game.game_map.model.Country;
import org.soen6441.risk_game.player_management.model.Player;
import java.util.*;

/**
 * GameMap controller class which is responsible for handling all
 * the map related logics.
 */
public class GameMapController {

    /**
     * Handles the load map step
     * @param p_gameSession The game session.
     */
    public void loadMap(GameSession p_gameSession) {

    }

    /**
     * Handles the assign countries step.
     * @param p_gameSession The game session.
     */
    public void assignCountries(GameSession p_gameSession) {
        List<Player> d_players = p_gameSession.getPlayers();
        List<Continent> d_continents = p_gameSession.getMap().getContinents();
        List<Country> d_countries = new ArrayList<Country>();
        Map<Country, Player> d_assignedSigned = new HashMap<>();

        for (int i = 0; i < d_continents.size(); i++) {
            for (int j = 0; j < d_continents.get(i).getCountries().size(); j++) {
                d_countries.add(d_continents.get(i).getCountries().get(i));
            }
        }

        for (int i = 0; i < d_countries.size(); i++) {
            Random rn = new Random();
            int randomNumber = rn.nextInt(d_countries.size());

            while (true) {
                if (d_countries.get(randomNumber).getD_ownedBy().isEmpty()) {
                    d_countries.get(randomNumber).setD_ownedBy(d_players.get(i).getName());
                    d_players.get(i).setD_countries_owned(d_countries.get(randomNumber));
                    d_assignedSigned.put(d_countries.get(randomNumber), d_players.get(i));
                    break;
                }
                randomNumber = rn.nextInt(d_countries.size());
            }
        }
        p_gameSession.setCountriesControllers(d_assignedSigned);
    }

    /**
     * Handles the assignReinforcements step.
     * @param p_gameSession The game session.
     */
    public void assignReinforcements(GameSession p_gameSession) {

    }
}
