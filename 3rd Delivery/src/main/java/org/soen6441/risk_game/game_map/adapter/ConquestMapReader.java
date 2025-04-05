package org.soen6441.risk_game.game_map.adapter;

import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.model.Continent;
import org.soen6441.risk_game.game_map.model.Country;
import org.soen6441.risk_game.game_map.model.GameMap;
import org.soen6441.risk_game.game_map.view.DisplayToUser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads maps in Conquest format.
 */
public class ConquestMapReader {

    private final String mapFolderPath = "maps/";
    private final DisplayToUser displayToUser = new DisplayToUser();

    /**
     * Reads a conquest format map and loads it into the game session.
     *
     * @param gameSession The game session
     * @param fileName    The map file name
     */
    public void readMap(GameSession gameSession, String fileName) {
        GameMap gameMap = new GameMap(fileName, new ArrayList<>());
        Continent.continentIdCounter = 1;

        try (BufferedReader reader = new BufferedReader(new FileReader(mapFolderPath + fileName))) {
            String line;
            boolean readingContinents = false;
            boolean readingTerritories = false;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty() || line.startsWith(";")) continue;

                if (line.startsWith("[Continents]")) {
                    readingContinents = true;
                    readingTerritories = false;
                    continue;
                } else if (line.startsWith("[Territories]")) {
                    readingContinents = false;
                    readingTerritories = true;
                    continue;
                }

                if (readingContinents) {
                    String[] parts = line.split("=");
                    String continentName = parts[0].trim();
                    int continentValue = Integer.parseInt(parts[1].trim());
                    Continent continent = new Continent(continentName, new ArrayList<>(), continentValue);
                    gameMap.getContinents().add(continent);
                }

                if (readingTerritories) {
                    String[] parts = line.split(",");
                    String countryName = parts[0].trim();
                    String continentName = parts[3].trim();

                    // Assign country to continent
                    Continent continent = gameMap.getContinents().stream()
                            .filter(c -> c.getName().equalsIgnoreCase(continentName))
                            .findFirst()
                            .orElse(null);

                    if (continent != null) {
                        Country country = new Country(gameMap.getCountries().size() + 1, countryName, new ArrayList<>(), 0);
                        continent.getCountries().add(country);
                    }
                }
            }

            // Now assign neighbours
            try (BufferedReader neighbourReader = new BufferedReader(new FileReader(mapFolderPath + fileName))) {
                readingTerritories = false;
                while ((line = neighbourReader.readLine()) != null) {
                    line = line.trim();

                    if (line.startsWith("[Territories]")) {
                        readingTerritories = true;
                        continue;
                    }

                    if (readingTerritories && !line.isEmpty() && !line.startsWith(";")) {
                        String[] parts = line.split(",");
                        String countryName = parts[0].trim();

                        Country country = gameMap.getCountries().stream()
                                .filter(c -> c.getName().equalsIgnoreCase(countryName))
                                .findFirst()
                                .orElse(null);

                        if (country != null) {
                            for (int i = 4; i < parts.length; i++) {
                                String neighborName = parts[i].trim();
                                Country neighborCountry = gameMap.getCountries().stream()
                                        .filter(c -> c.getName().equalsIgnoreCase(neighborName))
                                        .findFirst()
                                        .orElse(null);

                                if (neighborCountry != null && !country.getAdjacentCountries().contains(neighborCountry)) {
                                    country.addNeighbor(neighborCountry);
                                }
                            }
                        }
                    }
                }
            }

            gameSession.setMap(gameMap);
            displayToUser.instructionMessage("The Map \"" + fileName + "\" has been loaded successfully (Conquest format).");

        } catch (IOException e) {
            gameSession.setMap(gameMap);
            displayToUser.instructionMessage("The Map \"" + fileName + "\" was not found. Loaded an empty map instead.");
        }
    }
}