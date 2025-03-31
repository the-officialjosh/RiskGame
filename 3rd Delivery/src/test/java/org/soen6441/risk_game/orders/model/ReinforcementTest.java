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
        gameMapController.loadMap(gameSession, "europe.map");
        List<Player> players = new ArrayList<>();
        players.add(new Player("Player1", 0, new ArrayList<>(), gameSession));
        players.add(new Player("Player2", 0, new ArrayList<>(), gameSession));
        gameSession.setPlayers(players);
        gameMapController.assignCountries(gameSession);
        gameMapController.assignReinforcements(gameSession);

        Country p1Country = gameSession.getPlayers().get(0).getD_countries_owned().getFirst();
        String command = "Deploy " + String.valueOf(p1Country.getCountryId()) + " 3";
        String[] commandParts = command.split(" ");
        gameSession.getPlayers().getFirst().processDeployCommand(commandParts);
        gameSession.getPlayers().getFirst().next_order();
        gameSession.getPlayers().getFirst().setD_cards_owned(1);
        gameSession.getPlayers().getFirst().processReinforcementCommand();
        gameSession.getPlayers().getFirst().next_order();

        assertEquals(6, gameSession.getPlayers().getFirst().getNumberOfReinforcementsArmies());
    }
}
