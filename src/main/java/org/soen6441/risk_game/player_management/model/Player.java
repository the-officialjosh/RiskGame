package org.soen6441.risk_game.player_management.model;

import org.soen6441.risk_game.game_map.model.Country;
import org.soen6441.risk_game.game_map.view.DisplayToUser;
import org.soen6441.risk_game.orders.model.Deploy;
import org.soen6441.risk_game.orders.model.Order;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * This class represents the player entity.
 */
public class Player {
    private String d_name;
    private int d_numberOfReinforcementsArmies;
    private List<Order> d_orders;
    private List<Country> d_countries_owned;
    private final DisplayToUser d_displayToUser;

    /**
     * Constructor for class.
     *
     * @param p_name                         the p name
     * @param p_numberOfReinforcementsArmies the p number of reinforcements armies
     * @param p_orders                       the p orders
     */
    public Player(String p_name, int p_numberOfReinforcementsArmies, List<Order> p_orders) {
        this.d_name = p_name;
        this.d_numberOfReinforcementsArmies = p_numberOfReinforcementsArmies;
        this.d_orders = p_orders;
        this.d_countries_owned = new ArrayList<>();
        this.d_displayToUser = new DisplayToUser();
    }

    /**
     * Getter for field.
     *
     * @return the name
     */
    public String getName() {
        return d_name;
    }

    /**
     * Setter for field.
     *
     * @param p_name the p name
     */
    public void setName(String p_name) {
        this.d_name = p_name;
    }

    /**
     * Getter for field.
     *
     * @return the number of reinforcements armies
     */
    public int getNumberOfReinforcementsArmies() {
        return d_numberOfReinforcementsArmies;
    }

    /**
     * Setter for field.
     *
     * @param p_numberOfReinforcementsArmies the p number of reinforcements armies
     */
    public void setNumberOfReinforcementsArmies(int p_numberOfReinforcementsArmies) {
        this.d_numberOfReinforcementsArmies = p_numberOfReinforcementsArmies;
    }

    /**
     * Getter for field.
     *
     * @return the orders
     */
    public List<Order> getOrders() {
        return d_orders;
    }

    /**
     * Setter for field.
     *
     * @param p_order the p orders
     */
    public void setOrders(Order p_order) {
        this.d_orders.add(p_order);
    }

    /**
     * This method handles the step of the player specifying
     * the orders he wants to execute.
     */
    public void issue_order() {
        Scanner l_scanner = new Scanner(System.in);
        if(d_numberOfReinforcementsArmies <= 0 ){
            d_displayToUser.instructionMessage(this.d_name+ "has no reinforcement left.");
        }
        while (true){
            String l_command = l_scanner.nextLine().trim();
            String[] l_command_parts = l_command.split(" ");
            if(l_command_parts.length != 3 || !l_command_parts[0].equalsIgnoreCase("deploy")){
                d_displayToUser.instructionMessage("Invalid Command, try this command: \"deploy <countryID> <numberOfArmies>\"");
                continue;
            }
            int l_countryID;
            int l_numOfArmies;
            try {
                l_countryID = Integer.parseInt(l_command_parts[1]);
                l_numOfArmies = Integer.parseInt(l_command_parts[2]);
            }catch (NumberFormatException e){
                d_displayToUser.instructionMessage("Invalid number of armies or countryID. please enter valid values");
                continue;
            }

            if(l_numOfArmies > d_numberOfReinforcementsArmies){
                d_displayToUser.instructionMessage("You can not deploy more the your reinforcement armies. You have "+d_numberOfReinforcementsArmies+" of armies. try again");
                continue;
            }
            d_numberOfReinforcementsArmies -= l_numOfArmies;
            Deploy deployOrder = new Deploy(this,l_numOfArmies,l_countryID);
            this.setOrders(deployOrder);
            break;
        }

    }

    /**
     * This method return the next order to execute.
     *
     * @return the order
     */
    public Order next_order() {
        return null;
    }

    /**
     * Gets d owned countries.
     *
     * @return the d owned countries
     */
    public List<Country> getD_countries_owned() {
        return d_countries_owned;
    }

    /**
     * Sets d owned countries.
     *
     * @param d_country_owned the d owned country
     */
    public void setD_countries_owned(Country d_country_owned) {
        this.d_countries_owned.add(d_country_owned);
    }

    /**
     * Has reinforcements armies boolean.
     *
     * @return the boolean
     */
    public boolean hasReinforcementsArmies(){
        return d_numberOfReinforcementsArmies > 0;
    }

    public boolean isReinforcementPhaseComplete(){
        return d_numberOfReinforcementsArmies == 0;
    }
}
