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
                DisplayToUser.instructionMessage("Invalid Command (To add player use this command \"gameplayer -add nameofplayer\"");
                continue;
            }
            String l_action = l_addPlayerCommandArray[1];
            if(!l_action.equals("-add") && !l_action.equals("-remove")){
                DisplayToUser.instructionMessage("Invalid Command (To add player use this command \"gameplayer -add nameofplayer\"");
                continue;
            }
            if(l_action.equals("-add")){
                Player player = new Player(l_addPlayerCommandArray[2], 0, new ArrayList<Order>());
                playerList.add(player);
                DisplayToUser.instructionMessage("Player added.");
            }
            if(l_action.equals("-remove")){
                if(!playerList.isEmpty()){
                   boolean isRemoved = playerList.removeIf(player -> player.getName().equalsIgnoreCase(l_addPlayerCommandArray[2]));
                   if(isRemoved){
                       DisplayToUser.instructionMessage("Player removed");
                   }else {
                       DisplayToUser.instructionMessage("Could not found player with this name");
                   }
                }else {
                    DisplayToUser.instructionMessage("There are no players to remove.");
                }
            }
        }while (true);
//        for(Player player: playerList){
//            System.out.println(player.getName());
//        }
        p_gameSession.setPlayers(playerList);
    }
}
