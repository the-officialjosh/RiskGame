package org.soen6441.risk_game.game_map.adapter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Detects the map format (Domination or Conquest).
 *
 * @author Joshua Onyema
 * @version 1.1
 */
public class MapFormatDetector {

    private static final String MAP_FOLDER_PATH = "maps/";

    /**
     * Detects the format of a map file.
     *
     * @param fileName the map file name
     * @return "domination", "conquest", or "unknown"
     */
    public static String detectFormat(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(MAP_FOLDER_PATH + fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim().toLowerCase();
                if (line.startsWith("[territories]")) {
                    return "conquest";
                }
                if (line.startsWith("[borders]")) {
                    return "domination";
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading the map file for format detection: " + e.getMessage());
        }
        return "unknown";
    }
}