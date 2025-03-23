package org.soen6441.risk_game.game_map.model;

import org.soen6441.risk_game.player_management.model.Player;


import java.util.List;
import java.util.Map;


/**
 * This class represents the country entity.
 *
 * @author Ahmed Fakhir
 * @author Kawshik Kumar Ghosh
 * @author Safin Mahesania
 *
 * @version 1.0
 */
public class Country {
    private int d_countryId;
    private String d_name;
    private Player d_ownedBy;
    private List<Country> d_adjacentCountries;
    private Map<Player, Integer> d_existingArmies;
    private boolean d_isTerritoryNeutral;

    /**
     * Constructor for the class.
     * @param p_countryId The id of the country.
     * @param p_name The name of the country.
     * @param p_adjacentCountries The list of adjacent countries.
     * @param p_existingArmies The existing armies in the country.
     */
    public Country(int p_countryId, String p_name, List<Country> p_adjacentCountries, Map<Player, Integer> p_existingArmies) {
        this.d_countryId = p_countryId;
        this.d_name = p_name;
        this.d_adjacentCountries = p_adjacentCountries;
        this.d_existingArmies = p_existingArmies;
        this.d_isTerritoryNeutral = false;
    }

    /**
     * Getter for the field.
     * @return The country id.
     */
    public int getCountryId() {
        return d_countryId;
    }

    /**
     * Setter for the field.
     * @param p_countryId The country id.
     */
    public void setCountryId(int p_countryId) {
        this.d_countryId = p_countryId;
    }

    /**
     * Getter for the field.
     * @return The country name.
     */
    public String getName() {
        return d_name;
    }

    /**
     * Setter for the field.
     * @param p_name The country name.
     */
    public void setName(String p_name) {
        this.d_name = p_name;
    }

    /**
     * Getter for the field.
     */
    public List<Country> getAdjacentCountries() {
        return d_adjacentCountries;
    }

    /**
     * Setter for the field.
     */
    public void setAdjacentCountries(List<Country> p_adjacentCountries) {
        this.d_adjacentCountries = p_adjacentCountries;
    }

    /**
     * Getter for the field.
     */
    public Map<Player, Integer> getExistingArmies() {
        return d_existingArmies;
    }

    /**
     * Setter for the field.
     */
    public void setExistingArmies(Map<Player, Integer> p_existingArmies) {
        this.d_existingArmies = p_existingArmies;
    }

    /**
     * Gets d owned by.
     *
     * @return the d owned by
     */
    public Player getD_ownedBy() {
        return d_ownedBy;
    }

    /**
     * Sets d owned by.
     *
     * @param d_ownedBy the d owned by
     */
    public void setD_ownedBy(Player d_ownedBy) {
        this.d_ownedBy = d_ownedBy;
    }

    /**
     * Adds an adjacent country.
     * @param neighbor The neighbor country to add.
     */
    public void addNeighbor(Country neighbor) {
        d_adjacentCountries.add(neighbor);
    }

    /**
     * Removes an adjacent country.
     * @param neighborName The name of the neighbor country to remove.
     */
    public void removeNeighbor(String neighborName) {
        d_adjacentCountries.removeIf(neighbor -> neighbor.getName().equals(neighborName));
    }

    /**
     * Is d is territory neutral boolean.
     *
     * @return the boolean
     */
    public boolean isD_isTerritoryNeutral() {
        return d_isTerritoryNeutral;
    }

    /**
     * Sets d is territory neutral.
     *
     * @param d_isTerritoryNeutral the d is territory neutral
     */
    public void setD_isTerritoryNeutral(boolean d_isTerritoryNeutral) {
        this.d_isTerritoryNeutral = d_isTerritoryNeutral;
    }
}
