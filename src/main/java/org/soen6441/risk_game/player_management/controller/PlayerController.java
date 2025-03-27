package org.soen6441.risk_game.player_management.controller;

import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.controller.GameMapController;
import org.soen6441.risk_game.game_map.view.DisplayToUser;
import org.soen6441.risk_game.monitoring.LogEntryBuffer;
import org.soen6441.risk_game.orders.model.Order;
import org.soen6441.risk_game.player_management.model.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * This class holds all the logic for players management.
 *
 * @author Irfan Maknojia
 * @author Joshua Onyema
 * @author Safin Mahesania
 * @author Ahmed Fakhir
 * @version 1.0
 */
public class PlayerController {

    private DisplayToUser d_displayToUser = new DisplayToUser();
    private GameMapController d_gameMapController = new GameMapController();

    /**
     * This method handles the players loading step.
     *
     * @param p_gameSession The game session.
     */
    public void loadPlayers(GameSession p_gameSession) {
        d_displayToUser.instructionMessage("=====================================");
        d_displayToUser.instructionMessage("       PLAYER MANAGEMENT STEP        ");
        d_displayToUser.instructionMessage("=====================================");
        d_displayToUser.instructionMessage("Use the following commands to manage players:");
        d_displayToUser.instructionMessage("- ⚒ gameplayer -add <playername>  (Add a new player)");
        d_displayToUser.instructionMessage("- ⚒ gameplayer -remove <playername>  (Remove a player)");
        d_displayToUser.instructionMessage("- ⚒ assigncountries  (Assign countries to players)");
        d_displayToUser.instructionMessage("----------------------------------------\n");

        Scanner l_scanner = new Scanner(System.in);
        String l_command;
        List<Player> playerList = new ArrayList<>();

        do {
            l_command = l_scanner.nextLine();

            // Catch user action for monitoring observer
            LogEntryBuffer.getInstance().setValue(l_command);

            String[] l_commandArray = l_command.split(" ");

            if (!playerList.isEmpty() && l_command.equals("assigncountries")) {
                break;
            }

            if (!l_commandArray[0].equals("gameplayer")) {
                d_displayToUser.instructionMessage("⚠ Invalid Command! Use: gameplayer -add <playername> or gameplayer -remove <playername>");
                continue;
            }

            if (l_commandArray.length < 3) {
                d_displayToUser.instructionMessage("⚠ Invalid Syntax! Correct usage: gameplayer -add <playername>");
                continue;
            }

            String l_action = l_commandArray[1];
            String l_playerName = l_commandArray[2];

            if (l_action.equals("-add")) {
                Player playerToAdd = new Player(l_playerName, 0, new ArrayList<>());
                boolean exists = playerList.stream().anyMatch(player -> player.getName().equalsIgnoreCase(playerToAdd.getName()));
                if (exists) {
                    d_displayToUser.instructionMessage("⚠ Player already exists. Try a different name.");
                    continue;
                }
                playerList.add(playerToAdd);
                d_displayToUser.instructionMessage("✔ Player " + l_playerName + " added.");
            } else if (l_action.equals("-remove")) {
                if (!playerList.isEmpty()) {
                    boolean isRemoved = playerList.removeIf(player -> player.getName().equalsIgnoreCase(l_playerName));
                    if (isRemoved) {
                        d_displayToUser.instructionMessage("✔ Player " + l_playerName + " removed.");
                    } else {
                        d_displayToUser.instructionMessage("⚠ Player not found.");
                    }
                } else {
                    d_displayToUser.instructionMessage("⚠ No players to remove.");
                }
            } else {
                d_displayToUser.instructionMessage("⚠ Invalid action. Use -add or -remove.");
            }
        } while (true);

        p_gameSession.setPlayers(playerList);
    }

    /**
     * This method handles the reinforcement phase.
     *
     * @param p_gameSession The game session.
     */
    public void issueOrderPhase(GameSession p_gameSession) {
        d_gameMapController.showMap(p_gameSession.getMap());
        for (Player player : p_gameSession.getPlayers()) {
            d_displayToUser.instructionMessage("\n⚔ Issue Order Phase");
            d_displayToUser.instructionMessage("==========================");
            d_displayToUser.instructionMessage("Use \"Deploy <country_id> <number_of_armies>\" to deploy");
            d_displayToUser.instructionMessage("Use \"Advance <fromCountry_id> <toCountry_id> <number_of_armies>\" to Advance");
            d_displayToUser.instructionMessage("Use \"Blockade <targetCountryID>\" to use blockade card");
            d_displayToUser.instructionMessage("Use \"Bomb <sourceCountryID> <targetCountryID>\" to use bomb card");
            d_displayToUser.instructionMessage("Use \"Reinforcement\" to use reinforcement card");
            //d_displayToUser.instructionMessage("Use \"Advance <fromCountry_id> <toCountry_id> <number_of_armies>\" to Advance");

            d_displayToUser.instructionMessage("Use \"Commit\" to complete orders\n");

            int[] cards = player.getD_cards_owned();
            d_displayToUser.instructionMessage("You have following cards:");
            d_displayToUser.instructionMessage("1. Bomb = "+cards[0]);
            d_displayToUser.instructionMessage("2. Reinforcement = "+cards[1]);
            d_displayToUser.instructionMessage("3. Blockade = "+cards[2]);
            d_displayToUser.instructionMessage("4. Airlift = "+cards[3]);
            d_displayToUser.instructionMessage("5. Diplomacy = "+cards[4]);

            player.issue_order();
        }
    }
    /**
     * This method handles the order execution phase.
     *
     * @param p_gameSession The game session.
     */
    public void executeOrder(GameSession p_gameSession) {
        for (Player player : p_gameSession.getPlayers()) {
            player.next_order();
        }
    }
}
