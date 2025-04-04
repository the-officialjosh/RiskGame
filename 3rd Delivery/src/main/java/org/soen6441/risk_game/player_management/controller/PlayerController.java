package org.soen6441.risk_game.player_management.controller;

import org.soen6441.risk_game.game_engine.controller.user_input.UserInputScanner;
import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.controller.GameMapController;
import org.soen6441.risk_game.game_map.view.DisplayToUser;
import org.soen6441.risk_game.monitoring.LogEntryBuffer;
import org.soen6441.risk_game.orders.model.Order;
import org.soen6441.risk_game.player_management.model.*;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
public class PlayerController implements Serializable {

    private DisplayToUser d_displayToUser = new DisplayToUser();
    private GameMapController d_gameMapController = new GameMapController();

    public void loadPlayers(GameSession p_gameSession) {
        d_displayToUser.instructionMessage("=====================================");
        d_displayToUser.instructionMessage("       PLAYER MANAGEMENT STEP        ");
        d_displayToUser.instructionMessage("=====================================");
        d_displayToUser.instructionMessage("Use the following commands to manage players:");
        d_displayToUser.instructionMessage("- ⚒ gameplayer -add <playername>  (Add a new player)");
        d_displayToUser.instructionMessage("- ⚒ gameplayer -remove <playername>  (Remove a player)");
        d_displayToUser.instructionMessage("- ⚒ assigncountries  (Assign countries to players)");
        d_displayToUser.instructionMessage("----------------------------------------\n");

        Scanner l_scanner = UserInputScanner.getInstance().getScanner();
        String l_command;
        List<Player> playerList = new ArrayList<>();

        Pattern pattern = Pattern.compile("(gameplayer\\s+-\\w+\\s+[^\\s]+|assigncountries)");

        boolean assignCountries = false;

        do {
            l_command = l_scanner.nextLine();

            LogEntryBuffer.getInstance().setValue(l_command);

            Matcher matcher = pattern.matcher(l_command);
            while (matcher.find()) {
                String command = matcher.group(1).trim();
                String[] l_commandArray = command.split(" ");

                if (command.equals("assigncountries")) {
                    if (!playerList.isEmpty()) {
                        assignCountries = true;
                        break;
                    } else {
                        d_displayToUser.instructionMessage("⚠ No players to assign countries to.");
                        continue;
                    }
                }

                if (l_commandArray.length < 3) {
                    d_displayToUser.instructionMessage("⚠ Invalid Syntax! Correct usage: gameplayer -add <playername>");
                    continue;
                }

                if (l_commandArray[0].equals("gameplayer")) {
                    String l_action = l_commandArray[1];
                    String l_playerName;
                    String strategy = "human"; // Default strategy

                    if (l_action.equals("-add")) {
                        l_playerName = l_commandArray[2];
                        // Check for strategy parameter
                        for (int i = 3; i < l_commandArray.length; i++) {
                            if (l_commandArray[i].equals("-strategy") && i + 1 < l_commandArray.length) {
                                strategy = l_commandArray[i + 1].toLowerCase();
                                break;
                            }
                        }

                        Player playerToAdd = new Player(l_playerName, 0, new ArrayList<>(), p_gameSession);
                        switch (strategy) {
                            case "human":
                                playerToAdd.setD_playerStrategy(new HumanPlayer(playerToAdd, p_gameSession));
                                break;
                            case "aggressive":
                                playerToAdd.setD_playerStrategy(new AggressivePlayer(playerToAdd, p_gameSession));
                                break;
                            case "benevolent":
                                playerToAdd.setD_playerStrategy(new BenevolentPlayer(playerToAdd, p_gameSession));
                                break;
                            case "random":
                                playerToAdd.setD_playerStrategy(new RandomPlayer(playerToAdd, p_gameSession));
                                break;
                            case "cheater":
                                playerToAdd.setD_playerStrategy(new CheaterPlayer(playerToAdd, p_gameSession));
                                break;
                            default:
                                d_displayToUser.instructionMessage("⚠ Invalid strategy. Defaulting to Human.");
                                playerToAdd.setD_playerStrategy(new HumanPlayer(playerToAdd, p_gameSession));
                                break;
                        }

                        boolean exists = playerList.stream().anyMatch(p -> p.getName().equalsIgnoreCase(l_playerName));
                        if (exists) {
                            d_displayToUser.instructionMessage("⚠ Player " + l_playerName + " already exists.");
                        } else {
                            playerList.add(playerToAdd);
                            d_displayToUser.instructionMessage("✔ Player " + l_playerName + " added with " + strategy + " strategy.");
                        }
                    } else if (l_action.equals("-remove")) {
                        boolean removed = playerList.removeIf(player -> player.getName().equalsIgnoreCase(l_commandArray[2]));
                        if (removed) {
                            d_displayToUser.instructionMessage("✔ Player " + l_commandArray[2] + " removed.");
                        } else {
                            d_displayToUser.instructionMessage("⚠ Player " + l_commandArray[2] + " does not exist.");
                        }
                    } else {
                        d_displayToUser.instructionMessage("⚠ Invalid action. Use -add or -remove.");
                    }
                }
            }

            if (assignCountries) {
                break;
            }
        } while (true);

        p_gameSession.setPlayers(playerList);
    }

