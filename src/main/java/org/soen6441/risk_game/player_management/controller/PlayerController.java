package org.soen6441.risk_game.player_management.controller;

import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.view.DisplayToUser;
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
        DisplayToUser.instructionMessage("Add players to the game by using (\"gameplayer -add nameofplayer\") command.\nAfter adding players you can use (\"assigncountries\")command");
        Scanner l_scanner = new Scanner(System.in);
        String l_command = "";
        String l_addPlayerCommand;
        List<Player> playerList = new ArrayList<>();
        do{
            l_addPlayerCommand = l_scanner.nextLine();
            String[] l_addPlayerCommandArray = l_addPlayerCommand.split(" ");
            l_command = l_addPlayerCommandArray[0];
            if(l_command.equals("assigncountries")){
                break;
            }
            if(!l_command.equals("gameplayer")){
                System.out.println("Invalid Command (To add player use this command \"gameplayer -add nameofplayer\"");
                continue;
            }
            String l_action = l_addPlayerCommandArray[1];
            if(!l_action.equals("-add") && !l_action.equals("-remove")){
                System.out.println("Invalid Command (To add player use this command \"gameplayer -add nameofplayer\"");
                continue;
            }
            if(l_action.equals("-add")){
                Player player = new Player(l_addPlayerCommandArray[2], 0, new ArrayList<Order>());
                playerList.add(player);
            }
        }while (true);

        p_gameSession.setPlayers(playerList);


    }

    //remove player
}
