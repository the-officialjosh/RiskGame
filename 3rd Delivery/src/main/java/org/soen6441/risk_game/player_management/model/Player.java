package org.soen6441.risk_game.player_management.model;

import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.model.Country;
import org.soen6441.risk_game.game_map.view.DisplayToUser;
import org.soen6441.risk_game.orders.model.*;
import org.soen6441.risk_game.player_management.strategy.PlayerStrategy;

import java.io.Serializable;
import java.util.*;

/**
 * Represents a player in the game.
 *
 * @author Irfan Maknojia
 * @author Joshua Onyema
 * @author Safin Mahesania
 * @author Ahmed Fakhir
 * @version 1.0
 */
public class Player implements Serializable {
    private String d_name;
    private int d_numberOfReinforcementsArmies;
    private List<Order> d_orders;
    private List<Country> d_countries_owned;
    private int[] d_cards_owned;
    private final DisplayToUser d_displayToUser;
    private final GameSession d_gameSession;
    private PlayerStrategy d_playerStrategy;
    /**
     * Constructs a player with a given name, reinforcement armies, and order list.
     *
     * @param p_name                         Player's name.
     * @param p_numberOfReinforcementsArmies Initial reinforcement armies.
     * @param p_orders                       List of orders.
     */
    public Player(String p_name, int p_numberOfReinforcementsArmies, List<Order> p_orders, GameSession p_gameSession) {
        this.d_name = p_name;
        this.d_numberOfReinforcementsArmies = p_numberOfReinforcementsArmies;
        this.d_orders = p_orders;
        this.d_countries_owned = new ArrayList<>();
        this.d_cards_owned = new int[]{0, 0, 0, 0, 0}; //index 0 = Bomb, 1 = Reinforcement, 2 = Blockade, 3 = Airlift, 4 = Diplomacy
        this.d_displayToUser = new DisplayToUser();
        this.d_gameSession = p_gameSession;
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


    public PlayerStrategy getD_playerStrategy() {
        return d_playerStrategy;
    }

    public void setD_playerStrategy(PlayerStrategy d_playerStrategy) {
        this.d_playerStrategy = d_playerStrategy;
    }

    /**
     * Prompts the player to issue an order.
     */
    public void issue_order() {
       d_playerStrategy.issueOrder();
    }

    /**
     * Executes the next order in the player's order list.
     */
    public void next_order() {
        if (this.getOrders().isEmpty()) return;

        if(!this.getOrders().getFirst().getClass().getName().equals("org.soen6441.risk_game.orders.model.Deploy")){
            while (!d_gameSession.getD_diplomacyPairs().isEmpty()) {
                Iterator<Diplomacy> iterator = d_gameSession.getD_diplomacyPairs().iterator();
                while (iterator.hasNext()) {
                    Diplomacy pair = iterator.next();
                    pair.incrementCount();

                    if (pair.getCount() >= d_gameSession.getPlayers().size()) {
                        iterator.remove();
                    }
                }
            }
        }

        this.getOrders().getFirst().execute();
        this.getOrders().removeFirst();
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
    public boolean isReinforcementArmiesDeployed() {
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
        int randomNumber = rn.nextInt(1,5);
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
}
