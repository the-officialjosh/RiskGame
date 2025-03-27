package org.soen6441.risk_game.orders.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.controller.GameMapController;
import org.soen6441.risk_game.game_map.model.Country;
import org.soen6441.risk_game.player_management.model.Player;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BombTest {
    private GameMapController gameMapController;
    private GameSession gameSession;

    @BeforeEach
    public void setUp() {
        gameMapController = new GameMapController();
        gameSession = GameSession.getInstance();
    }

    @Test
    public void testBomb() {
        gameMapController.loadMap(gameSession, "europe.map");
        List<Player> players = new ArrayList<>();
        players.add(new Player("Player1", 0, new ArrayList<>()));
        players.add(new Player("Player2", 0, new ArrayList<>()));
        gameSession.setPlayers(players);
        gameMapController.assignCountries(gameSession);
        gameMapController.assignReinforcements(gameSession);

        Country p1Country = gameSession.getPlayers().get(0).getD_countries_owned().getFirst();
        Deploy order1 = new Deploy(gameSession.getPlayers().get(0), 4, p1Country.getCountryId());
        Country p2Country = gameSession.getPlayers().get(1).getD_countries_owned().getFirst();
        Deploy order2 = new Deploy(gameSession.getPlayers().get(1), 4, p2Country.getCountryId());

        order1.execute();
        order2.execute();

        gameSession.getPlayers().get(0).setD_cards_owned(0);

        if (p1Country.getAdjacentCountries().contains(p2Country)) {
            gameSession.getPlayers().get(0).processBombCommand(String.valueOf(p1Country.getCountryId()), String.valueOf(p2Country.getCountryId()));
            gameSession.getPlayers().get(0).next_order();
            assertEquals(2, gameSession.getMap().getCountriesById(p2Country.getCountryId()).getExistingArmies());
        } else {
            gameSession.getPlayers().get(0).processBombCommand(String.valueOf(p1Country.getCountryId()), String.valueOf(p2Country.getCountryId()));
            assertEquals(4, gameSession.getMap().getCountriesById(p2Country.getCountryId()).getExistingArmies());
        }


        //assertEquals(4, gameSession.getPlayers().get(0).getNumberOfReinforcementsArmies());
        //assertEquals(4, gameSession.getPlayers().get(1).getNumberOfReinforcementsArmies());
//        Player player = new Player("Player1",3,new ArrayList<Order>());
//        boolean result = player.validNumberOfReinforcementArmies(5);
//        assertEquals(false,result);
    }
}
