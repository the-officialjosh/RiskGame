package org.soen6441.risk_game.game_engine.controller.state;

import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.controller.GameMapController;
import org.soen6441.risk_game.player_management.controller.PlayerController;
import org.soen6441.risk_game.player_management.model.Player;

import java.io.Serializable;

/**
 * Class for IssueOrder phase
 */
public class IssueOrderPhase implements Phase, Serializable {
    private GameMapController d_gameMapController;
    private PlayerController d_playerController;

    /**
     * Constructor for class.
     */
    public IssueOrderPhase() {
        d_gameMapController = new GameMapController();
        d_playerController = new PlayerController();
    }

    /**
     * Contains the implementation details for a specific phase.
     */
    @Override
    public void handlePhase(GameSession p_gameSession) {
        // Issue order step
        d_gameMapController.assignReinforcements(p_gameSession);
        d_playerController.issueOrderPhase(p_gameSession, 0);
    }

}
