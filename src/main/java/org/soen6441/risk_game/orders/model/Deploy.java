package org.soen6441.risk_game.orders.model;

import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.model.Country;
import org.soen6441.risk_game.player_management.model.Player;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents the Deploy order.
 */
public class Deploy implements Order {

    private Player d_issuer;
    private int d_numberOfDeployedArmies;
    private int d_countryId;
    /**
     * Constructor for class.
     * @param p_issuer
     * @param p_numberOfDeployedArmies
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
        Country country = d_issuer.findCountryById(d_issuer.getD_countries_owned(), d_countryId);
        Map<Player, Integer> map = new HashMap<>();
        map.put(d_issuer,d_numberOfDeployedArmies);
        for (int i = 0; i < l_gameSession.getMap().getCountries().size(); i++) {
             if(l_gameSession.getMap().getCountries().get(i).getCountryId()==country.getCountryId()){
                 l_gameSession.getMap().getCountries().get(i).setExistingArmies(map);
                 break;
             }
        }
    }
}
