package org.soen6441.risk_game.player_management.controller;

import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.orders.model.Order;
import org.soen6441.risk_game.player_management.model.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * This class holds all the logic for players management.
 */
public class PlayerController {
    /**
     * This method handles the players loading step.
     * @param p_gameSession The game session.
     */
    public void loadPlayers(GameSession p_gameSession) {
        Scanner l_scanner = new Scanner(System.in);
        String l_addPlayerCommand = l_scanner.nextLine();
        String[] l_addPlayerCommandArray = l_addPlayerCommand.split(" ");
        String l_command = l_addPlayerCommandArray[0];
        String l_action = l_addPlayerCommandArray[1];
        List<Player> playerList = new ArrayList<>();
        if(!l_command.equals("gameplayer")){
            System.out.println("Invalid Command");
            return;
        }
        if(!l_action.equals("-add") && !l_action.equals("-remove")){
            System.out.println("Invalid Command");
            return;
        }
        if(l_action.equals("-add")){
            for(int i= 2; i < l_addPlayerCommandArray.length; i++){
                Player player = new Player(l_addPlayerCommandArray[i],0,new ArrayList<Order>());
                playerList.add(player);
            }
        }
//        for(int i = 0; i < playerList.size(); i++){
//            System.out.println(playerList.get(i).getName());
//        }
        p_gameSession.setPlayers(playerList);


    }
}
