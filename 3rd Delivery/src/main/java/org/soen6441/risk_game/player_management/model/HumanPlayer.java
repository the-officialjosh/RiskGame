package org.soen6441.risk_game.player_management.model;

import org.soen6441.risk_game.game_engine.controller.user_input.UserInputScanner;
import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.model.Country;
import org.soen6441.risk_game.game_map.view.DisplayToUser;
import org.soen6441.risk_game.monitoring.LogEntryBuffer;
import org.soen6441.risk_game.orders.model.*;
import org.soen6441.risk_game.player_management.strategy.PlayerStrategy;

import java.io.File;
import java.io.Serializable;
import java.util.Scanner;

public class HumanPlayer implements PlayerStrategy, Serializable {

    private final Player d_player;
    private DisplayToUser d_displayToUser;
    private GameSession d_gameSession;

    public HumanPlayer(Player p_player, GameSession p_gameSession) {
        this.d_player = p_player;
        this.d_displayToUser = new DisplayToUser();
        this.d_gameSession = p_gameSession;
    }

    @Override
    public void issueOrder() {
        Scanner l_scanner = UserInputScanner.getInstance().getScanner();
        if (d_player.getNumberOfReinforcementsArmies() <= 0) {
            d_displayToUser.instructionMessage(d_player.getName() + " has no reinforcement left.");
        }
        d_displayToUser.instructionMessage(d_player.getName() + " you have (" + d_player.getNumberOfReinforcementsArmies() + ") reinforcement armies.");
        while (true) {
            String l_command = l_scanner.nextLine().trim();

            // Catch user action for monitoring observer
            LogEntryBuffer.getInstance().setValue(l_command);
            if (l_command.equalsIgnoreCase("commit")) break;

            String[] l_command_parts = l_command.split(" ");
            try {
                if (l_command_parts[0].equalsIgnoreCase("savegame")) {
                    if (l_command_parts.length != 2) {
                        d_displayToUser.instructionMessage("Invalid command. Use: savegame <filename>");
                        continue;
                    }
                    processSaveGameCommand(l_command_parts[1]);
                } else if (d_player.hasReinforcementsArmies()) {
                    if (l_command_parts.length != 3 || !l_command_parts[0].equalsIgnoreCase("deploy")) {
                        d_displayToUser.instructionMessage("Invalid command. Use: deploy <countryID> <numberOfArmies>");
                        continue;
                    }
                    processDeployCommand(l_command_parts);
                    if (d_player.isReinforcementArmiesDeployed()) {
                        d_displayToUser.instructionMessage("✔ All armies have been deployed.");
                        d_displayToUser.instructionMessage("\nYou can use Advance order command to move or attack countries");
                    } else {
                        d_displayToUser.instructionMessage(d_player.getName() + " you have (" + d_player.getNumberOfReinforcementsArmies() + ") reinforcement armies.");
                    }
                } else if (d_player.isReinforcementArmiesDeployed()) {
                    if (l_command_parts[0].equalsIgnoreCase("Advance")) {
                        if (l_command_parts.length != 4) {
                            d_displayToUser.instructionMessage("Invalid command. Use: Advance <fromCountryID> <toCountryID> <numberOfArmies>");
                            continue;
                        }
                        processAdvanceCommand(l_command_parts);
                    } else if (l_command_parts[0].equalsIgnoreCase("Airlift")) {
                        if (l_command_parts.length != 4) {
                            d_displayToUser.instructionMessage("Invalid command. Use: Airlift <fromCountryID> <toCountryID> <numberOfArmies>");
                            continue;
                        }
                        processAirliftCommand(l_command_parts);
                    } else if (l_command_parts[0].equalsIgnoreCase("Bomb")) {
                        if (l_command_parts.length != 3) {
                            d_displayToUser.instructionMessage("Invalid command. Use: Bomb <sourceCountryID> <targetCountryID>");
                            continue;
                        }
                        processBombCommand(l_command_parts[1], l_command_parts[2]);
                    } else if (l_command_parts[0].equalsIgnoreCase("Reinforcement")) {
                        if (l_command_parts.length != 1) {
                            d_displayToUser.instructionMessage("Invalid command. Use: Reinforcement");
                            continue;
                        }
                        processReinforcementCommand();
                    } else if (l_command_parts[0].equalsIgnoreCase("Blockade")) {
                        if (l_command_parts.length != 2) {
                            d_displayToUser.instructionMessage("Invalid command. Use: Blockade <targetCountryID>");
                            continue;
                        }
                        processBlockadeCommand(l_command_parts[1]);
                    } else if (l_command_parts[0].equalsIgnoreCase("Diplomacy")) {
                        if (l_command_parts.length != 2) {
                            d_displayToUser.instructionMessage("Invalid command. Use: Diplomacy <targetPlayerName>");
                            continue;
                        }
                        processDiplomacyCommand(l_command_parts[1]);
                    } else {
                        d_displayToUser.instructionMessage("Invalid command. Use one of the command given above");
                    }
                }
            } catch (NumberFormatException e) {
                d_displayToUser.instructionMessage("Invalid number format. Please enter valid numeric values for country ID and number of armies.");
            }
        }
    }

