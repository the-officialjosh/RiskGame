package org.soen6441.risk_game.game_map.model;

import org.soen6441.risk_game.player_management.model.Player;

import java.util.List;
import java.util.Map;

/**
 * This class represents the country entity.
 */
public class Country {
    private int d_countryId;
    private String d_name;
    private List<Country> d_adjacentCountries;
    private Map<Player, Integer> d_existingArmies;

    /**
     * Constructor for the class.
     * @param p_countryId
     * @param p_name
     * @param p_adjacentCountries
     * @param p_existingArmies
     */
    public Country(int p_countryId, String p_name, List<Country> p_adjacentCountries, Map<Player, Integer> p_existingArmies) {
        this.d_countryId = p_countryId;
        this.d_name = p_name;
        this.d_adjacentCountries = p_adjacentCountries;
        this.d_existingArmies = p_existingArmies;
    }

    /**
     * Getter for the field.
     */
    public int getCountryId() {
        return d_countryId;
    }

    /**
     * Setter for the field.
     */
    public void setCountryId(int p_countryId) {
        this.d_countryId = p_countryId;
    }

    /**
     * Getter for the field.
     */
    public String getName() {
        return d_name;
    }

    /**
     * Setter for the field.
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
}
