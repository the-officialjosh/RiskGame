package org.soen6441.risk_game.game_map.controller;

import java.util.ArrayList; // map tests
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.model.GameMap;
import org.soen6441.risk_game.player_management.model.Player;

public class GameMapControllerTest {

    private GameMapController gameMapController;
    private GameSession gameSession;
    private GameMap gameMap;

    @BeforeEach
    public void setUp() {
        gameMapController = new GameMapController();
        gameSession = GameSession.getInstance();
        gameMap = new GameMap("my_map", new ArrayList<>());
        gameSession.setMap(gameMap);

    }

    @Test
    public void testAddContinent() {
        gameMapController.addContinent(gameMap, "Asia", 5);
        assertEquals(1, gameMap.getContinents().size());
        assertEquals("Asia", gameMap.getContinents().get(0).getName());
    }

    @Test
    public void testRemoveContinent() {
        gameMapController.addContinent(gameMap, "Asia", 5);
        gameMapController.removeContinent(gameMap, "Asia");
        assertEquals(0, gameMap.getContinents().size());
    }

    @Test
    public void testAddCountry() {
        gameMapController.addContinent(gameMap, "Asia", 5);
        gameMapController.addCountry(gameMap, "India", "Asia");
        assertEquals(1, gameMap.getContinents().get(0).getCountries().size());
        assertEquals("India", gameMap.getContinents().get(0).getCountries().get(0).getName());
    }

    @Test
    public void testRemoveCountry() {
        gameMapController.addContinent(gameMap, "Asia", 5);
        gameMapController.addCountry(gameMap, "India", "Asia");
        gameMapController.removeCountry(gameMap, "India");
        assertEquals(0, gameMap.getContinents().get(0).getCountries().size());
    }

    @Test
    public void testConnectedMapIsValid() {
        gameMapController.loadMap(gameSession, "europe.map");
        assertEquals(gameMapController.validateMap(gameSession.getMap()), true);
    }

    @Test
    public void testUnconnectedMapIsInvalid() {
        gameMapController.loadMap(gameSession, "tests/bigeurope-unconnected.map");
        assertEquals(gameMapController.validateMap(gameSession.getMap()), false);
    }

    @Test
    public void testUnconnectedContinentIsInvalid() {
        gameMapController.loadMap(gameSession, "tests/bigeurope-continent-subgraph-unconnected.map");
        assertEquals(gameMapController.validateMap(gameSession.getMap()), false);
    }

    @Test
    public void testReinforcementFor2Players() {
        gameMapController.loadMap(gameSession, "europe.map");
        List<Player> players = new ArrayList<>();
        players.add(new Player("Player1", 0, new ArrayList<>()));
        players.add(new Player("Player2", 0, new ArrayList<>()));
        gameSession.setPlayers(players);
        gameMapController.assignCountries(gameSession);
        gameMapController.assignReinforcements(gameSession);
        assertEquals(4, gameSession.getPlayers().get(0).getNumberOfReinforcementsArmies());
        assertEquals(4, gameSession.getPlayers().get(1).getNumberOfReinforcementsArmies());
    }

    @Test
    public void testReinforcementFor5Players() {
        gameMapController.loadMap(gameSession, "europe.map");
        List<Player> players = new ArrayList<>();
        players.add(new Player("Player1", 0, new ArrayList<>()));
        players.add(new Player("Player2", 0, new ArrayList<>()));
        players.add(new Player("Player3", 0, new ArrayList<>()));
        players.add(new Player("Player4", 0, new ArrayList<>()));
        players.add(new Player("Player5", 0, new ArrayList<>()));
        gameSession.setPlayers(players);
        gameMapController.assignCountries(gameSession);
        gameMapController.assignReinforcements(gameSession);
        assertEquals(3, gameSession.getPlayers().get(0).getNumberOfReinforcementsArmies());
        assertEquals(3, gameSession.getPlayers().get(1).getNumberOfReinforcementsArmies());
        assertEquals(3, gameSession.getPlayers().get(2).getNumberOfReinforcementsArmies());
        assertEquals(3, gameSession.getPlayers().get(3).getNumberOfReinforcementsArmies());
        assertEquals(3, gameSession.getPlayers().get(4).getNumberOfReinforcementsArmies());
    }
}