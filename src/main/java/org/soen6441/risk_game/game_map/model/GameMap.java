package org.soen6441.risk_game.game_map.model;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the global map for the game.
 */
public class GameMap {
    private List<Continent> d_continents;

    /**
     * Constructor for class.
     * @param p_continents
     */
    public GameMap(List<Continent> p_continents) {
        this.d_continents = p_continents;
    }

    /**
     * Getter for the field.
     */
    public List<Continent> getContinents() {
        return d_continents;
    }

    /**
     * Setter for the field.
     */
    public void setContinents(List<Continent> p_continents) {
        this.d_continents = p_continents;
    }
    /**
     * Adds a continent to the map.
     * @param continent The continent to add.
     */
    public void addContinent(Continent continent) {
        d_continents.add(continent);
    }

    /**
     * Removes a continent from the map.
     * @param continentName The name of the continent to remove.
     */
    public void removeContinent(String continentName) {
        d_continents.removeIf(continent -> continent.getName().equals(continentName));
    }

    /**
     * Returns all countries in the map.
     * @return List of all countries.
     */
    public List<Country> getCountries() {
        List<Country> countries = new ArrayList<>();
        for (Continent continent : d_continents) {
            countries.addAll(continent.getCountries());
        }
        return countries;
    }
}