    private void processSaveGameCommand(String filename) {
        String folderName = "out/save-game/" + filename + ".dat";
        File folder = new File(folderName);

        if (folder.exists()) {
            System.out.println("❌ Filename already exist.");
            System.out.println("❌ Do you want to replace existing file?\nY for Yes\tN for No");
            Scanner sc = new Scanner(System.in);
            char opt;
            while (true) {
                opt = sc.next().charAt(0);
                if (opt == 'N' || opt == 'n' || opt == 'Y' || opt == 'y') break;
                System.out.println("Invalid option. Please select the correct option.");
            }
            if (opt == 'n' || opt == 'N') {
                System.out.println("File not saved, try again.");
            } else {
                d_gameSession.saveGame(filename);
                System.out.println("File saved.");
            }
        } else {
            d_gameSession.saveGame(filename);
            System.out.println("File saved.");
        }
    }

    /**
     * Process deploy command.
     *
     * @param l_command_parts the l command parts
     */
    private void processDeployCommand(String[] l_command_parts) {
        int l_countryID = Integer.parseInt(l_command_parts[1]);
        int l_numOfArmies = Integer.parseInt(l_command_parts[2]);

        if (!d_player.validNumberOfReinforcementArmies(l_numOfArmies)) {
            d_displayToUser.instructionMessage("Cannot deploy more armies than available. You have " + d_player.getNumberOfReinforcementsArmies() + " armies left.");
            return;
        }

        if (d_player.findCountryById(d_player.getD_countries_owned(), l_countryID) == null) {
            d_displayToUser.instructionMessage("You can only deploy armies to countries you own. Try again.");
            return;
        }

        Deploy deployOrder = new Deploy(d_player, l_numOfArmies, l_countryID);
        deployOrder.setD_gameSession(d_gameSession);
        d_player.setOrders(deployOrder);
        int remainingReinforcementArmies = d_player.getNumberOfReinforcementsArmies() - l_numOfArmies;
        d_player.setNumberOfReinforcementsArmies(remainingReinforcementArmies);
    }

    /**
     * Process diplomacy command.
     *
     * @param p_targetPlayerName the p target player name
     */
    public void processDiplomacyCommand(String p_targetPlayerName) {
        Player l_targetPlayer = d_gameSession.getPlayerByName(p_targetPlayerName);

        if (l_targetPlayer == null) {
            System.out.println("❌ Target player does not exist.");
            return;
        }

        // Validation: player must own the target country
        if (d_player.equals(l_targetPlayer)) {
            System.out.println("❌ Diplomacy failed: a player cannot establish diplomacy with themselves.");
            return;
        }

        if (d_gameSession.areInDiplomacy(d_player, l_targetPlayer)) {
            System.out.println("ℹ️ Diplomacy already exists between " + d_player.getName() + " and " + l_targetPlayer.getName() + ".");
            return;

        }

        if (!d_player.hasCard("diplomacy")) {
            System.out.println("❌ Invalid order: no diplomacy cards available.");
            return;
        }

        Diplomacy l_diplomacyOrder = new Diplomacy(d_player, l_targetPlayer);
        l_diplomacyOrder.setD_gameSession(d_gameSession);
        d_player.setOrders(l_diplomacyOrder);

        // Consume one diplomacy card
        d_player.useCard("diplomacy");
    }

    /**
     * Process reinforcement command.
     */
    public void processReinforcementCommand() {
        if (!d_player.hasCard("reinforcement")) {
            System.out.println("❌ Invalid order: no reinforcement cards available.");
            return;
        }

        Reinforcement l_reinforcementOrder = new Reinforcement(d_player);
        l_reinforcementOrder.setD_gameSession(d_gameSession);
        d_player.setOrders(l_reinforcementOrder);

        // Consume one reinforcement card
        d_player.useCard("reinforcement");
    }

    /**
     * Process blockade command.
     *
     * @param p_targetCountryID the p target country id
     */
    public void processBlockadeCommand(String p_targetCountryID) {
        Country l_targetCountry = d_gameSession.getMap().getCountriesById(Integer.parseInt(p_targetCountryID));

        if (l_targetCountry == null) {
            System.out.println("❌ Target country does not exist.");
            return;
        }

        // Validation: player must own the target country
        if (!d_player.equals(l_targetCountry.getD_ownedBy())) {
            System.out.println("❌ Invalid order: you do not own the target country.");
            return;
        }

        int currentArmies = l_targetCountry.getExistingArmies();
        if (currentArmies <= 0) {
            System.out.println("❌ No armies to blockade.");
            return;
        }

        if (!d_player.hasCard("blockade")) {
            System.out.println("❌ Invalid order: no blockade cards available.");
            return;
        }

        Blockade l_blockadeOrder = new Blockade(l_targetCountry);
        l_blockadeOrder.setD_gameSession(d_gameSession);
        d_player.setOrders(l_blockadeOrder);

        // Consume one blockade card
        d_player.useCard("blockade");
    }

