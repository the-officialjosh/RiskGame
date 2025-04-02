package org.soen6441.risk_game.orders.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.controller.GameMapController;
import org.soen6441.risk_game.game_map.model.Country;
import org.soen6441.risk_game.player_management.model.HumanPlayer;
import org.soen6441.risk_game.player_management.model.Player;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The type Bomb test.
 */
public class BombTest {
    private GameMapController gameMapController;
    private GameSession gameSession;

    /**
     * Sets up.
     */
    @BeforeEach
    public void setUp() {
        gameMapController = new GameMapController();
        gameSession = new GameSession();
    }

    /**
     * Test bomb.
     */
    @Test
    public void testBomb() {
        gameMapController.loadMap(gameSession, "europe.map");
        List<Player> players = new ArrayList<>();
        players.add(new Player("Player1", 0, new ArrayList<>(), gameSession));
        players.add(new Player("Player2", 0, new ArrayList<>(), gameSession));
        gameSession.setPlayers(players);
        gameMapController.assignCountries(gameSession);
        gameMapController.assignReinforcements(gameSession);

        Country p1Country = gameSession.getPlayers().get(0).getD_countries_owned().getFirst();
        Deploy order1 = new Deploy(gameSession.getPlayers().get(0), 4, p1Country.getCountryId());
        order1.setD_gameSession(gameSession);
        Country p2Country = gameSession.getPlayers().get(1).getD_countries_owned().getFirst();
        Deploy order2 = new Deploy(gameSession.getPlayers().get(1), 4, p2Country.getCountryId());
        order2.setD_gameSession(gameSession);

        order1.execute();
        order2.execute();

        gameSession.getPlayers().get(0).setD_cards_owned(0);
        HumanPlayer humanPlayer = new HumanPlayer(gameSession.getPlayers().get(0),gameSession);
        if (p1Country.getAdjacentCountries().contains(p2Country)) {
            humanPlayer.processBombCommand(String.valueOf(p1Country.getCountryId()), String.valueOf(p2Country.getCountryId()));
            gameSession.getPlayers().get(0).next_order();
            assertEquals(2, gameSession.getMap().getCountriesById(p2Country.getCountryId()).getExistingArmies());
        } else {
            humanPlayer.processBombCommand(String.valueOf(p1Country.getCountryId()), String.valueOf(p2Country.getCountryId()));
            assertEquals(4, gameSession.getMap().getCountriesById(p2Country.getCountryId()).getExistingArmies());
        }
    }
}
