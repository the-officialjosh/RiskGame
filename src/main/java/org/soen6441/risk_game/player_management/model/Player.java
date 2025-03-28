package org.soen6441.risk_game.player_management.model;

import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.model.Country;
import org.soen6441.risk_game.game_map.view.DisplayToUser;
import org.soen6441.risk_game.monitoring.LogEntryBuffer;
import org.soen6441.risk_game.orders.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Represents a player in the game.
 *
 * @author Irfan Maknojia
 * @author Joshua Onyema
 * @author Safin Mahesania
 * @author Ahmed Fakhir
 * @version 1.0
 */
public class Player {
    private String d_name;
    private int d_numberOfReinforcementsArmies;
    private List<Order> d_orders;
    private List<Country> d_countries_owned;
    private int[] d_cards_owned;
    private final DisplayToUser d_displayToUser;

    /**
     * Constructs a player with a given name, reinforcement armies, and order list.
     *
     * @param p_name                         Player's name.
     * @param p_numberOfReinforcementsArmies Initial reinforcement armies.
     * @param p_orders                       List of orders.
     */
    public Player(String p_name, int p_numberOfReinforcementsArmies, List<Order> p_orders) {
        this.d_name = p_name;
        this.d_numberOfReinforcementsArmies = p_numberOfReinforcementsArmies;
        this.d_orders = p_orders;
        this.d_countries_owned = new ArrayList<>();
        this.d_cards_owned = new int[]{0, 0, 0, 0, 0}; //index 0 = Bomb, 1 = Reinforcement, 2 = Blockade, 3 = Airlift, 4 = Diplomacy
        this.d_displayToUser = new DisplayToUser();
    }

    /**
     * Gets the player's name.
     *
     * @return Player name.
     */
    public String getName() {
        return d_name;
    }

    /**
     * Sets the player's name.
     *
     * @param p_name New player name.
     */
    public void setName(String p_name) {
        this.d_name = p_name;
    }

    /**
     * Gets the number of reinforcement armies.
     *
     * @return Number of reinforcement armies.
     */
    public int getNumberOfReinforcementsArmies() {
        return d_numberOfReinforcementsArmies;
    }

    /**
     * Sets the number of reinforcement armies.
     *
     * @param p_numberOfReinforcementsArmies New reinforcement army count.
     */
    public void setNumberOfReinforcementsArmies(int p_numberOfReinforcementsArmies) {
        this.d_numberOfReinforcementsArmies = p_numberOfReinforcementsArmies;
    }

    /**
     * Calculates and sets the correct number of reinforcement armies.
     *
     * @param p_minimumNumberOfReinforcementsArmies Minimum reinforcement armies per round.
     */
    public void reinforcement(int p_minimumNumberOfReinforcementsArmies) {
        int l_totalNewArmies = Math.max(d_countries_owned.size() / 3, p_minimumNumberOfReinforcementsArmies);
        setNumberOfReinforcementsArmies(l_totalNewArmies);
    }

    /**
     * Gets the player's list of orders.
     *
     * @return List of orders.
     */
    public List<Order> getOrders() {
        return d_orders;
    }

    /**
     * Adds an order to the player's order list.
     *
     * @param p_order Order to add.
     */
    public void setOrders(Order p_order) {
        this.d_orders.add(p_order);
    }

