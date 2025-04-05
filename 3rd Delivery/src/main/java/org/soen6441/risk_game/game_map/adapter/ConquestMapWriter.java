package org.soen6441.risk_game.game_map.adapter;

import org.soen6441.risk_game.game_map.model.Continent;
import org.soen6441.risk_game.game_map.model.Country;
import org.soen6441.risk_game.game_map.model.GameMap;
import org.soen6441.risk_game.game_map.view.DisplayToUser;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Writes maps in Conquest format.
 */
public class ConquestMapWriter {

    private final String mapFolderPath = "maps/";
    private final DisplayToUser displayToUser = new DisplayToUser();

    /**
     * Writes the game map in conquest format to a file.
     *
     * @param gameMap  The game map
     * @param fileName The map file name
     */
    public void writeMap(GameMap gameMap, String fileName) {
        gameMap.setD_name(fileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(mapFolderPath + gameMap.getD_name()))) {
            // Write continents
            writer.write("[Continents]");
            writer.newLine();
            for (Continent continent : gameMap.getContinents()) {
                writer.write(continent.getName() + "=" + continent.getControlValue());
                writer.newLine();
            }

            // Write territories
            writer.write("[Territories]");
            writer.newLine();
            for (Country country : gameMap.getCountries()) {
                // Write: countryName, x, y, continentName, neighbour1, neighbour2, ...
                StringBuilder line = new StringBuilder();
                line.append(country.getName()).append(",0,0,"); // Position is 0,0 (optional in conquest format)

                // Add continent name
                String continentName = gameMap.getContinents().stream()
                        .filter(continent -> continent.getCountries().contains(country))
                        .map(Continent::getName)
                        .findFirst()
                        .orElse("Unknown");

                line.append(continentName);

                // Add neighbours
                for (Country neighbor : country.getAdjacentCountries()) {
                    line.append(",").append(neighbor.getName());
                }

                writer.write(line.toString());
                writer.newLine();
            }

            displayToUser.instructionMessage("The Map \"" + gameMap.getD_name() + "\" was saved successfully (Conquest format).");

        } catch (IOException e) {
            throw new RuntimeException("Error saving map: " + e.getMessage());
        }
    }
}