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
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * The type Advance test.
 */
public class AdvanceTest {
    private GameMapController gameMapController;
    private GameSession gameSession;

    /**
     * Sets up.
     */
    @BeforeEach
    public void setUp() {

    }

    /**
     * Test process advance command valid move.
     */
    @Test
    void testProcessAdvanceCommand_ValidMove() {
        gameMapController = new GameMapController();
        gameSession = new GameSession();
        gameMapController.loadMap(gameSession, "europe.map");

        List<Player> players = new ArrayList<>();
        players.add(new Player("Player1", 0, new ArrayList<>(), gameSession));
        players.add(new Player("Player2", 0, new ArrayList<>(), gameSession));
        gameSession.setPlayers(players);
        gameMapController.assignCountries(gameSession);

        Player attacker = gameSession.getPlayers().getFirst();

        Country fromCountry = attacker.getD_countries_owned().getFirst();
        Country toCountry = fromCountry.getAdjacentCountries().getFirst();

        String[] command = {"advance", fromCountry.getCountryId()+"", toCountry.getCountryId()+"", "5"};
        attacker.processAdvanceCommand(command);

        assertEquals(1, attacker.getOrders().size(), "Advance order should be added to player's order list");
    }

    /**
     * Test process advance command non adjacent country.
     */
    @Test
    void testProcessAdvanceCommand_NonAdjacentCountry() {
        gameMapController = new GameMapController();
        gameSession = new GameSession();
        gameMapController.loadMap(gameSession, "europe.map");

        List<Player> players = new ArrayList<>();
        players.add(new Player("Player1", 5, new ArrayList<>(), gameSession));
        players.add(new Player("Player2", 5, new ArrayList<>(), gameSession));
        gameSession.setPlayers(players);
        gameMapController.assignCountries(gameSession);

        Player attacker = gameSession.getPlayers().getFirst();
        Player defender = gameSession.getPlayers().getLast();

        Country fromCountry = attacker.getD_countries_owned().getFirst();
        Country toCountry = new Country(-1,"Non-AdjustantCountry",new ArrayList<>(),0);

        String[] command = {"advance", fromCountry.getCountryId()+"", toCountry.getCountryId()+"", "5"};
        attacker.processAdvanceCommand(command);

        assertEquals(0, attacker.getOrders().size(), "Advance order should be not added to player's order list");
    }

    /**
     * Test move armies not enough.
     */
    @Test
    void testMoveArmiesNotEnough() {
        gameMapController = new GameMapController();
        gameSession = new GameSession();
        gameMapController.loadMap(gameSession, "europe.map");

        List<Player> players = new ArrayList<>();
        players.add(new Player("Player1", 0, new ArrayList<>(), gameSession));
        players.add(new Player("Player2", 0, new ArrayList<>(), gameSession));
        gameSession.setPlayers(players);
        gameMapController.assignCountries(gameSession);

        Player attacker = gameSession.getPlayers().getFirst();

        Country fromCountry = attacker.getD_countries_owned().getFirst();
        Country toCountry = fromCountry.getAdjacentCountries().getFirst();
        fromCountry.setExistingArmies(5);
        toCountry.setExistingArmies(0);
        toCountry.setD_ownedBy(attacker);
        String[] command = {"advance", fromCountry.getCountryId()+"", toCountry.getCountryId()+"", "6"}; // advancing more armies than the country have.
        attacker.processAdvanceCommand(command);
        attacker.next_order();

        assertEquals(0, toCountry.getExistingArmies()); //should remain unchanged
    }

    /**
     * Test advance to owned country.
     */
    @Test
    void testAdvanceToOwnedCountry() {
        gameMapController = new GameMapController();
        gameSession = new GameSession();
        gameMapController.loadMap(gameSession, "europe.map");

        List<Player> players = new ArrayList<>();
        players.add(new Player("Player1", 5, new ArrayList<>(), gameSession));
        players.add(new Player("Player2", 5, new ArrayList<>(), gameSession));
        gameSession.setPlayers(players);
        gameMapController.assignCountries(gameSession);
        gameMapController.assignReinforcements(gameSession);

        Player attacker = gameSession.getPlayers().getFirst();

        Country fromCountry = attacker.getD_countries_owned().getFirst();
        Country toCountry = fromCountry.getAdjacentCountries().getFirst();
        fromCountry.setExistingArmies(5);
        toCountry.setExistingArmies(0);
        toCountry.setD_ownedBy(attacker);
        String[] command = {"advance", fromCountry.getCountryId()+"", toCountry.getCountryId()+"", "5"};
        attacker.processAdvanceCommand(command);
        attacker.next_order();

        assertEquals(0, fromCountry.getExistingArmies());
        assertEquals(5, toCountry.getExistingArmies());
    }

    /**
     * Test attack success.
     */
    @Test
    void testAttackSuccess() {
        gameMapController = new GameMapController();
        gameSession = new GameSession();
        gameMapController.loadMap(gameSession, "europe.map");

        List<Player> players = new ArrayList<>();
        players.add(new Player("Player1", 0, new ArrayList<>(), gameSession));
        players.add(new Player("Player2", 0, new ArrayList<>(), gameSession));
        gameSession.setPlayers(players);
        gameMapController.assignCountries(gameSession);

        Player attacker = gameSession.getPlayers().getFirst();
        Player defender = gameSession.getPlayers().getLast();

        Country fromCountry = attacker.getD_countries_owned().getFirst();
        Country toCountry = fromCountry.getAdjacentCountries().getFirst();
        toCountry.setD_ownedBy(defender);
        fromCountry.setExistingArmies(5);
        toCountry.setExistingArmies(2);

        String[] command = {"advance", fromCountry.getCountryId()+"", toCountry.getCountryId()+"", "5"};
        attacker.processAdvanceCommand(command);

        attacker.next_order();
        assertNotEquals(defender, toCountry.getD_ownedBy()); // Ownership should change
    }

    /**
     * Test defended successfully.
     */
    @Test
    void testDefendedSuccessfully() {
        gameMapController = new GameMapController();
        gameSession = new GameSession();
        gameMapController.loadMap(gameSession, "europe.map");

        List<Player> players = new ArrayList<>();
        players.add(new Player("Player1", 0, new ArrayList<>(), gameSession));
        players.add(new Player("Player2", 0, new ArrayList<>(), gameSession));
        gameSession.setPlayers(players);
        gameMapController.assignCountries(gameSession);

        Player attacker = gameSession.getPlayers().getFirst();
        Player defender = gameSession.getPlayers().getLast();

        Country fromCountry = attacker.getD_countries_owned().getFirst();
        Country toCountry = fromCountry.getAdjacentCountries().getFirst();
        toCountry.setD_ownedBy(defender);
        fromCountry.setExistingArmies(5);
        toCountry.setExistingArmies(10);

        String[] command = {"advance", fromCountry.getCountryId()+"", toCountry.getCountryId()+"", "5"};
        attacker.processAdvanceCommand(command);

        attacker.next_order();
        assertEquals(defender, toCountry.getD_ownedBy()); // Ownership should change
    }





}
