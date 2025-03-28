package org.soen6441.risk_game.orders.model;

import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.model.Country;
import org.soen6441.risk_game.monitoring.LogEntryBuffer;
import org.soen6441.risk_game.player_management.model.Player;

/**
 * The Bomb order class.
 * Destroys half of the armies in an opponent’s adjacent territory, consuming one bomb card.
 *
 * @author Joshua Onyema
 * @author Safin Mahesania
 * @author Irfan Maknojia
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
     * @param p_player        the player issuing the order
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

        if (l_gameSession.areInDiplomacy(d_sourceCountry.getD_ownedBy(), d_targetCountry.getD_ownedBy())) {
            System.out.println("Bombing of " + d_sourceCountry.getD_ownedBy().getName() + " on " + d_targetCountry.getD_ownedBy().getName() + "'s territory that is " + d_targetCountry.getName() + " is not possible because they are in diplomacy terms.");
        } else {
            // Proceed with bombing
            int l_currentArmies = l_target.getExistingArmies();
            int l_reducedArmies = l_currentArmies / 2;

            //l_target.setExistingArmies(updatedArmies);
            l_gameSession.getMap().getCountriesById(d_targetCountry.getCountryId()).setExistingArmies(l_reducedArmies);

            System.out.println("💣 Bomb executed on " + l_target.getName() + ". Armies reduced from " +
                    l_currentArmies + " to " + l_reducedArmies + ". Bomb card used.");

            LogEntryBuffer.getInstance().setValue(
                    "💣 " + d_player.getName() + " bombed " + l_target.getName() +
                            ", reducing armies from " + l_currentArmies + " to " + l_reducedArmies + "."
            );
        }
    }
}
