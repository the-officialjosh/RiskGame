package org.soen6441.risk_game.game_map.model;

import java.util.List;

/**
 * Entity representing the continents of the game.
 * It contains the countries that are part of the continent.
 *
 * @author Ahmed Fakhir
 * @author Kawshik Kumar Ghosh
 * @version 1.0
 */
public class Continent {
    private int d_continentId;
    private String d_name;
    private List<Country> d_countries;
    private int d_controlValue;

    public static int continentIdCounter = 1;
    /**
     * Contructor for the class.
     * @param p_name The name of the continent.
     * @param p_countries The countries that will be inside the continent.
     * @param p_controlValue The value of the continent.
     */
    public Continent(String p_name, List<Country> p_countries, int p_controlValue) {
        this.d_continentId = continentIdCounter;
        continentIdCounter++;
        this.d_name = p_name;
        this.d_countries = p_countries;
        this.d_controlValue = p_controlValue;
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
    public int getControlValue() {
        return d_controlValue;
    }

    /**
     * Sets the value of the continent.
     * @param p_controlValue The value of the continent.
     */
    public void setControlValue(int p_controlValue) {
        this.d_controlValue = p_controlValue;
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

    /**
     * Gets the continent id.
     * @return the continent id.
     */
    public int getD_continentId() {
        return d_continentId;
    }

    /**
     * Sets the continent id.
     * @param p_continentId the continent id to set.
     */
    public void setD_continentId(int p_continentId) {
        this.d_continentId = p_continentId;
    }
}