    /**
     * This method handles the order issuing phase, allowing players to issue orders.
     *
     * @param p_gameSession The game session.
     */
    public void issueOrderPhase(GameSession p_gameSession, int playerNumber) {
        d_gameMapController.showMap(p_gameSession.getMap());
        //for (Player player : p_gameSession.getPlayers()) {
        for (int i = playerNumber; i < p_gameSession.getPlayers().size(); i++) {
            d_displayToUser.instructionMessage("\n⚔ Issue Order Phase");
            d_displayToUser.instructionMessage("==========================");
            d_displayToUser.instructionMessage("Use \"Deploy <country_id> <number_of_armies>\" to deploy.");
            d_displayToUser.instructionMessage("Use \"Advance <fromCountry_id> <toCountry_id> <number_of_armies>\" to Advance.");
            d_displayToUser.instructionMessage("Use \"Blockade <targetCountryID>\" to use blockade card.");
            d_displayToUser.instructionMessage("Use \"Bomb <sourceCountryID> <targetCountryID>\" to use bomb card.");
            d_displayToUser.instructionMessage("Use \"Reinforcement\" to use reinforcement card.");
            d_displayToUser.instructionMessage("Use \"Diplomacy <targetPlayerName>\" to use diplomacy card.");

            d_displayToUser.instructionMessage("Use \"Savegame <filename>\" to use save game.");
            d_displayToUser.instructionMessage("Use \"Commit\" to complete orders.\n");

            int[] cards = p_gameSession.getPlayers().get(i).getD_cards_owned();
            d_displayToUser.instructionMessage("You have following cards:");
            d_displayToUser.instructionMessage("1. Bomb = " + cards[0]);
            d_displayToUser.instructionMessage("2. Reinforcement = " + cards[1]);
            d_displayToUser.instructionMessage("3. Blockade = " + cards[2]);
            d_displayToUser.instructionMessage("4. Airlift = " + cards[3]);
            d_displayToUser.instructionMessage("5. Diplomacy = " + cards[4]);

            p_gameSession.getPlayers().get(i).issue_order();
        }
    }

    /**
     * This method handles the order execution phase.
     *
     * @param p_gameSession The game session.
     */
    public void executeOrder(GameSession p_gameSession) {
        int playersOrderCompletionCount = 0;
        while (playersOrderCompletionCount < p_gameSession.getPlayers().size()) {
            for (Player player : p_gameSession.getPlayers()) {
                if (player.getOrders().isEmpty()) {
                    playersOrderCompletionCount++;
                }
                if (player.getOrders().isEmpty()) continue;
                player.next_order();
            }
        }

    }
}
