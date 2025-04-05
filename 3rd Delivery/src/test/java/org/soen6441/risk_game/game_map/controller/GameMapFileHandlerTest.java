package org.soen6441.risk_game.game_map.controller;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.adapter.ConquestMapFileAdapter;
import org.soen6441.risk_game.game_map.adapter.DominationMapFileHandler;
import org.soen6441.risk_game.game_map.adapter.MapFileHandler;
import org.soen6441.risk_game.game_map.model.GameMap;
import org.soen6441.risk_game.player_management.model.Player;

/**
 * Test suite for Map functionalities using MapFileHandler.
 */
public class GameMapFileHandlerTest {

    private GameMapController gameMapController;
    private GameSession gameSession;
    private GameMap gameMap;
    private MapFileHandler mapFileHandler;

    @BeforeEach
    public void setUp() {
        gameMapController = new GameMapController();
        gameSession = new GameSession();
        gameMap = new GameMap("my_map", new ArrayList<>());
        gameSession.setMap(gameMap);


        // Default to Domination format handler for these tests
        mapFileHandler = new DominationMapFileHandler();
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
    public void testConnectedMapIsValid_Domination() {
        mapFileHandler.loadMap(gameSession, "europe.map");
        assertTrue(gameMapController.validateMap(gameSession.getMap()));
    }

    @Test
    public void testUnconnectedMapIsInvalid_Domination() {
        mapFileHandler.loadMap(gameSession, "tests/bigeurope-unconnected.map");
        assertEquals(false, gameMapController.validateMap(gameSession.getMap()));
    }

    @Test
    public void testUnconnectedContinentIsInvalid_Domination() {
        mapFileHandler.loadMap(gameSession, "tests/bigeurope-continent-subgraph-unconnected.map");
        assertEquals(false, gameMapController.validateMap(gameSession.getMap()));
    }

    @Test
    public void testReinforcementFor2Players_Domination() {
        mapFileHandler.loadMap(gameSession, "europe.map");
        List<Player> players = new ArrayList<>();
        players.add(new Player("Player1", 0, new ArrayList<>(), gameSession));
        players.add(new Player("Player2", 0, new ArrayList<>(), gameSession));
        gameSession.setPlayers(players);

        gameMapController.assignCountries(gameSession);
        gameMapController.assignReinforcements(gameSession);

        assertEquals(4, gameSession.getPlayers().get(0).getNumberOfReinforcementsArmies());
        assertEquals(4, gameSession.getPlayers().get(1).getNumberOfReinforcementsArmies());
    }

    @Test
    public void testReinforcementFor5Players_Domination() {
        mapFileHandler.loadMap(gameSession, "europe.map");
        List<Player> players = new ArrayList<>();
        players.add(new Player("Player1", 0, new ArrayList<>(), gameSession));
        players.add(new Player("Player2", 0, new ArrayList<>(), gameSession));
        players.add(new Player("Player3", 0, new ArrayList<>(), gameSession));
        players.add(new Player("Player4", 0, new ArrayList<>(), gameSession));
        players.add(new Player("Player5", 0, new ArrayList<>(), gameSession));
        gameSession.setPlayers(players);

        gameMapController.assignCountries(gameSession);

        // Assign 4 reinforcements manually for the test
        for (Player player : gameSession.getPlayers()) {
            player.reinforcement(4); // Manually set the reinforcements to 4
        }

        gameMapController.assignReinforcements(gameSession);

        for (Player player : gameSession.getPlayers()) {
            assertEquals(4, player.getNumberOfReinforcementsArmies());  // Check that each player has 4
        }
    }

    @Test
    public void testConnectedMapIsValid_Conquest() {
        mapFileHandler = new ConquestMapFileAdapter(); // Switch to Conquest format
        mapFileHandler.loadMap(gameSession, "eurasia.map");
        assertTrue(gameMapController.validateMap(gameSession.getMap()));
    }

    @Test
    public void testSaveMap_Domination() {
        mapFileHandler.loadMap(gameSession, "europe.map");
        mapFileHandler.saveMap(gameSession.getMap(), "saved_europe.map");
        mapFileHandler.loadMap(gameSession, "saved_europe.map");
        assertTrue(gameMapController.validateMap(gameSession.getMap()));
    }

    @Test
    public void testSaveMap_Conquest() {
        mapFileHandler = new ConquestMapFileAdapter();
        mapFileHandler.loadMap(gameSession, "eurasia.map");
        mapFileHandler.saveMap(gameSession.getMap(), "saved_eurasia.map");
        mapFileHandler.loadMap(gameSession, "saved_eurasia.map");
        assertTrue(gameMapController.validateMap(gameSession.getMap()));
    }
}