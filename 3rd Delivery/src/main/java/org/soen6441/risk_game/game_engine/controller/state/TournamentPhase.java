package org.soen6441.risk_game.game_engine.controller.state;

import org.soen6441.risk_game.game_engine.controller.GameEngine;
import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.adapter.ConquestMapFileAdapter;
import org.soen6441.risk_game.game_map.adapter.DominationMapFileHandler;
import org.soen6441.risk_game.game_map.adapter.MapFileHandler;
import org.soen6441.risk_game.game_map.adapter.MapFormatDetector;
import org.soen6441.risk_game.game_map.controller.GameMapController;
import org.soen6441.risk_game.game_map.view.DisplayToUser;
import org.soen6441.risk_game.monitoring.LogEntryBuffer;
import org.soen6441.risk_game.player_management.model.*;
import org.soen6441.risk_game.player_management.strategy.PlayerStrategy;

import java.util.ArrayList;

/**
 * Class for Tournament phase.
 *
 * @author Ahmed Fakhir
 * @author Joshua Onyema
 * @version 1.0
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
        tournamentMode();
        System.exit(0);
    }

    /**
     * Handles the tournament mode gameplay.
     */
    public void tournamentMode() {
        ArrayList<String> l_gameMapsFileNames = new ArrayList<>();
        ArrayList<String> l_computerPlayerStrategies = new ArrayList<>();
        int l_numberOfGames = 0, l_maxTurnsPerEachGame = 0;

        String[] l_tournamentUserDetails = d_tournamentCommand.split(" ");
        for (int i = 0; i < l_tournamentUserDetails.length; i++) {
            switch (l_tournamentUserDetails[i]) {
                case "-M" -> {
                    i++;
                    while (i < l_tournamentUserDetails.length && !l_tournamentUserDetails[i].startsWith("-")) {
                        l_gameMapsFileNames.add(l_tournamentUserDetails[i]);
                        i++;
                    }
                    i--;
                }
                case "-P" -> {
                    i++;
                    while (i < l_tournamentUserDetails.length && !l_tournamentUserDetails[i].startsWith("-")) {
                        l_computerPlayerStrategies.add(l_tournamentUserDetails[i]);
                        i++;
                    }
                    i--;
                }
                case "-G" -> {
                    if (i + 1 < l_tournamentUserDetails.length) {
                        l_numberOfGames = Integer.parseInt(l_tournamentUserDetails[++i]);
                    }
                }
                case "-D" -> {
                    if (i + 1 < l_tournamentUserDetails.length) {
                        l_maxTurnsPerEachGame = Integer.parseInt(l_tournamentUserDetails[++i]);
                    }
                }
            }
        }

        // Prepare report header
        StringBuilder l_resultReport = new StringBuilder(String.format("""
                        M: %s
                        P: %s
                        G: %d
                        D: %d
                        
                        |                              |""",
                l_gameMapsFileNames, l_computerPlayerStrategies, l_numberOfGames, l_maxTurnsPerEachGame));

        for (int i = 0; i < l_numberOfGames; i++) {
            l_resultReport.append(" Game ").append(i + 1).append("                |");
        }

        for (String l_gameMap : l_gameMapsFileNames) {
            l_resultReport.append("\n| ").append(String.format("%-" + 29 + "s", l_gameMap)).append("|");
            for (int i = 0; i < l_numberOfGames; i++) {
                System.out.print("\n\n............................... ⚔️ Map " + l_gameMap + " - Game " + (i + 1) + " ...............................\n\n");
                GameSession l_gameSession = new GameSession();

                d_displayToUser.startupPhaseBeginningMessage();

                // Set computer players
                ArrayList<Player> l_players = new ArrayList<>();
                for (int j = 0; j < l_computerPlayerStrategies.size(); j++) {
                    PlayerStrategy l_playerStrategy;
                    ComputerPlayer l_player = new ComputerPlayer(
                            "Player " + (j + 1),
                            0,
                            new ArrayList<>(),
                            l_gameSession
                    );
                    switch (l_computerPlayerStrategies.get(j)) {
                        case "Aggressive": {
                            l_playerStrategy = new AggressivePlayer(l_player, l_gameSession);
                            break;
                        }
                        case "Benevolent": {
                            l_playerStrategy = new BenevolentPlayer(l_player, l_gameSession);
                            break;
                        }
                        case "Cheater": {
                            l_playerStrategy = new CheaterPlayer(l_player, l_gameSession);
                            break;
                        }
                        case "Random": {
                            l_playerStrategy = new RandomPlayer(l_player, l_gameSession);
                            break;
                        }
                        default:
                            d_displayToUser.instructionMessage("Invalid Player Behavior. Using Aggressive behavior instead.");
                            l_playerStrategy = new AggressivePlayer(l_player, l_gameSession);
                    }
                    l_player.setD_playerStrategy(l_playerStrategy);
                    l_players.add(l_player);
                }
                l_gameSession.setPlayers(l_players);

                // Detect format and load map properly
                String mapFormat = MapFormatDetector.detectFormat(l_gameMap);
                MapFileHandler mapFileHandler = "conquest".equals(mapFormat)
                        ? new ConquestMapFileAdapter()
                        : new DominationMapFileHandler();
                mapFileHandler.loadMap(l_gameSession, l_gameMap);

                // Validate map
                if (!d_gameMapController.validateMap(l_gameSession.getMap())) {
                    System.out.print("⚠️ Invalid Map specified! This game will be skipped.\n");
                    System.out.print("\n\n........................... ⚔️ Map " + l_gameMap + " - Game " + (i + 1) + " End Game ..........................\n");
                    l_resultReport.append(" Skipped (Invalid Map) |");
                    continue;
                }

                // Assign countries
                d_gameMapController.assignCountries(l_gameSession);

                d_displayToUser.startupPhaseEndMessage();

                // Tournament Game loop
                int turnCounter = 0;
                boolean isPlayerWonTheGame;
                do {
                    // Issue Order Phase
                    Phase phase = new IssueOrderPhase();
                    phase.handlePhase(l_gameSession);

                    // Execute order phase
                    phase = new ExecuteOrderPhase();
                    phase.handlePhase(l_gameSession);
                    isPlayerWonTheGame = GameEngine.isGameFinished(l_gameSession, new DisplayToUser());
                    turnCounter++;
                } while (!isPlayerWonTheGame && (turnCounter != l_maxTurnsPerEachGame));

                // Update the final report
                if (!isPlayerWonTheGame)
                    // In this case the game did not finish because a player won
                    // the game but because the maximum counter was reached
                    l_resultReport.append(" ")
                            .append(String.format("%-" + 21 + "s", "Draw"))
                            .append(" |");
                else
                    l_resultReport.append(" ")
                        .append(String.format("%-" + 21 + "s",  l_gameSession.getPlayers().getFirst().getD_playerStrategy().toString()))
                        .append(" |");

                System.out.print("\n\n........................... ⚔️ Map " + l_gameMap + " - Game " + (i + 1) + " End Game ..........................\n");
            }
        }

        System.out.print("\n⚔️ Tournament ended! Here is the result report:\n\n" + l_resultReport + "\n");

        LogEntryBuffer.getInstance().setValue("\nTournament result report:\n\n" + l_resultReport);
    }
}