package org.soen6441.risk_game.game_map.model;

import java.util.List;

/**
 * Entity representing the continents of the game.
 */
public class Continent {
    private String d_name;
    private List<Country> d_countries;

    /**
     * Contructor for the class.
     * @param p_name The name of the continent.
     * @param p_countries The countries that will be inside the continent.
     */
    public Continent(String p_name, List<Country> p_countries) {
        this.d_name = p_name;
        this.d_countries = p_countries;
    }

    /**
     * Returns the name of the continent.
     * @return the name of continent
     */
    public String getName() {
        return d_name;
    }

    /**
     * Sets the name of the continent.
     * @param p_name the name of the continent.
     */
    public void setName(String p_name) {
        this.d_name = p_name;
    }

    /**
     * Returns the list of countries inside the continent.
     * @return The list of countries inside the continent.
     */
    public List<Country> getCountries() {
        return d_countries;
    }

    /**
     * Sets the lists of countries for the continent.
     * @param p_territories The list of countries for the continent.
     */
    public void setCountries(List<Country> p_territories) {
        this.d_countries = p_territories;
    }
}
