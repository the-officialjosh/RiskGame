import java.util.ArrayList; // map tests

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.controller.GameMapController;
import org.soen6441.risk_game.game_map.model.GameMap;

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
}