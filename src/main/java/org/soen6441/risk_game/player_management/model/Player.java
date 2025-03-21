package org.soen6441.risk_game.player_management.model;

import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.model.Country;
import org.soen6441.risk_game.game_map.view.DisplayToUser;
import org.soen6441.risk_game.monitoring.LogEntryBuffer;
import org.soen6441.risk_game.orders.model.Advance;
import org.soen6441.risk_game.orders.model.Deploy;
import org.soen6441.risk_game.orders.model.Order;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Represents a player in the game.
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
            if(l_command.equalsIgnoreCase("commit")) break;

            String[] l_command_parts = l_command.split(" ");
            try {
                if(this.hasReinforcementsArmies()){
                    if (l_command_parts.length != 3 || !l_command_parts[0].equalsIgnoreCase("deploy")) {
                        d_displayToUser.instructionMessage("Invalid command. Use: deploy <countryID> <numberOfArmies>");
                        continue;
                    }
                    int l_countryID = Integer.parseInt(l_command_parts[1]);
                    int l_numOfArmies = Integer.parseInt(l_command_parts[2]);

                    if (!validNumberOfReinforcementArmies(l_numOfArmies)) {
                        d_displayToUser.instructionMessage("Cannot deploy more armies than available. You have " + d_numberOfReinforcementsArmies + " armies left.");
                        continue;
                    }

                    if (findCountryById(this.d_countries_owned, l_countryID) == null) {
                        d_displayToUser.instructionMessage("You can only deploy armies to countries you own. Try again.");
                        continue;
                    }

                    Deploy deployOrder = new Deploy(this, l_numOfArmies, l_countryID);
                    this.setOrders(deployOrder);
                    d_numberOfReinforcementsArmies -= l_numOfArmies;
                    if(this.isReinforcementPhaseComplete()){
                        d_displayToUser.instructionMessage("✔ All armies have been deployed.");
                        d_displayToUser.instructionMessage("\nYou can use Advance order command to move or attack countries");
                    }else{
                        d_displayToUser.instructionMessage(this.getName() + " you have (" + this.getNumberOfReinforcementsArmies() + ") reinforcement armies.");
                    }
                }
                if(this.isReinforcementPhaseComplete()){
                    if(l_command_parts[0].equalsIgnoreCase("Advance")){
                        if(l_command_parts.length != 4){
                            d_displayToUser.instructionMessage("Invalid command. Use: Advance <fromCountryID> <toCountryID> <numberOfArmies>");
                            continue;
                        }
                        processAdvanceCommand(l_command_parts);
                    }
                }
            } catch (NumberFormatException e) {
                d_displayToUser.instructionMessage("Invalid number format. Please enter valid numeric values for country ID and number of armies.");
            }
        }
    }

    private void processAdvanceCommand(String[] l_command_parts) {
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

        Advance advanceOrder = new Advance(this, l_fromCountryID, l_toCountryID, l_numOfArmies);
        this.setOrders(advanceOrder);

    }

    /**
     * Executes the next order in the player's order list.
     */
    public void next_order() {
        for (Order order : this.getOrders()) {
            order.execute();
        }
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
}
