package org.soen6441.risk_game.orders.model;

import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.player_management.model.Player;
import org.soen6441.risk_game.monitoring.LogEntryBuffer;

/**
 * The Diplomacy order establishes a temporary truce between two players for the current turn.
 * Neither player can attack the other after this order is executed.
 *
 * @author Joshua Onyema
 * @version 1.0
 */
public class Diplomacy implements Order {

    private Player d_issuer;
    private Player d_target;

    /**
     * Creates a new Diplomacy order between the issuing player and the target player.
     *
     * @param p_issuer the player issuing the diplomacy order
     * @param p_target the target player to establish diplomacy with
     */
    public Diplomacy(Player p_issuer, Player p_target) {
        this.d_issuer = p_issuer;
        this.d_target = p_target;
    }

    /**
     * Executes the diplomacy order, updating the GameSession to record the truce.
     */
    @Override
    public void execute() {
        GameSession l_gameSession = GameSession.getInstance();

        l_gameSession.addDiplomacyPair(d_issuer, d_target);

        System.out.println("🕊️ " + d_issuer.getName() + " and " + d_target.getName() + " are now in diplomacy.");
        LogEntryBuffer.getInstance().setValue("🕊️ Diplomacy: " + d_issuer.getName() + " and " + d_target.getName() + " cannot attack each other this turn.");
    }
}
