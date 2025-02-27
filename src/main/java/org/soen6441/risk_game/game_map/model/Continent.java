package org.soen6441.risk_game.game_map.model;

import java.util.List;

/**
 * Entity representing the continents of the game.
 */
public class Continent {
    private String d_name;
    private List<Country> d_countries;
    private int d_value;
    /**
     * Contructor for the class.
     * @param p_name The name of the continent.
     * @param p_countries The countries that will be inside the continent.
     * @param p_value The value of the continent.
     */
    public Continent(String p_name, List<Country> p_countries, int p_value) {
        this.d_name = p_name;
        this.d_countries = p_countries;
        this.d_value = p_value;
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
    /**
     * Returns the value of the continent.
     * @return The value of the continent.
     */
    public int getValue() {
        return d_value;
    }

    /**
     * Sets the value of the continent.
     * @param p_value The value of the continent.
     */
    public void setValue(int p_value) {
        this.d_value = p_value;
    }

    /**
     * Adds a country to the continent.
     * @param country The country to add.
     */
    public void addCountry(Country country) {
        d_countries.add(country);
    }

    /**
     * Removes a country from the continent.
     * @param countryName The name of the country to remove.
     */
    public void removeCountry(String countryName) {
        d_countries.removeIf(country -> country.getName().equals(countryName));
    }
}
