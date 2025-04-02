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
 * The type Blockade test.
 */
public class BlockadeTest {
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
     * Test blockade.
     */
    @Test
    public void testBlockade() {
        gameMapController.loadMap(gameSession, "europe.map");
        List<Player> players = new ArrayList<>();
        players.add(new Player("Player1", 0, new ArrayList<>(), gameSession));
        players.add(new Player("Player2", 0, new ArrayList<>(), gameSession));
        gameSession.setPlayers(players);
        gameMapController.assignCountries(gameSession);
        gameMapController.assignReinforcements(gameSession);

        Country p1Country = gameSession.getPlayers().get(0).getD_countries_owned().getFirst();
        Deploy order1 = new Deploy(gameSession.getPlayers().get(0), 2, p1Country.getCountryId());
        order1.setD_gameSession(gameSession);
        Country p2Country = gameSession.getPlayers().get(1).getD_countries_owned().getFirst();
        Deploy order2 = new Deploy(gameSession.getPlayers().get(1), 4, p2Country.getCountryId());
        order2.setD_gameSession(gameSession);

        order1.execute();
        order2.execute();

        gameSession.getPlayers().get(0).setD_cards_owned(2);
        HumanPlayer humanPlayer = new HumanPlayer(gameSession.getPlayers().get(0),gameSession);
        humanPlayer.processBlockadeCommand(String.valueOf(p1Country.getCountryId()));
        gameSession.getPlayers().get(0).next_order();
        assertEquals(6, gameSession.getMap().getCountriesById(p1Country.getCountryId()).getExistingArmies());
        assertEquals(null, gameSession.getMap().getCountriesById(p1Country.getCountryId()).getD_ownedBy());
        assertEquals(true, gameSession.getMap().getCountriesById(p1Country.getCountryId()).isD_isTerritoryNeutral());


        humanPlayer.processBlockadeCommand(String.valueOf(p2Country.getCountryId()));
        assertEquals(4, gameSession.getMap().getCountriesById(p2Country.getCountryId()).getExistingArmies());
        assertEquals(players.get(1), gameSession.getMap().getCountriesById(p2Country.getCountryId()).getD_ownedBy());
        assertEquals(false, gameSession.getMap().getCountriesById(p2Country.getCountryId()).isD_isTerritoryNeutral());
    }
}
