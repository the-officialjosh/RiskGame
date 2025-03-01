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

    private DisplayToUser d_displayToUser = new DisplayToUser();

    /**
     * This method handles the players loading step.
     * @param p_gameSession The game session.
     */
    public void loadPlayers(GameSession p_gameSession) {
        d_displayToUser.instructionMessage("Add players to the game by using (\"gameplayer -add nameofplayer\") command.\nAfter adding players you can use (\"assigncountries\")command");
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
                d_displayToUser.instructionMessage("Invalid Command (To add player use this command \"gameplayer -add nameofplayer\"");
                continue;
            }
            String l_action = l_addPlayerCommandArray[1];
            if(!l_action.equals("-add") && !l_action.equals("-remove")){
                d_displayToUser.instructionMessage("Invalid Command (To add player use this command \"gameplayer -add nameofplayer\"");
                continue;
            }
            if(l_action.equals("-add")){
                Player playerToAdd = new Player(l_addPlayerCommandArray[2], 0, new ArrayList<Order>());
                boolean exists = playerList.stream().anyMatch(player -> player.getName().equalsIgnoreCase(playerToAdd.getName()));
                if(exists){
                    d_displayToUser.instructionMessage("Player already exist. Try with different name");
                    continue;
                }
                playerList.add(playerToAdd);
                d_displayToUser.instructionMessage("Player added.");
            }
            if(l_action.equals("-remove")){
                if(!playerList.isEmpty()){
                   boolean isRemoved = playerList.removeIf(player -> player.getName().equalsIgnoreCase(l_addPlayerCommandArray[2]));
                   if(isRemoved){
                       d_displayToUser.instructionMessage("Player removed");
                   }else {
                       d_displayToUser.instructionMessage("Could not found player with this name");
                   }
                }else {
                    d_displayToUser.instructionMessage("There are no players to remove.");
                }
            }
        }while (true);
//        for(Player player: playerList){
//            System.out.println(player.getName());
//        }
        p_gameSession.setPlayers(playerList);
    }
}
