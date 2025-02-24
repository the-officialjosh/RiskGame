package org.soen6441.risk_game.player_management.model;

import org.soen6441.risk_game.game_map.model.Country;
import org.soen6441.risk_game.orders.model.Order;

import java.util.List;

/**
 * This class represents the player entity.
 */
public class Player {
    private String d_name;
    private int d_numberOfReinforcementsArmies;
    private List<Order> d_orders;
    private List<Country> d_countries_owned;

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
     * @param p_orders the p orders
     */
    public void setOrders(List<Order> p_orders) {
        this.d_orders = p_orders;
    }

    /**
     * This method handles the step of the player specifying
     * the orders he wants to execute.
     */
    public void issue_order() {

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
     * @param d_countries_owned the d owned countries
     */
    public void setD_countries_owned(List<Country> d_countries_owned) {
        this.d_countries_owned = d_countries_owned;
    }
}
