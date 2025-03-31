package org.soen6441.risk_game.player_management.controller;
import org.junit.jupiter.api.Test;
import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.orders.model.Order;
import org.soen6441.risk_game.player_management.model.Player;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlayerControllerTest {

    @Test
    public void testDeployArmyMoreThenReinforcementPool(){
        Player player = new Player("Player1",3,new ArrayList<Order>(), new GameSession());
        boolean result = player.validNumberOfReinforcementArmies(5);
        assertEquals(false,result);
    }

    @Test
    public void testDeployArmyEqualToReinforcementPool(){
        Player player = new Player("Player1",3,new ArrayList<Order>(), new GameSession());
        boolean result = player.validNumberOfReinforcementArmies(3);
        assertEquals(true,result);
    }

    @Test
    public void testDeployArmyLessThenReinforcementPool(){
        Player player = new Player("Player1",3,new ArrayList<Order>(), new GameSession());
        boolean result = player.validNumberOfReinforcementArmies(2);
        assertEquals(true,result);
    }
}