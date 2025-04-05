package org.soen6441.risk_game.orders.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.controller.GameMapController;
import org.soen6441.risk_game.game_map.model.Country;
import org.soen6441.risk_game.player_management.model.HumanPlayer;
import org.soen6441.risk_game.player_management.model.Player;
import org.soen6441.risk_game.game_map.adapter.MapFormatDetector;
import org.soen6441.risk_game.game_map.adapter.MapFileHandler;
import org.soen6441.risk_game.game_map.adapter.ConquestMapFileAdapter;
import org.soen6441.risk_game.game_map.adapter.DominationMapFileHandler;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The type Reinforcement test.
 */
public class ReinforcementTest {
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
     * Test reinforcment.
     */
    @Test
    public void testReinforcment() {
        MapFileHandler mapFileHandler;
        String mapFormat = MapFormatDetector.detectFormat("europe.map");
        if ("conquest".equalsIgnoreCase(mapFormat)) {
            mapFileHandler = new ConquestMapFileAdapter();
        } else {
            mapFileHandler = new DominationMapFileHandler();
        }
        mapFileHandler.loadMap(gameSession, "europe.map");
        List<Player> players = new ArrayList<>();
        players.add(new Player("Player1", 0, new ArrayList<>(), gameSession));
        players.add(new Player("Player2", 0, new ArrayList<>(), gameSession));
        gameSession.setPlayers(players);
        gameMapController.assignCountries(gameSession);
        gameMapController.assignReinforcements(gameSession);

        Country p1Country = gameSession.getPlayers().get(0).getD_countries_owned().getFirst();
        String command = "Deploy " + String.valueOf(p1Country.getCountryId()) + " 3";
        String[] commandParts = command.split(" ");
        HumanPlayer humanPlayer = new HumanPlayer(gameSession.getPlayers().getFirst(),gameSession);
        humanPlayer.processDeployCommand(commandParts);
        gameSession.getPlayers().getFirst().next_order();
        gameSession.getPlayers().getFirst().setD_cards_owned(1);
        humanPlayer.processReinforcementCommand();
        gameSession.getPlayers().getFirst().next_order();

        assertEquals(6, gameSession.getPlayers().getFirst().getNumberOfReinforcementsArmies());
    }
}
