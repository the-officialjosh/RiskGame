package org.soen6441.risk_game.orders.model;

import org.soen6441.risk_game.player_management.model.Player;

public class Advance implements Order{

    private Player d_issuer;
    private int d_numberOfDeployedArmies;
    private int d_fromCountryId;
    private int d_toCountryId;

    public Advance(Player d_issuer, int d_fromCountryId, int d_toCountryId, int d_numberOfDeployedArmies) {
        this.d_issuer = d_issuer;
        this.d_numberOfDeployedArmies = d_numberOfDeployedArmies;
        this.d_fromCountryId = d_fromCountryId;
        this.d_toCountryId = d_toCountryId;
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

    public int getD_fromCountryId() {
        return d_fromCountryId;
    }

    public int getD_toCountryId() {
        return d_toCountryId;
    }

    public void setD_toCountryId(int d_toCountryId) {
        this.d_toCountryId = d_toCountryId;
    }

    public void setD_fromCountryId(int d_fromCountryId) {
        this.d_fromCountryId = d_fromCountryId;
    }

    @Override
    public void execute() {

    }
}
