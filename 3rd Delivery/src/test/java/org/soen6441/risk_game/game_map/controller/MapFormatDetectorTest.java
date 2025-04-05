package org.soen6441.risk_game.game_map.controller;

import org.junit.jupiter.api.Test;
import org.soen6441.risk_game.game_map.adapter.MapFormatDetector;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for MapFormatDetector.
 */
public class MapFormatDetectorTest {

    @Test
    public void testDetectDominationFormat() {
        String dominationFile = "europe.map";
        String format = MapFormatDetector.detectFormat(dominationFile);
        assertEquals("domination", format, "Should detect Domination format");
    }

    @Test
    public void testDetectConquestFormat() {
        String conquestFile = "eurasia.map";
        String format = MapFormatDetector.detectFormat(conquestFile);
        assertEquals("conquest", format, "Should detect Conquest format");
    }

    @Test
    public void testUnknownFormat() {
        String unknownFile = "nonexistent.map";
        String format = MapFormatDetector.detectFormat(unknownFile);
        assertEquals("unknown", format, "Should detect unknown format for non-existing file");
    }
}