    /**
     * Process bomb command.
     *
     * @param p_sourceCountryID the p source country id
     * @param p_targetCountryID the p target country id
     */
    public void processBombCommand(String p_sourceCountryID, String p_targetCountryID) {

        Country l_sourceCountry = d_gameSession.getMap().getCountriesById(Integer.parseInt(p_sourceCountryID));
        Country l_targetCountry = d_gameSession.getMap().getCountriesById(Integer.parseInt(p_targetCountryID));

        if (l_sourceCountry == null || l_targetCountry == null) {
            System.out.println("❌ Either source or target country does not exist.");
            return;
        }

        Player l_targetOwner = l_targetCountry.getD_ownedBy();

        // Validation: player must own the source country
        if (!d_player.equals(l_sourceCountry.getD_ownedBy())) {
            System.out.println("❌ Invalid order: you do not own the source country.");
            return;
        }

        // Validation: can't bomb your own country
        if (d_player.equals(l_targetOwner)) {
            System.out.println("❌ Invalid order: you cannot bomb your own country.");
            return;
        }

        // Validation: cannot bomb if in diplomacy
        if (l_targetOwner != null && d_gameSession.areInDiplomacy(d_player, l_targetOwner)) {
            System.out.println("❌ Invalid order: you cannot bomb a player you're in diplomacy with.");
            return;
        }

        // Validation: countries must be adjacent
        if (!l_sourceCountry.getAdjacentCountries().contains(l_targetCountry)) {
            System.out.println("❌ Invalid order: target country is not adjacent to source country.");
            return;
        }

        if (!d_player.hasCard("bomb")) {
            System.out.println("❌ Invalid order: no bomb cards available.");
            return;
        }

        Bomb bombOrder = new Bomb(l_sourceCountry, d_player, l_targetCountry);
        bombOrder.setD_gameSession(d_gameSession);
        d_player.setOrders(bombOrder);

        // Consume one bomb card
        d_player.useCard("bomb");
    }

    /**
     * Process advance command.
     *
     * @param l_command_parts the l command parts
     */
    public void processAdvanceCommand(String[] l_command_parts) {
        int l_fromCountryID = Integer.parseInt(l_command_parts[1]);
        int l_toCountryID = Integer.parseInt(l_command_parts[2]);
        int l_numOfArmies = Integer.parseInt(l_command_parts[3]);

        if (d_player.findCountryById(d_player.getD_countries_owned(), l_fromCountryID) == null) {
            d_displayToUser.instructionMessage("You can only advance armies from countries you own. Try again.");
            return;
        }
        if (!d_player.findCountryById(d_gameSession.getMap().getCountries(), l_fromCountryID).getAdjacentCountries().contains(d_player.findCountryById(d_gameSession.getMap().getCountries(), l_toCountryID))) {
            d_displayToUser.instructionMessage("You can only Advance armies to adjacent countries, Try again.");
            return;
        }

        Country fromCountry = d_player.findCountryById(d_gameSession.getMap().getCountries(), l_fromCountryID);
        Country toCountry = d_player.findCountryById(d_gameSession.getMap().getCountries(), l_toCountryID);

        Advance advanceOrder = new Advance(d_player, fromCountry, toCountry, l_numOfArmies);
        advanceOrder.setD_gameSession(d_gameSession);
        d_player.setOrders(advanceOrder);

    }

    /**
     * Process airlift command.
     *
     * @param l_command_parts the l command parts
     */
    public void processAirliftCommand(String[] l_command_parts) {
        int l_fromCountryID = Integer.parseInt(l_command_parts[1]);
        int l_toCountryID = Integer.parseInt(l_command_parts[2]);
        int l_numOfArmies = Integer.parseInt(l_command_parts[3]);

        if (!d_player.hasCard("Airlift")) {
            return;
        }

        if (d_player.findCountryById(d_player.getD_countries_owned(), l_fromCountryID) == null) {
            d_displayToUser.instructionMessage("You can only airlift armies from countries you own. Try again.");
            return;
        }

        if (d_player.findCountryById(d_player.getD_countries_owned(), l_toCountryID) == null) {
            d_displayToUser.instructionMessage("You can only airlift armies to countries you own. Try again.");
            return;
        }

        Country fromCountry = d_player.findCountryById(d_gameSession.getMap().getCountries(), l_fromCountryID);
        Country toCountry = d_player.findCountryById(d_gameSession.getMap().getCountries(), l_toCountryID);

        Airlift airliftOrder = new Airlift(fromCountry, toCountry, l_numOfArmies);
        airliftOrder.setD_gameSession(d_gameSession);
        d_player.setOrders(airliftOrder);
        d_player.useCard("airlift");
    }
}
