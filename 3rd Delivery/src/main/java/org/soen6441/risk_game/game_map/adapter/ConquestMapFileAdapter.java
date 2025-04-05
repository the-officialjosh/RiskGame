package org.soen6441.risk_game.game_map.adapter;

import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.model.GameMap;

/**
 * Adapter class for Conquest map format.
 * Implements the MapFileHandler interface to unify with other formats.
 */
public class ConquestMapFileAdapter implements MapFileHandler {

    private final ConquestMapReader conquestMapReader;
    private final ConquestMapWriter conquestMapWriter;

    /**
     * Constructor initializing reader and writer.
     */
    public ConquestMapFileAdapter() {
        this.conquestMapReader = new ConquestMapReader();
        this.conquestMapWriter = new ConquestMapWriter();
    }

    /**
     * Loads a conquest map.
     *
     * @param gameSession The game session
     * @param fileName    The map file name
     */
    @Override
    public void loadMap(GameSession gameSession, String fileName) {
        conquestMapReader.readMap(gameSession, fileName);
    }

    /**
     * Saves a conquest map.
     *
     * @param gameMap  The game map
     * @param fileName The map file name
     */
    @Override
    public void saveMap(GameMap gameMap, String fileName) {
        conquestMapWriter.writeMap(gameMap, fileName);
    }
}