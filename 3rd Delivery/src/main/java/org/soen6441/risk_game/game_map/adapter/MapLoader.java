package org.soen6441.risk_game.game_map.adapter;

import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.model.GameMap;

/**
 * MapLoader class that uses the appropriate handler
 * based on the detected map format.
 *
 * @author Joshua Onyema
 * @version 1.0
 */
public class MapLoader {

    /**
     * Loads a map using the appropriate handler.
     *
     * @param gameSession The game session
     * @param fileName    The map file name
     */
    public static void loadMap(GameSession gameSession, String fileName) {
        String format = MapFormatDetector.detectFormat(fileName);
        MapFileHandler handler;

        if (format.equals("conquest")) {
            handler = new ConquestMapFileAdapter();
        } else {
            handler = new DominationMapFileHandler();
        }

        handler.loadMap(gameSession, fileName);
    }

    /**
     * Saves a map using the appropriate handler.
     *
     * @param gameMap  The game map
     * @param fileName The map file name
     * @param format   The desired format ("domination" or "conquest")
     */
    public static void saveMap(GameMap gameMap, String fileName, String format) {
        MapFileHandler handler;

        if (format.equals("conquest")) {
            handler = new ConquestMapFileAdapter();
        } else {
            handler = new DominationMapFileHandler();
        }

        handler.saveMap(gameMap, fileName);
    }
}