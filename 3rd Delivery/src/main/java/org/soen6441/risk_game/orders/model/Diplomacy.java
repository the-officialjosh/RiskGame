package org.soen6441.risk_game.orders.model;

import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.player_management.model.Player;
import org.soen6441.risk_game.monitoring.LogEntryBuffer;

import java.io.Serializable;

/**
 * The Diplomacy order establishes a temporary truce between two players for the current turn.
 * Neither player can attack the other after this order is executed.
 *
 * @author Joshua Onyema
 * @version 1.0
 */
public class Diplomacy implements Order, Serializable {

    private Player d_issuer;
    private Player d_target;
    private int count;
    private GameSession d_gameSession;

    public Player getD_issuer() {
        return d_issuer;
    }

    public void setD_issuer(Player d_issuer) {
        this.d_issuer = d_issuer;
    }

    public Player getD_target() {
        return d_target;
    }

    public void setD_target(Player d_target) {
        this.d_target = d_target;
    }

    public int getCount() {
        return count;
    }

    public void incrementCount() {
        this.count++;
    }

    public GameSession getD_gameSession() {
        return d_gameSession;
    }

    public void setD_gameSession(GameSession p_gameSession) {
        this.d_gameSession = p_gameSession;
    }

    /**
     * Creates a new Diplomacy order between the issuing player and the target player.
     *
     * @param p_issuer the player issuing the diplomacy order
     * @param p_target the target player to establish diplomacy with
     */
    public Diplomacy(Player p_issuer, Player p_target) {
        this.d_issuer = p_issuer;
        this.d_target = p_target;
        this.count = 0;
    }

    /**
     * Executes the diplomacy order, updating the GameSession to record the truce.
     */
    @Override
    public void execute() {
        d_gameSession.addDiplomacyPair(d_issuer, d_target);


        System.out.println("🕊️ " + d_issuer.getName() + " and " + d_target.getName() + " are now in diplomacy.");
        LogEntryBuffer.getInstance().setValue("🕊️ Diplomacy: " + d_issuer.getName() + " and " + d_target.getName() + " cannot attack each other this turn.");
    }
}
