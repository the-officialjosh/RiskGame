package org.soen6441.risk_game.game_engine.controller;

import org.soen6441.risk_game.game_engine.controller.state.*;
import org.soen6441.risk_game.game_engine.controller.user_input.UserInputScanner;
import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.view.DisplayToUser;
import org.soen6441.risk_game.monitoring.LogEntryBuffer;
import org.soen6441.risk_game.monitoring.LogEntryBufferObserver;

import java.io.File;
import java.util.Scanner;

/**
 * GameEngine class which is serving as the game entry point as well as handling
 * the global game logic.
 *
 * @author Ahmed Fakhir
 * @author Safin Mahesania
 * @author Irfan Maknojia
 * @version 1.0
 */
public class GameEngine {
    private static String outputFolder = "out/";

    /**
     * Main method which is the entry point method of the game
     */
    public static void main(String[] args) {
        // Game Session
        GameSession l_gameSession = new GameSession();
        // Game Phase
        Phase phase;

        // View object
        DisplayToUser l_displayToUser = new DisplayToUser();

        // Configure Observer pattern for monitoring
        LogEntryBuffer.getInstance().addObserver(new LogEntryBufferObserver(outputFolder + "observed_actions.txt"));
        LogEntryBuffer.getInstance().setValue("Single game mode chosen.");

        l_displayToUser.welcomeMessage();

        // Startup Phase
        phase = new StartupPhase();
        phase.handlePhase(l_gameSession);

        // Game loop
        while (true) {
            // Issue Order Phase
            phase = new IssueOrderPhase();
            phase.handlePhase(l_gameSession);

            // Execute order phase
            phase = new ExecuteOrderPhase();
            phase.handlePhase(l_gameSession);
        }
    }

    public static void startTournamentModeGame(String l_tournamentCommand) {
        // Game Phase
        Phase phase;

        // Configure Observer pattern for monitoring
        LogEntryBuffer.getInstance().addObserver(new LogEntryBufferObserver(outputFolder + "observed_actions.txt"));
        LogEntryBuffer.getInstance().setValue("Tournament mode chosen.");

        // Startup Phase for Tournament games
        phase = new TournamentPhase(l_tournamentCommand);

        try {
            phase.handlePhase(new GameSession());
        } catch (Exception e) {
            System.out.print("Unexpected Error in Tournament Gameplay:\n");
            e.printStackTrace();
            System.exit(0);
        }
    }

    private static boolean loadSaveGame(GameSession p_gameSession) {
        Scanner l_scanner = UserInputScanner.getInstance().getScanner();
        boolean isFileLoaded = false;
        DisplayToUser l_displayToUser = new DisplayToUser();
        l_displayToUser.instructionMessage("Do you want to load game?\nY for Yes\tN for No");
        char opt;
        while (true) {
            opt = l_scanner.nextLine().charAt(0);
            if (opt == 'N' || opt == 'n' || opt == 'Y' || opt == 'y') break;
            System.out.println("Invalid option. Please select the correct option.");
        }

        if (opt == 'n' || opt == 'N') {
            System.out.println("No game loaded.");
        } else {
            l_displayToUser.instructionMessage("Use \"loadgame <filename>\" to load game.");
            l_displayToUser.instructionMessage("Use \"exit\" to continue to game without loading a game.");

            while (true) {
                String l_command = l_scanner.nextLine().trim();
                if (l_command.equalsIgnoreCase("exit")) {
                    break;
                } else {
                    String[] l_command_parts = l_command.split(" ");
                    if (l_command_parts[0].equalsIgnoreCase("loadgame")) {
                        if (l_command_parts.length != 2) {
                            l_displayToUser.instructionMessage("Invalid command. Use: loadgame <filename>");
                            continue;
                        }
                        String folderName = "out/save-game/" + l_command_parts[1] + ".dat";
                        File folder = new File(folderName);

                        if (folder.exists()) {
                            p_gameSession.loadGame(l_command_parts[1]);
                            isFileLoaded = true;
                            break;
                        } else {
                            l_displayToUser.instructionMessage("File not found.\nPlease enter correct file name.");


                        }
                    }

                }
            }
        }
        return isFileLoaded;
    }
}