package org.soen6441.risk_game.orders.model;

import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.model.Country;
import org.soen6441.risk_game.monitoring.LogEntryBuffer;
import org.soen6441.risk_game.player_management.model.Player;

import java.io.Serializable;

/**
 * This class represents the Deploy order.
 *
 * @author Ahmed Fakhir
 * @author Irfan Maknojia
 * @author Safin Mahesania
 * @author Joshua Onyema
 * @version 1.0
 * @see Order
 */
public class Deploy implements Order, Serializable {

    private Player d_issuer;
    private int d_numberOfDeployedArmies;
    private int d_countryId;
    private GameSession d_gameSession;

    /**
     * Constructor for class.
     *
     * @param p_issuer                 The player who issued the order.
     * @param p_numberOfDeployedArmies The number of armies to deploy.
     */
    public Deploy(Player p_issuer, int p_numberOfDeployedArmies, int d_countryId) {
        this.d_issuer = p_issuer;
        this.d_numberOfDeployedArmies = p_numberOfDeployedArmies;
        this.d_countryId = d_countryId;
    }

    /**
     * Getter for field.
     */
    public Player getIssuer() {
        return d_issuer;
    }

    /**
     * Setter for field.
     */
    public void setIssuer(Player p_issuer) {
        this.d_issuer = p_issuer;
    }

    /**
     * Getter for field.
     */
    public int getNumberOfDeployedArmies() {
        return d_numberOfDeployedArmies;
    }

    /**
     * Setter for field.
     */
    public void setNumberOfDeployedArmies(int p_numberOfDeployedArmies) {
        this.d_numberOfDeployedArmies = p_numberOfDeployedArmies;
    }

    public GameSession getD_gameSession() {
        return d_gameSession;
    }

    public void setD_gameSession(GameSession p_gameSession) {
        this.d_gameSession = p_gameSession;
    }

    /**
     * This class executes the "deploy" order by updating
     * accordingly the provided gameSession.
     */
    public void execute() {
        Country l_country = d_issuer.findCountryById(d_issuer.getD_countries_owned(), d_countryId);
        for (int i = 0; i < d_gameSession.getMap().getCountries().size(); i++) {
            if (d_gameSession.getMap().getCountries().get(i).getCountryId() == l_country.getCountryId()) {
                Country actualCountry = d_gameSession.getMap().getCountries().get(i);
                int currentArmies = actualCountry.getExistingArmies();
                actualCountry.setExistingArmies(currentArmies + d_numberOfDeployedArmies);
                break;
            }
        }

        // Catch user action for monitoring observer
        LogEntryBuffer.getInstance().setValue(d_numberOfDeployedArmies + " armies of player: " + d_issuer.getName() + " has been deployed to the country: " + l_country.getName() + ".");
    }
}
