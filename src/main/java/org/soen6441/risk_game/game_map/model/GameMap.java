package org.soen6441.risk_game.game_map.model;

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
}
