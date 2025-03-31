package org.soen6441.risk_game.game_engine.controller.state;

import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.controller.GameMapController;
import org.soen6441.risk_game.game_map.view.DisplayToUser;
import org.soen6441.risk_game.monitoring.LogEntryBuffer;
import org.soen6441.risk_game.player_management.model.ComputerPlayer;
import org.soen6441.risk_game.player_management.model.Player;

import java.util.ArrayList;

/**
 * Class for Tournament phase.
 */
public class TournamentPhase implements Phase {
    private GameMapController d_gameMapController;
    private DisplayToUser d_displayToUser;
    private String d_tournamentCommand;

    /**
     * Constructor for class.
     */
    public TournamentPhase(String p_tournamentCommand) {
        d_gameMapController = new GameMapController();
        d_displayToUser = new DisplayToUser();
        d_tournamentCommand = p_tournamentCommand;
    }

    /**
     * {@inheritDoc}
     */
    public void handlePhase(GameSession p_gameSession) {
        // "p_gameSession" parameter will not be used since we will create
        // new GameSession objects for each game
        tournamentMode();
        System.exit(0);
    }

    public void tournamentMode() {
        ArrayList<String> l_gameMapsFileNames = new ArrayList<String>();
        ArrayList<String> l_computerPlayerStrategies = new ArrayList<String>();
        int l_numberOfGames = 0, l_maxTurnsPerEachGame = 0;
        String[] l_tournamentUserDetails = d_tournamentCommand.split(" ");
        for (int i = 0; i < l_tournamentUserDetails.length; i++) {
            if (l_tournamentUserDetails[i].equals("-M")) {
                if (i == (l_tournamentUserDetails.length - 1))
                    break;
                else
                    i++;
                while (l_tournamentUserDetails[i].toCharArray()[0] != '-') {
                    l_gameMapsFileNames.add(l_tournamentUserDetails[i]);
                    if (i == (l_tournamentUserDetails.length - 1))
                        break;
                    else
                        i++;
                }
                i--;
            }
            if (l_tournamentUserDetails[i].equals("-P")) {
                if (i == (l_tournamentUserDetails.length - 1))
                    break;
                else
                    i++;
                while (l_tournamentUserDetails[i].toCharArray()[0] != '-') {
                    l_computerPlayerStrategies.add(l_tournamentUserDetails[i]);
                    if (i == (l_tournamentUserDetails.length - 1))
                        break;
                    else
                        i++;
                }
                i--;
            }
            if (l_tournamentUserDetails[i].equals("-G")) {
                if (i == (l_tournamentUserDetails.length - 1))
                    break;
                else
                    i++;
                l_numberOfGames = Integer.valueOf(l_tournamentUserDetails[i]);
            }
            if (l_tournamentUserDetails[i].equals("-D")) {
                if (i == (l_tournamentUserDetails.length - 1))
                    break;
                else
                    i++;
                l_maxTurnsPerEachGame = Integer.valueOf(l_tournamentUserDetails[i]);
            }
        }

        // Define the string that hold the report shown to the user
        String l_resultReport = String.format("""
                        M: %s
                        P: %s
                        G: %d
                        D: %d
                        
                        |                               |""",
                l_gameMapsFileNames.toString(), l_computerPlayerStrategies.toString(),
                l_numberOfGames, l_maxTurnsPerEachGame);

        for (int i = 0; i < l_numberOfGames; i++)
            l_resultReport += " Game " + (i + 1) + "                |";

        for (String l_gameMap : l_gameMapsFileNames) {
            l_resultReport += "\n| " + String.format("%-" + 21 + "s", l_gameMap) + "         |";
            for (int i = 0; i < l_numberOfGames; i++) {
                System.out.print("\n\n............................... ⚔\uFE0F Map " + l_gameMap + " - Game " + (i + 1) + " ...............................\n\n");
                GameSession l_gameSession = new GameSession();

                // Show startup end message
                d_displayToUser.startupPhaseBeginningMessage();

                // Set the ComputerPlayers with the specified strategies
                ArrayList<Player> l_players = new ArrayList<>();
                for (int j = 0; j < l_computerPlayerStrategies.size(); j++) {
                    // TODO: Ensure GamePlayer are correctly created
                    ComputerPlayer player = new ComputerPlayer("Player " + (j + 1), 0, new ArrayList<>(), l_gameSession, l_computerPlayerStrategies.get(j));
                    l_players.add(player);
                }
                l_gameSession.setPlayers(l_players);

                // Load the map
                d_gameMapController.loadMap(l_gameSession, l_gameMap);
                if (!(d_gameMapController.validateMap(l_gameSession.getMap()))) {
                    System.out.print("⚠\uFE0F Invalid Map specified! This game will be skipped.");
                    System.out.print("\n\n........................... ⚔\uFE0F Map " + l_gameMap + " - Game " + (i + 1) + " End Game ..........................\n");
                    // Update the report
                    l_resultReport += " Skipped (Invalid Map) |";
                    continue;
                }

                // Assign countries step
                d_gameMapController.assignCountries(l_gameSession);

                // Show startup end message
                d_displayToUser.startupPhaseEndMessage();

                // Game loop
                // TODO: Update end condition of a tournament game
                // TODO: Take into consideration the maximum of turns
                /*while (true) {
                    // Issue Order Phase
                    Phase phase = new IssueOrderPhase();
                    phase.handlePhase(l_gameSession);

                    // Execute order phase
                    phase = new ExecuteOrderPhase();
                    phase.handlePhase(l_gameSession);
                }*/

                // Update the report
                l_resultReport += " " + String.format("%-" + 21 + "s", ((ComputerPlayer) l_gameSession.getPlayers().getFirst()).getD_playerBehavior()) + " |";

                System.out.print("\n\n........................... ⚔\uFE0F Map " + l_gameMap + " - Game " + (i + 1) + " End Game ..........................\n");
            }
        }

        // show tournament result report
        System.out.print("\n⚔\uFE0F Tournament ended! Here is the result report:\n\n" + l_resultReport + "\n");

        LogEntryBuffer.getInstance().setValue("\nTournament result report:\n\n" + l_resultReport);
    }
}
