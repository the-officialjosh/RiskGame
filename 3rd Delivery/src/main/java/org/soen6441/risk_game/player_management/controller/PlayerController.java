package org.soen6441.risk_game.player_management.controller;

import org.soen6441.risk_game.game_engine.controller.user_input.UserInputScanner;
import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.controller.GameMapController;
import org.soen6441.risk_game.game_map.view.DisplayToUser;
import org.soen6441.risk_game.monitoring.LogEntryBuffer;
import org.soen6441.risk_game.orders.model.Order;
import org.soen6441.risk_game.player_management.model.*;
import org.soen6441.risk_game.player_management.strategy.PlayerStrategy;

import java.io.Serializable;
import java.util.InputMismatchException;
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

    /**
     * This method handles the player management step, allowing users to add or remove players
     * and assign countries to players.
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

        Scanner l_scanner = UserInputScanner.getInstance().getScanner();
        String l_command;
        List<Player> playerList = new ArrayList<>();

        // Regular expression to match individual commands
        Pattern pattern = Pattern.compile("(gameplayer\\s+-\\w+\\s+[^\\s]+|assigncountries)");

        boolean assignCountries = false;

        do {
            l_command = l_scanner.nextLine();

            // Catch user action for monitoring observer
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

                String l_action = l_commandArray[1];
                String l_playerName = l_commandArray[2];

                if (l_action.equals("-add")) {
                    d_displayToUser.instructionMessage("Select player strategy:\n");
                    d_displayToUser.instructionMessage("1. Human player");
                    d_displayToUser.instructionMessage("2. Aggressive player");
                    d_displayToUser.instructionMessage("3. Benevolent player");
                    d_displayToUser.instructionMessage("4. Cheater player");
                    d_displayToUser.instructionMessage("5. Random player");
                    try {
                        Player playerToAdd = new Player(l_playerName, 0, new ArrayList<>(), p_gameSession);
                        String choice = "";
                        do {
                            d_displayToUser.instructionMessage("Enter: ");
                            choice = l_scanner.next();
                            switch (choice) {
                                case "1": {
                                    playerToAdd.setD_playerStrategy(new HumanPlayer(playerToAdd, p_gameSession));
                                    break;
                                }
                                case "2": {
                                    playerToAdd.setD_playerStrategy(new AggressivePlayer(playerToAdd, p_gameSession));
                                    break;
                                }
                                case "3": {
                                    playerToAdd.setD_playerStrategy(new BenevolentPlayer(playerToAdd, p_gameSession));
                                    break;
                                }
                                case "4": {
                                    playerToAdd.setD_playerStrategy(new CheaterPlayer(playerToAdd, p_gameSession));
                                    break;
                                }
                                case "5": {
                                    playerToAdd.setD_playerStrategy(new RandomPlayer(playerToAdd, p_gameSession));
                                    break;
                                }
                                default:
                                    d_displayToUser.instructionMessage("Invalid option. try again");
                            }
                        } while (!choice.equals("1") && !choice.equals("2") && !choice.equals("3") && !choice.equals("4") && !choice.equals("5"));
                        boolean exists = playerList.stream().anyMatch(player -> player.getName().equalsIgnoreCase(playerToAdd.getName()));
                        if (exists) {
                            d_displayToUser.instructionMessage("⚠ Player " + l_playerName + " already exists.");
                        } else {
                            playerList.add(playerToAdd);
                            d_displayToUser.instructionMessage("✔ Player " + l_playerName + " added.");
                        }
                    }catch (InputMismatchException e){
                        d_displayToUser.instructionMessage("Invalid option.");
                    }




                } else if (l_action.equals("-remove")) {
                    boolean removed = playerList.removeIf(player -> player.getName().equalsIgnoreCase(l_playerName));
                    if (removed) {
                        d_displayToUser.instructionMessage("✔ Player " + l_playerName + " removed.");
                    } else {
                        d_displayToUser.instructionMessage("⚠ Player " + l_playerName + " does not exist.");
                    }
                } else {
                    d_displayToUser.instructionMessage("⚠ Invalid action. Use -add or -remove.");
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
