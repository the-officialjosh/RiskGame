package org.soen6441.risk_game.game_map.adapter;

import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.model.GameMap;

/**
 * Interface for map file handlers.
 * Supports loading and saving maps in different formats.
 *
 * @author Joshua Onyema
 * @version 1.0
 */
public interface MapFileHandler {

    /**
     * Load a map file into the game session.
     *
     * @param gameSession the game session
     * @param fileName    the map file name
     */
    void loadMap(GameSession gameSession, String fileName);

    /**
     * Save the current game map to a file.
     *
     * @param gameMap  the game map
     * @param fileName the map file name
     */
    void saveMap(GameMap gameMap, String fileName);
}