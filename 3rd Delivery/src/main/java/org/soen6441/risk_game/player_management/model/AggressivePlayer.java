package org.soen6441.risk_game.player_management.model;

import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.view.DisplayToUser;
import org.soen6441.risk_game.player_management.strategy.PlayerStrategy;

import java.io.Serializable;

public class AggressivePlayer implements PlayerStrategy, Serializable {

    private final Player d_player;
    private DisplayToUser d_displayToUser;
    private GameSession d_gameSession;

    public AggressivePlayer(Player d_player, GameSession d_gameSession) {
        this.d_player = d_player;
        this.d_displayToUser = d_displayToUser;
        this.d_gameSession = d_gameSession;
    }

    @Override
    public void issueOrder() {

    }
}
