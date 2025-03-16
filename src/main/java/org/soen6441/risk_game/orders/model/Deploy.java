package org.soen6441.risk_game.orders.model;

import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.model.Country;
import org.soen6441.risk_game.monitoring.LogEntryBuffer;
import org.soen6441.risk_game.player_management.model.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents the Deploy order.
 *
 * @author Ahmed Fakhir
 * @author Irfan Maknojia
 * @author Safin Mahesania
 * @version 1.0
 */
public class Deploy implements Order {

    private Player d_issuer;
    private int d_numberOfDeployedArmies;
    private int d_countryId;

    /**
     * Constructor for class.
     *
     * @param p_issuer               The player who issued the order.
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

    /**
     * This class executes the "deploy" order by updating
     * accordingly the provided gameSession.
     */
    public void execute() {
        GameSession l_gameSession = GameSession.getInstance();
        Country l_country = d_issuer.findCountryById(d_issuer.getD_countries_owned(), d_countryId);
        Map<Player, Integer> l_map = new HashMap<>();
        l_map.put(d_issuer, d_numberOfDeployedArmies);
        for (int i = 0; i < l_gameSession.getMap().getCountries().size(); i++) {
            if (l_gameSession.getMap().getCountries().get(i).getCountryId() == l_country.getCountryId()) {
                l_gameSession.getMap().getCountries().get(i).setExistingArmies(l_map);
                break;
            }
        }

        // Catch user action for monitoring observer
        LogEntryBuffer.getInstance().setValue(d_numberOfDeployedArmies + " armies of player: " + d_issuer.getName()
                + " has been deployed to the country: " + l_country.getName()  + ".");
    }
}
