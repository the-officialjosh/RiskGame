package org.soen6441.risk_game.orders.model;

import org.soen6441.risk_game.player_management.model.Player;

public class Advance implements Order{

    private Player d_issuer;
    private int d_numberOfDeployedArmies;
    private int d_countryId;

    public Advance(Player d_issuer, int d_numberOfDeployedArmies, int d_countryId) {
        this.d_issuer = d_issuer;
        this.d_numberOfDeployedArmies = d_numberOfDeployedArmies;
        this.d_countryId = d_countryId;
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

    public int getD_countryId() {
        return d_countryId;
    }

    public void setD_countryId(int d_countryId) {
        this.d_countryId = d_countryId;
    }

    @Override
    public void execute() {

    }
}
