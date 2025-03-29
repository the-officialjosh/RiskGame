package org.soen6441.risk_game.orders.model;

import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.model.Country;
import org.soen6441.risk_game.monitoring.LogEntryBuffer;
import org.soen6441.risk_game.player_management.model.Player;

import java.util.HashMap;
import java.util.Random;

/**
 * This class represents the Advance order.
 * @author Irfan Maknojia
 * @author Safin Mahesania
 * @version 1.0
 */
public class Advance implements Order{

    private Player d_issuer;
    private int d_numberOfDeployedArmies;
    private Country d_fromCountry;
    private Country d_toCountry;

    public Advance(Player d_issuer, Country d_fromCountry, Country d_toCountry, int d_numberOfDeployedArmies) {
        this.d_issuer = d_issuer;
        this.d_numberOfDeployedArmies = d_numberOfDeployedArmies;
        this.d_fromCountry = d_fromCountry;
        this.d_toCountry = d_toCountry;
    }

    public Player getD_issuer() {
        return d_issuer;
    }

    public void setD_issuer(Player d_issuer) {
        this.d_issuer = d_issuer;
    }

    public int getD_numberOfDeployedArmies() {
        return d_numberOfDeployedArmies;
    }

    public void setD_numberOfDeployedArmies(int d_numberOfDeployedArmies) {
        this.d_numberOfDeployedArmies = d_numberOfDeployedArmies;
    }

    public Country getD_fromCountry() {
        return d_fromCountry;
    }

    public Country getD_toCountry() {
        return d_toCountry;
    }

    public void setD_toCountry(Country d_toCountry) {
        this.d_toCountry = d_toCountry;
    }

    public void setD_fromCountry(Country d_fromCountry) {
        this.d_fromCountry = d_fromCountry;
    }

    @Override
    public void execute() {
        Country attackingCountry = d_fromCountry;
        Country defendingCountry = d_toCountry;

        Player attacker = d_fromCountry.getD_ownedBy();
        Player defender = d_toCountry.getD_ownedBy();

        if(attackingCountry.getD_ownedBy().equals(defendingCountry.getD_ownedBy())){
            //move armies;
            if(d_numberOfDeployedArmies > attackingCountry.getExistingArmies()){
                System.out.println(attacker.getName()+ "tried to moves armies from" + attackingCountry.getName()+" to "+defendingCountry.getName()+".");
                System.out.println(attacker.getName() + " does not have enough armies to move.");
                LogEntryBuffer.getInstance().setValue(attacker.getName()+ "tried to moves armies from" + attackingCountry.getName()+" to "+defendingCountry.getName()+".");
                LogEntryBuffer.getInstance().setValue("💣 " +attacker.getName() + " does not have enough armies to move.");
                return;
            }
            attackingCountry.setExistingArmies(attackingCountry.getExistingArmies() - d_numberOfDeployedArmies);
            defendingCountry.setExistingArmies(defendingCountry.getExistingArmies() + d_numberOfDeployedArmies);
            System.out.println(attacker.getName() + "Moved armies from "+ attackingCountry.getName()+" to "+defendingCountry.getName()+"." );
            LogEntryBuffer.getInstance().setValue("💣 " +attacker.getName() + "Moved armies from "+ attackingCountry.getName()+" to "+defendingCountry.getName()+"." );
        }else {
            System.out.println(attacker.getName() + " attacks " + defendingCountry.getName() + " from " + attackingCountry.getName());
            LogEntryBuffer.getInstance().setValue(
                    "💣 " + attacker.getName() + " attacks " + defendingCountry.getName() + " from " + attackingCountry.getName());
            if(GameSession.getInstance().areInDiplomacy(attacker,defender)){
                System.out.println("Can not attack target country because of Diplomacy card");
                LogEntryBuffer.getInstance().setValue("Can not attack target country because of Diplomacy card");
                return;
            }
            if(d_numberOfDeployedArmies > attackingCountry.getExistingArmies()){
                System.out.println(attacker.getName() + " does not have enough armies to attack.");
                LogEntryBuffer.getInstance().setValue("💣 " +attacker.getName() + " does not have enough armies to move.");
                return;
            }
            int attackingArmies = d_numberOfDeployedArmies;
            int defendingArmies = defendingCountry.getExistingArmies();
            Random random = new Random();

            while (attackingArmies > 0 && defendingArmies > 0) {
                // Attackers' chance to kill defenders (60% per army unit)
                for (int i = 0; i < attackingArmies; i++) {
                    if (random.nextDouble() < 0.6) { // 60% chance
                        defendingArmies--;
                    }
                }

                // Defenders' chance to kill attackers (70% per army unit)
                for (int i = 0; i < defendingCountry.getExistingArmies(); i++) {
                    if (random.nextDouble() < 0.7) { // 70% chance
                        attackingArmies--;
                    }
                }
            }
            int remainingArmies = attackingCountry.getExistingArmies() - d_numberOfDeployedArmies;
            attackingCountry.setExistingArmies(remainingArmies);

            if (defendingArmies <= 0) {
                // Attacker wins and captures the territory
                defendingCountry.setD_ownedBy(attacker);
                attacker.setD_countries_owned(defendingCountry);
                defender.getD_countries_owned().remove(defendingCountry);
                defendingCountry.setExistingArmies(attackingArmies);
                attacker.assignCard();
                System.out.println(attacker.getName() + " has conquered " + defendingCountry.getName() + "!");
                LogEntryBuffer.getInstance().setValue("💣 " + attacker.getName() + " has conquered " + defendingCountry.getName() + "!");
            } else {
                // Defender holds the territory
                defendingCountry.setExistingArmies(defendingCountry.getExistingArmies() - defendingArmies);
                System.out.println(defender.getName() + " successfully defended " + defendingCountry.getName());
                LogEntryBuffer.getInstance().setValue("💣 " + defender.getName() + " successfully defended " + defendingCountry.getName());
            }
        }
    }
}
