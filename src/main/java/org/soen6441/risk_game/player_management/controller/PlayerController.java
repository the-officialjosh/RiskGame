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
            l_command = l_scanner.nextLine().trim();

            // Catch user action for monitoring observer
            LogEntryBuffer.getInstance().setValue(l_command);

            String[] l_commandArray = l_command.split(" ");

            if (l_command.equals("assigncountries")) {
                if (playerList.isEmpty()) {
                    d_displayToUser.instructionMessage("⚠ Add at least one player before assigning countries.");
                    continue;
                }
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
        boolean allArmiesDeployed;
        d_gameMapController.showMap(p_gameSession.getMap());
        for (Player player : p_gameSession.getPlayers()) {
            player.reinforcement(3); // Assign reinforcements once before deployment begins
        }
        do {
            allArmiesDeployed = true;
            for (Player player : p_gameSession.getPlayers()) {
                d_displayToUser.instructionMessage("\n⚔ Reinforcement Phase");
                d_displayToUser.instructionMessage("==========================");
                d_displayToUser.instructionMessage("Use \"deploy <country_id> <number_of_armies>\" to deploy\n");
                d_displayToUser.instructionMessage(player.getName() + " you have (" + player.getNumberOfReinforcementsArmies() + ") reinforcement armies.");

                if (player.hasReinforcementsArmies()) {
                    player.issue_order(true);
                } else {
                    d_displayToUser.instructionMessage(player.getName() + " has no armies left to deploy.");
                }
                if (!player.isReinforcementPhaseComplete()) {
                    allArmiesDeployed = false;
                }
            }
        } while (!allArmiesDeployed);
        d_displayToUser.instructionMessage("✔ All armies have been deployed.");
    }

    /**
     * This method handles the general order phase after reinforcements.
     *
     * @param p_gameSession The game session.
     */
    public void issueGeneralOrders(GameSession p_gameSession) {
        d_displayToUser.instructionMessage("\n🧠 Order Issue Phase");
        for (Player player : p_gameSession.getPlayers()) {
            d_displayToUser.instructionMessage("🎯 " + player.getName() + ", it's your turn to issue an order.");
            player.issue_order(false);
        }
    }

    /**
     * This method handles the order execution phase.
     *
     * @param p_gameSession The game session.
     */
    public void executeOrder(GameSession p_gameSession) {
        d_displayToUser.instructionMessage("\n🚀 Executing Orders...\n");
        for (Player player : p_gameSession.getPlayers()) {
            player.next_order();
            LogEntryBuffer.getInstance().setValue("Executed orders for: " + player.getName());
        }
    }
}
