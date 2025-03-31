package org.soen6441.risk_game.game_engine.controller.state;

import org.junit.jupiter.api.Test;
import org.soen6441.risk_game.game_engine.model.GameSession;

import static org.junit.jupiter.api.Assertions.*;

public class TournamentPhaseTest {
    @Test
    void testTournamentPhaseWithValidMaps() {
        TournamentPhase tournamentPhase = new TournamentPhase("tournament -M europe.map canada.map solar.map sudamerica.map -P Aggressive Cheater Random Benevolent -G 4 -D 30");
        assertDoesNotThrow(() -> tournamentPhase.tournamentMode());
    }

    @Test
    void testTournamentPhaseWithInvalidMaps() {
        TournamentPhase tournamentPhase = new TournamentPhase("tournament -M europe.map canada.map solar.map invalid.map -P Aggressive Cheater Random Benevolent -G 4 -D 30");
        assertDoesNotThrow(() -> tournamentPhase.tournamentMode());
    }
}
