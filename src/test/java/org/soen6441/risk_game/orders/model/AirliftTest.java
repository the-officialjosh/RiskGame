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

public class AirliftTest {
    private GameMapController gameMapController;
    private GameSession gameSession;

    @BeforeEach
    public void setUp() {
        gameMapController = new GameMapController();
        gameSession = GameSession.getInstance();
    }

    @Test
    public void testAirlift() {
        gameMapController.loadMap(gameSession, "europe.map");
        List<Player> players = new ArrayList<>();
        players.add(new Player("Player1", 0, new ArrayList<>()));
        players.add(new Player("Player2", 0, new ArrayList<>()));
        gameSession.setPlayers(players);
        gameMapController.assignCountries(gameSession);
        gameMapController.assignReinforcements(gameSession);

        Country p1Country1 = gameSession.getPlayers().get(0).getD_countries_owned().getFirst();
        Deploy order1 = new Deploy(gameSession.getPlayers().get(0), 2, p1Country1.getCountryId());
        Country p1Country2 = gameSession.getPlayers().get(0).getD_countries_owned().getLast();
        Deploy order2 = new Deploy(gameSession.getPlayers().get(0), 2, p1Country2.getCountryId());
        Country p2Country = gameSession.getPlayers().get(1).getD_countries_owned().getFirst();
        Deploy order3 = new Deploy(gameSession.getPlayers().get(1), 2, p2Country.getCountryId());

        order1.execute();
        order2.execute();
        order3.execute();

        gameSession.getPlayers().get(0).setD_cards_owned(3);

        //If source country and target country belong to same player.
        String command = "Airlift " + gameSession.getMap().getCountriesById(p1Country1.getCountryId()).getCountryId() + " " + gameSession.getMap().getCountriesById(p1Country2.getCountryId()).getCountryId() + " 1";
        String[] commandParts = command.split(" ");
        gameSession.getPlayers().get(0).processAirliftCommand(commandParts);
        gameSession.getPlayers().get(0).next_order();
        assertEquals(3, gameSession.getMap().getCountriesById(p1Country2.getCountryId()).getExistingArmies());

        //If source country and target country belong to different players.
        String command2 = "Airlift " + gameSession.getMap().getCountriesById(p1Country1.getCountryId()).getCountryId() + " " + gameSession.getMap().getCountriesById(p2Country.getCountryId()).getCountryId() + " 1";
        String[] commandParts2 = command2.split(" ");
        gameSession.getPlayers().get(0).processAirliftCommand(commandParts2);
        gameSession.getPlayers().get(0).next_order();
        assertEquals(2, gameSession.getMap().getCountriesById(p2Country.getCountryId()).getExistingArmies());
    }
}