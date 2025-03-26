package org.soen6441.risk_game.orders.model;

import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.player_management.model.Player;
import org.soen6441.risk_game.monitoring.LogEntryBuffer;

/**
 * The Diplomacy order establishes a temporary truce between two players for the current turn.
 * Neither player can attack the other after this order is executed.
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

        if (d_issuer == null || d_target == null) {
            System.out.println("❌ Diplomacy failed: one of the players is null.");
            return;
        }

        if (d_issuer.equals(d_target)) {
            System.out.println("❌ Diplomacy failed: a player cannot establish diplomacy with themselves.");
            return;
        }

        if (l_gameSession.areInDiplomacy(d_issuer, d_target)) {
            System.out.println("ℹ️ Diplomacy already exists between " + d_issuer.getName() + " and " + d_target.getName() + ".");
            return;
        }

        l_gameSession.addDiplomacyPair(d_issuer, d_target);

        System.out.println("🕊️ " + d_issuer.getName() + " and " + d_target.getName() + " are now in diplomacy.");
        LogEntryBuffer.getInstance().setValue("🕊️ Diplomacy: " + d_issuer.getName() + " and " + d_target.getName() + " cannot attack each other this turn.");
    }
}
