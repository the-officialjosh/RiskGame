package org.soen6441.risk_game.orders.model;

import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.model.Country;
import org.soen6441.risk_game.monitoring.LogEntryBuffer;
import org.soen6441.risk_game.player_management.model.Player;
import java.util.HashMap;

/**
 * The Bomb order class.
 * Destroys half of the armies in an opponent’s adjacent territory, consuming one bomb card.
 *
 * @author Joshua Onyema
 * @author Safin Mahesania
 * @version 1.0
 */
public class Bomb implements Order {

    private Player d_player;
    private Country d_sourceCountry;
    private Country d_targetCountry;

    /**
     * Instantiates a new Bomb order.
     *
     * @param p_sourceCountry the source country (must be owned by the player)
     * @param p_player the player issuing the order
     * @param p_targetCountry the enemy country to bomb
     */
    public Bomb(Country p_sourceCountry, Player p_player, Country p_targetCountry) {
        this.d_sourceCountry = p_sourceCountry;
        this.d_player = p_player;
        this.d_targetCountry = p_targetCountry;
    }

    /**
     * Executes the Bomb order, validating preconditions and applying effects.
     */
    @Override
    public void execute() {
        GameSession l_gameSession = GameSession.getInstance();
        Country l_target = l_gameSession.getMap().getCountriesById(d_targetCountry.getCountryId());
        Player l_targetOwner = l_target.getD_ownedBy();
        /*if (l_target == null) {
            System.out.println("❌ Target country does not exist.");
            return;
        }*/



        // Validation: player must own the source country
        /*if (!d_player.equals(d_sourceCountry.getD_ownedBy())) {
            System.out.println("❌ Invalid order: you do not own the source country.");
            return;
        }

        // Validation: can't bomb your own country
        if (d_player.equals(l_targetOwner)) {
            System.out.println("❌ Invalid order: you cannot bomb your own country.");
            return;
        }

        // Validation: cannot bomb if in diplomacy
        if (l_targetOwner != null && GameSession.getInstance().areInDiplomacy(d_player, l_targetOwner)) {
            System.out.println("❌ Invalid order: you cannot bomb a player you're in diplomacy with.");
            return;
        }

        // Validation: countries must be adjacent
        if (!d_sourceCountry.getAdjacentCountries().contains(l_target)) {
            System.out.println("❌ Invalid order: target country is not adjacent to source country.");
            return;
        }

        // Placeholder check – adjust this if getBombCards() is not defined
        if (!d_player.hasBombCard()) {
            System.out.println("❌ Invalid order: no bomb cards available.");
            return;
        }*/

        // Proceed with bombing
        int l_currentArmies = l_target.getExistingArmies().getOrDefault(l_targetOwner, 0);
        int l_reducedArmies = l_currentArmies / 2;

        HashMap<Player, Integer> updatedArmies = new HashMap<>();
        updatedArmies.put(l_targetOwner, l_reducedArmies);
        //l_target.setExistingArmies(updatedArmies);
        l_gameSession.getMap().getCountriesById(d_targetCountry.getCountryId()).setExistingArmies(updatedArmies);

        // Consume one bomb card
        d_player.useBombCard();

        System.out.println("💣 Bomb executed on " + l_target.getName() + ". Armies reduced from " +
                l_currentArmies + " to " + l_reducedArmies + ". Bomb card used.");

        LogEntryBuffer.getInstance().setValue(
            "💣 " + d_player.getName() + " bombed " + l_target.getName() +
            ", reducing armies from " + l_currentArmies + " to " + l_reducedArmies + "."
        );
    }
}
