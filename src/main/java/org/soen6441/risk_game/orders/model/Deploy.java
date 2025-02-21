package org.soen6441.risk_game.orders.model;

import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.player_management.model.Player;

/**
 * This class represents the Deploy order.
 */
public class Deploy implements Order {

    private Player d_issuer;
    private int d_numberOfDeployedArmies;

    /**
     * Constructor for class.
     * @param p_issuer
     * @param p_numberOfDeployedArmies
     */
    public Deploy(Player p_issuer, int p_numberOfDeployedArmies) {
        this.d_issuer = p_issuer;
        this.d_numberOfDeployedArmies = p_numberOfDeployedArmies;
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

    }
}