    /**
     * Prompts the player to issue an order.
     */
    public void issue_order() {
        Scanner l_scanner = new Scanner(System.in);
        if (d_numberOfReinforcementsArmies <= 0) {
            d_displayToUser.instructionMessage(this.d_name + " has no reinforcement left.");
        }
        d_displayToUser.instructionMessage(this.getName() + " you have (" + this.getNumberOfReinforcementsArmies() + ") reinforcement armies.");
        while (true) {
            String l_command = l_scanner.nextLine().trim();

            // Catch user action for monitoring observer
            LogEntryBuffer.getInstance().setValue(l_command);
            if (l_command.equalsIgnoreCase("commit")) break;

            String[] l_command_parts = l_command.split(" ");
            try {
                if (this.hasReinforcementsArmies()) {
                    if (l_command_parts.length != 3 || !l_command_parts[0].equalsIgnoreCase("deploy")) {
                        d_displayToUser.instructionMessage("Invalid command. Use: deploy <countryID> <numberOfArmies>");
                        continue;
                    }
                    processDeployCommand(l_command_parts);
                    if (this.isReinforcementPhaseComplete()) {
                        d_displayToUser.instructionMessage("✔ All armies have been deployed.");
                        d_displayToUser.instructionMessage("\nYou can use Advance order command to move or attack countries");
                    } else {
                        d_displayToUser.instructionMessage(this.getName() + " you have (" + this.getNumberOfReinforcementsArmies() + ") reinforcement armies.");
                    }
                }
                if (this.isReinforcementPhaseComplete()) {
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
                    }
                }
            } catch (NumberFormatException e) {
                d_displayToUser.instructionMessage("Invalid number format. Please enter valid numeric values for country ID and number of armies.");
            }
        }
    }

    public void processDeployCommand(String[] l_command_parts) {
        int l_countryID = Integer.parseInt(l_command_parts[1]);
        int l_numOfArmies = Integer.parseInt(l_command_parts[2]);

        if (!validNumberOfReinforcementArmies(l_numOfArmies)) {
            d_displayToUser.instructionMessage("Cannot deploy more armies than available. You have " + d_numberOfReinforcementsArmies + " armies left.");
            return;
        }

        if (findCountryById(this.d_countries_owned, l_countryID) == null) {
            d_displayToUser.instructionMessage("You can only deploy armies to countries you own. Try again.");
            return;
        }

        Deploy deployOrder = new Deploy(this, l_numOfArmies, l_countryID);
        this.setOrders(deployOrder);
        d_numberOfReinforcementsArmies -= l_numOfArmies;
    }

    private void processDiplomacyCommand(String p_targetPlayerName) {
        GameSession l_gameSession = GameSession.getInstance();
        Player l_targetPlayer = l_gameSession.getPlayerByName(p_targetPlayerName);

        if (l_targetPlayer == null) {
            System.out.println("❌ Target player does not exist.");
            return;
        }

        // Validation: player must own the target country
        if (this.equals(l_targetPlayer)) {
            System.out.println("❌ Diplomacy failed: a player cannot establish diplomacy with themselves.");
            return;
        }

        if (l_gameSession.areInDiplomacy(this, l_targetPlayer)) {
            System.out.println("ℹ️ Diplomacy already exists between " + this.getName() + " and " + l_targetPlayer.getName() + ".");
            return;

        }

        if (!this.hasCard("diplomacy")) {
            System.out.println("❌ Invalid order: no diplomacy cards available.");
            return;
        }

        Diplomacy l_diplomacyOrder = new Diplomacy(this, l_targetPlayer);
        this.setOrders(l_diplomacyOrder);

        // Consume one diplomacy card
        this.useCard("diplomacy");
    }

    public void processReinforcementCommand() {
        if (!this.hasCard("reinforcement")) {
            System.out.println("❌ Invalid order: no reinforcement cards available.");
            return;
        }

        Reinforcement l_reinforcementOrder = new Reinforcement(this);
        this.setOrders(l_reinforcementOrder);

        // Consume one reinforcement card
        this.useCard("reinforcement");
    }

    public void processBlockadeCommand(String p_targetCountryID) {
        GameSession l_gameSession = GameSession.getInstance();
        Country l_targetCountry = l_gameSession.getMap().getCountriesById(Integer.parseInt(p_targetCountryID));

        if (l_targetCountry == null) {
            System.out.println("❌ Target country does not exist.");
            return;
        }

        // Validation: player must own the target country
        if (!this.equals(l_targetCountry.getD_ownedBy())) {
            System.out.println("❌ Invalid order: you do not own the target country.");
            return;
        }

        int currentArmies = l_targetCountry.getExistingArmies();
        if (currentArmies <= 0) {
            System.out.println("❌ No armies to blockade.");
            return;
        }

        if (!this.hasCard("blockade")) {
            System.out.println("❌ Invalid order: no blockade cards available.");
            return;
        }

        Blockade l_blockadeOrder = new Blockade(l_targetCountry);
        this.setOrders(l_blockadeOrder);

        // Consume one blockade card
        this.useCard("blockade");
    }

    public void processBombCommand(String p_sourceCountryID, String p_targetCountryID) {
        GameSession l_gameSession = GameSession.getInstance();

        Country l_sourceCountry = l_gameSession.getMap().getCountriesById(Integer.parseInt(p_sourceCountryID));
        Country l_targetCountry = l_gameSession.getMap().getCountriesById(Integer.parseInt(p_targetCountryID));

        if (l_sourceCountry == null || l_targetCountry == null) {
            System.out.println("❌ Either source or target country does not exist.");
            return;
        }

        Player l_targetOwner = l_targetCountry.getD_ownedBy();

        // Validation: player must own the source country
        if (!this.equals(l_sourceCountry.getD_ownedBy())) {
            System.out.println("❌ Invalid order: you do not own the source country.");
            return;
        }

        // Validation: can't bomb your own country
        if (this.equals(l_targetOwner)) {
            System.out.println("❌ Invalid order: you cannot bomb your own country.");
            return;
        }

        // Validation: cannot bomb if in diplomacy
        if (l_targetOwner != null && GameSession.getInstance().areInDiplomacy(this, l_targetOwner)) {
            System.out.println("❌ Invalid order: you cannot bomb a player you're in diplomacy with.");
            return;
        }

        // Validation: countries must be adjacent
        if (!l_sourceCountry.getAdjacentCountries().contains(l_targetCountry)) {
            System.out.println("❌ Invalid order: target country is not adjacent to source country.");
            return;
        }

        if (!this.hasCard("bomb")) {
            System.out.println("❌ Invalid order: no bomb cards available.");
            return;
        }

        Bomb bombOrder = new Bomb(l_sourceCountry, this, l_targetCountry);
        this.setOrders(bombOrder);

        // Consume one bomb card
        this.useCard("bomb");
    }

    public void processAdvanceCommand(String[] l_command_parts) {
        int l_fromCountryID = Integer.parseInt(l_command_parts[1]);
        int l_toCountryID = Integer.parseInt(l_command_parts[2]);
        int l_numOfArmies = Integer.parseInt(l_command_parts[3]);

        if (findCountryById(this.d_countries_owned, l_fromCountryID) == null) {
            d_displayToUser.instructionMessage("You can only advance armies from countries you own. Try again.");
            return;
        }
        if (!findCountryById(GameSession.getInstance().getMap().getCountries(), l_fromCountryID).getAdjacentCountries().contains(findCountryById(GameSession.getInstance().getMap().getCountries(), l_toCountryID))) {
            d_displayToUser.instructionMessage("You can only Advance armies to adjacent countries, Try again.");
            return;
        }

        Country fromCountry = findCountryById(GameSession.getInstance().getMap().getCountries(), l_fromCountryID);
        Country toCountry = findCountryById(GameSession.getInstance().getMap().getCountries(), l_toCountryID);

        Advance advanceOrder = new Advance(this, fromCountry, toCountry, l_numOfArmies);
        this.setOrders(advanceOrder);

    }

    public void processAirliftCommand(String[] l_command_parts) {
        int l_fromCountryID = Integer.parseInt(l_command_parts[1]);
        int l_toCountryID = Integer.parseInt(l_command_parts[2]);
        int l_numOfArmies = Integer.parseInt(l_command_parts[3]);

        if (findCountryById(this.d_countries_owned, l_fromCountryID) == null) {
            d_displayToUser.instructionMessage("You can only airlift armies from countries you own. Try again.");
            return;
        }

        if (findCountryById(this.d_countries_owned, l_toCountryID) == null) {
            d_displayToUser.instructionMessage("You can only airlift armies to countries you own. Try again.");
            return;
        }
        Country fromCountry = findCountryById(GameSession.getInstance().getMap().getCountries(), l_fromCountryID);
        Country toCountry = findCountryById(GameSession.getInstance().getMap().getCountries(), l_toCountryID);

        Airlift airliftOrder = new Airlift(fromCountry, toCountry, l_numOfArmies);
        this.setOrders(airliftOrder);
        this.useCard("airlift");

    }

    /**
     * Executes the next order in the player's order list.
     */
    public void next_order() {
        if (this.getOrders().isEmpty())
            return;

        this.getOrders().getFirst().execute();
        this.getOrders().removeFirst();

        /*for (Order order : this.getOrders()) {
            order.execute();
        }*/
    }

    /**
     * Gets the list of countries owned by the player.
     *
     * @return List of owned countries.
     */
    public List<Country> getD_countries_owned() {
        return d_countries_owned;
    }

    /**
     * Adds a country to the player's owned list.
     *
     * @param d_country_owned Country to add.
     */
    public void setD_countries_owned(Country d_country_owned) {
        this.d_countries_owned.add(d_country_owned);
    }

    /**
     * Checks if the player has reinforcement armies.
     *
     * @return True if reinforcement armies exist, false otherwise.
     */
    public boolean hasReinforcementsArmies() {
        return d_numberOfReinforcementsArmies > 0;
    }

    /**
     * Checks if the reinforcement phase is complete.
     *
     * @return True if no reinforcement armies are left.
     */
    public boolean isReinforcementPhaseComplete() {
        return d_numberOfReinforcementsArmies == 0;
    }

    /**
     * Finds a country by its ID.
     *
     * @param countries List of countries.
     * @param countryID Country ID to search for.
     * @return Country object if found, null otherwise.
     */
    public Country findCountryById(List<Country> countries, int countryID) {
        return countries.stream().filter(country -> country.getCountryId() == countryID).findFirst().orElse(null);
    }

    /**
     * Validates if the given number of reinforcement armies can be deployed.
     *
     * @param l_numOfArmies Number of armies.
     * @return True if valid, false otherwise.
     */
    public boolean validNumberOfReinforcementArmies(int l_numOfArmies) {
        return l_numOfArmies <= d_numberOfReinforcementsArmies;
    }

    public int[] getD_cards_owned() {
        return d_cards_owned;
    }

    public void setD_cards_owned(int card) {
        this.d_cards_owned[card] += 1;
    }

    /**
     * Assign card function to add a count of cards.
     */
    public void assignCard() {
        Random rn = new Random();
        int randomNumber = rn.nextInt(5);
        this.d_cards_owned[randomNumber - 1] += 1;
    }

    /**
     * Checks if the player has at least one card of the given type.
     *
     * @param cardType the card type
     * @return true if card is available, false otherwise.
     */
    public boolean hasCard(String cardType) {
        return switch (cardType.toLowerCase()) {
            case "bomb" -> d_cards_owned != null && d_cards_owned.length > 0 && d_cards_owned[0] > 0;
            case "reinforcement" -> d_cards_owned != null && d_cards_owned.length > 0 && d_cards_owned[1] > 0;
            case "blockade" -> d_cards_owned != null && d_cards_owned.length > 0 && d_cards_owned[2] > 0;
            case "airlift" -> d_cards_owned != null && d_cards_owned.length > 0 && d_cards_owned[3] > 0;
            case "diplomacy" -> d_cards_owned != null && d_cards_owned.length > 0 && d_cards_owned[4] > 0;
            default -> false;
        };
    }

    /**
     * Uses one card if available.
     *
     * @param cardType the card type
     */
    public void useCard(String cardType) {
        switch (cardType.toLowerCase()) {
            case "bomb": {
                if (hasCard("bomb")) {
                    d_cards_owned[0] -= 1;
                }
            }
            case "reinforcement": {
                if (hasCard("reinforcement")) {
                    d_cards_owned[1] -= 1;
                }
            }
            case "blockade": {
                if (hasCard("blockade")) {
                    d_cards_owned[2] -= 1;
                }
            }
            case "airlift": {
                if (hasCard("airlift")) {
                    d_cards_owned[3] -= 1;
                }
            }
            case "diplomacy": {
                if (hasCard("diplomacy")) {
                    d_cards_owned[4] -= 1;
                }
            }
        }
    }

    /**
     * Checks if the player has at least one diplomacy card.
     *
     * @return true if diplomacy card is available, false otherwise.
     */
    public boolean hasDiplomacyCard() {
        return d_cards_owned != null && d_cards_owned.length > 4 && d_cards_owned[4] > 0;
    }

    /**
     * Uses one diplomacy card if available.
     */
    public void useDiplomacyCard() {
        if (hasDiplomacyCard()) {
            d_cards_owned[4] -= 1;
        }
    }
}
