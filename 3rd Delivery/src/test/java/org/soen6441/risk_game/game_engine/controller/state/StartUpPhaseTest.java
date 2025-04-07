package org.soen6441.risk_game.game_engine.controller.state;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.soen6441.risk_game.game_engine.model.GameSession;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(OrderAnnotation.class)
public class StartUpPhaseTest {
    @BeforeAll
    static void beforeAll() {
        System.setIn(new ByteArrayInputStream(("""
                loadmap europe.map
                mapeditordone
                gameplayer -add Ahmed
                1
                gameplayer -add Irfan
                1
                assigncountries
                """
                +
                """
                loadmap tests/bigeurope-unconnected.map
                mapeditordone
                loadmap europe.map
                mapeditordone
                gameplayer -add Ahmed
                1
                gameplayer -add Irfan
                1
                assigncountries
                """)
                .getBytes()));
    }

    @Order(1)
    @Test
    void testStartUpPhaseForEuropeMap() {
        GameSession l_gameSession = new GameSession();
        (new StartupPhase()).handlePhase(l_gameSession);
        assertEquals("England",
                l_gameSession.getMap().getCountries().getFirst().getName());
        assertEquals("Ahmed",
                l_gameSession.getPlayers().getFirst().getName());
    }

    @Order(2)
    @Test
    void testStartUpPhaseForInvalidEuropeMap() {
        GameSession l_gameSession = new GameSession();
        (new StartupPhase()).handlePhase(l_gameSession);
        assertEquals("England",
                l_gameSession.getMap().getCountries().getFirst().getName());
        assertEquals("Ahmed",
                l_gameSession.getPlayers().getFirst().getName());
    }
}
