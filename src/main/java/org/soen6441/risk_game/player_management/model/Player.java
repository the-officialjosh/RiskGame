package org.soen6441.risk_game.player_management.model;

import org.soen6441.risk_game.orders.model.Order;

import java.util.List;

/**
 * This class represents the player entity.
 */
public class Player {
    private String d_name;
    private int d_numberOfReinforcementsArmies;
    private List<Order> d_orders;

    /**
     * Constructor for class.
     * @param p_name
     * @param p_numberOfReinforcementsArmies
     * @param p_orders
     */
    public Player(String p_name, int p_numberOfReinforcementsArmies, List<Order> p_orders) {
        this.d_name = p_name;
        this.d_numberOfReinforcementsArmies = p_numberOfReinforcementsArmies;
        this.d_orders = p_orders;
    }

    /**
     * Getter for field.
     */
    public String getName() {
        return d_name;
    }

    /**
     * Setter for field.
     */
    public void setName(String p_name) {
        this.d_name = p_name;
    }

    /**
     * Getter for field.
     */
    public int getNumberOfReinforcementsArmies() {
        return d_numberOfReinforcementsArmies;
    }

    /**
     * Setter for field.
     */
    public void setNumberOfReinforcementsArmies(int p_numberOfReinforcementsArmies) {
        this.d_numberOfReinforcementsArmies = p_numberOfReinforcementsArmies;
    }

    /**
     * Getter for field.
     */
    public List<Order> getOrders() {
        return d_orders;
    }

    /**
     * Setter for field.
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
     */
    public Order next_order() {
        return null;
    }
}
