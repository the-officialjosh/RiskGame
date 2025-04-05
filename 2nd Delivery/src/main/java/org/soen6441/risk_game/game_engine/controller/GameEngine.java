package org.soen6441.risk_game.game_engine.controller;

import org.soen6441.risk_game.game_engine.controller.state.ExecuteOrderPhase;
import org.soen6441.risk_game.game_engine.controller.state.IssueOrderPhase;
import org.soen6441.risk_game.game_engine.controller.state.Phase;
import org.soen6441.risk_game.game_engine.controller.state.StartupPhase;
import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.view.DisplayToUser;
import org.soen6441.risk_game.monitoring.LogEntryBuffer;
import org.soen6441.risk_game.monitoring.LogEntryBufferObserver;

/**
 * GameEngine class which is serving as the game entry point as well as handling
 * the global game logic.
 *
 * @author Ahmed Fakhir
 * @author Safin Mahesania
 * @author Irfan Maknojia
 * @version 1.0
 *
 */
public class GameEngine {
    private static String outputFolder = "out/";

    /**
     * Main method which is the entry point method of the game.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        // Game Session
        GameSession l_gameSession = GameSession.getInstance();
        // Game Phase
        Phase phase;

        // View object
        DisplayToUser l_displayToUser = new DisplayToUser();

        // Configure Observer pattern for monitoring
        LogEntryBuffer.getInstance().addObserver(new LogEntryBufferObserver(outputFolder + "observed_actions.txt"));

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
}