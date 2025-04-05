package org.soen6441.risk_game.game_map.adapter;

import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.model.Continent;
import org.soen6441.risk_game.game_map.model.Country;
import org.soen6441.risk_game.game_map.model.GameMap;
import org.soen6441.risk_game.game_map.view.DisplayToUser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles loading and saving maps in Domination format.
 */
public class DominationMapFileHandler implements MapFileHandler {

    private final String mapFolderPath = "maps/";
    private final DisplayToUser displayToUser = new DisplayToUser();

    @Override
    public void loadMap(GameSession gameSession, String fileName) {
        GameMap gameMap = new GameMap(fileName, new ArrayList<>());
        Continent.continentIdCounter = 1;

        try (BufferedReader reader = new BufferedReader(new FileReader(mapFolderPath + fileName))) {
            String line;
            boolean readingContinents = false;
            boolean readingCountries = false;
            boolean readingBorders = false;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith(";")) {
                    if (readingBorders) break;
                    continue;
                }

                switch (line) {
                    case "[continents]" -> {
                        readingContinents = true;
                        readingCountries = false;
                        readingBorders = false;
                        continue;
                    }
                    case "[countries]" -> {
                        readingContinents = false;
                        readingCountries = true;
                        readingBorders = false;
                        continue;
                    }
                    case "[borders]" -> {
                        readingContinents = false;
                        readingCountries = false;
                        readingBorders = true;
                        continue;
                    }
                }

                if (readingContinents) {
                    String[] parts = line.split(" ");
                    String continentName = parts[0];
                    int continentValue = Integer.parseInt(parts[1]);
                    Continent continent = new Continent(continentName, new ArrayList<>(), continentValue);
                    gameMap.getContinents().add(continent);
                }

                if (readingCountries) {
                    String[] parts = line.split(" ");
                    int countryId = Integer.parseInt(parts[0]);
                    String countryName = parts[1];
                    int continentId = Integer.parseInt(parts[2]);
                    Country country = new Country(countryId, countryName, new ArrayList<>(), 0);
                    for (Continent continent : gameMap.getContinents()) {
                        if (continent.getD_continentId() == continentId) {
                            continent.getCountries().add(country);
                            break;
                        }
                    }
                }

                if (readingBorders) {
                    String[] parts = line.split(" ");
                    int countryId = Integer.parseInt(parts[0]);
                    for (Continent continent : gameMap.getContinents()) {
                        for (Country country : continent.getCountries()) {
                            if (country.getCountryId() == countryId) {
                                List<Country> adjacentCountries = new ArrayList<>();
                                for (int i = 1; i < parts.length; i++) {
                                    int adjacentId = Integer.parseInt(parts[i]);
                                    gameMap.getCountries().stream()
                                            .filter(c -> c.getCountryId() == adjacentId)
                                            .findFirst()
                                            .ifPresent(adjacentCountries::add);
                                }
                                country.setAdjacentCountries(adjacentCountries);
                            }
                        }
                    }
                }
            }

            gameSession.setMap(gameMap);
            displayToUser.instructionMessage("The Map \"" + fileName + "\" has been loaded successfully (Domination format).");

        } catch (IOException e) {
            gameSession.setMap(gameMap);
            displayToUser.instructionMessage("The Map \"" + fileName + "\" was not found. Loaded an empty map instead.");
        }
    }

    @Override
    public void saveMap(GameMap gameMap, String fileName) {
        gameMap.setD_name(fileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(mapFolderPath + gameMap.getD_name()))) {
            writer.write("[continents]");
            writer.newLine();
            for (Continent continent : gameMap.getContinents()) {
                writer.write(continent.getName() + " " + continent.getControlValue());
                writer.newLine();
            }

            writer.write("[countries]");
            writer.newLine();
            for (Continent continent : gameMap.getContinents()) {
                for (Country country : continent.getCountries()) {
                    writer.write(country.getCountryId() + " " + country.getName() + " " + continent.getD_continentId());
                    writer.newLine();
                }
            }

            writer.write("[borders]");
            writer.newLine();
            for (Country country : gameMap.getCountries()) {
                writer.write(String.valueOf(country.getCountryId()));
                for (Country neighbor : country.getAdjacentCountries()) {
                    writer.write(" " + neighbor.getCountryId());
                }
                writer.newLine();
            }

            displayToUser.instructionMessage("The Map \"" + gameMap.getD_name() + "\" was saved successfully (Domination format).");

        } catch (IOException e) {
            throw new RuntimeException("Error saving map: " + e.getMessage());
        }
    }
}