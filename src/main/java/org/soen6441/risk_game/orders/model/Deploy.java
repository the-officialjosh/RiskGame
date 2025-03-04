package org.soen6441.risk_game.orders.model;

import org.soen6441.risk_game.game_engine.model.GameSession;
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
     * @param p_gameSession The game session.
     */
    public void execute(GameSession p_gameSession) {
        for (int i = 0; i <p_gameSession.getPlayers().size(); i++) {
            List orders = p_gameSession.getPlayers().get(i).getOrders();
            for (int j = 0; j < orders.size(); j++) {
                Deploy order = (Deploy) orders.get(j);
                for (int k = 0; k < p_gameSession.getMap().getCountries().size(); k++) {
                    if(p_gameSession.getMap().getCountries().get(k).getCountryId()==order.d_countryId){
                        Map<Player, Integer> map = new HashMap<>();
                        map.put(p_gameSession.getPlayers().get(i),order.d_numberOfDeployedArmies);
                        p_gameSession.getMap().getCountries().get(k).setExistingArmies(map);
                        break;
                    }
                }
            }
        }
    }
}
