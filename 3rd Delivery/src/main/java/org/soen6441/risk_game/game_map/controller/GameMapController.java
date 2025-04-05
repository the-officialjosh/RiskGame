package org.soen6441.risk_game.game_map.controller;

import org.soen6441.risk_game.game_engine.controller.GameEngine;
import org.soen6441.risk_game.game_engine.controller.user_input.UserInputScanner;
import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.adapter.ConquestMapFileAdapter;
import org.soen6441.risk_game.game_map.adapter.DominationMapFileHandler;
import org.soen6441.risk_game.game_map.adapter.MapFileHandler;
import org.soen6441.risk_game.game_map.adapter.MapFormatDetector;
import org.soen6441.risk_game.game_map.model.Continent;
import org.soen6441.risk_game.game_map.model.Country;
import org.soen6441.risk_game.game_map.model.GameMap;
import org.soen6441.risk_game.game_map.view.DisplayToUser;
import org.soen6441.risk_game.monitoring.LogEntryBuffer;
import org.soen6441.risk_game.player_management.model.Player;

import java.io.File;
import java.io.Serializable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * GameMap controller class which is responsible for handling all map-related logic.
 *
 * Refactored to use MapFileHandler interface for flexible format handling.
 *
 * @author Ahmed Fakhir
 * @author Irfan Maknojia
 * @author Joshua Onyema
 * @author Kawshik Kumar Ghosh
 * @version 1.0
 */
public class GameMapController implements Serializable {

    private final DisplayToUser d_displayToUser = new DisplayToUser();
    private static final String MAP_FOLDER_PATH = "maps/";

    private MapFileHandler getMapFileHandler(String fileName) {
        String format = MapFormatDetector.detectFormat(fileName);
        return switch (format) {
            case "conquest" -> new ConquestMapFileAdapter();
            default -> new DominationMapFileHandler();
        };
    }

    public void loadMap(GameSession gameSession, String mapFileName) {
        MapFileHandler mapFileHandler = getMapFileHandler(mapFileName);
        mapFileHandler.loadMap(gameSession, mapFileName);

        if (validateMap(gameSession.getMap())) {
            d_displayToUser.instructionMessage("\nThe Map \"" + mapFileName + "\" is valid and has been loaded into the game.");
        } else {
            d_displayToUser.instructionMessage("The Map \"" + mapFileName + "\" has been loaded into the game but is not valid.");
        }
    }

    public void saveMap(GameMap gameMap, String mapFileName) {
        if (!validateMap(gameMap)) {
            d_displayToUser.instructionMessage("The Map \"" + mapFileName + "\" cannot be saved because it is not valid.");
            return;
        }

        MapFileHandler mapFileHandler = getMapFileHandler(mapFileName);
        mapFileHandler.saveMap(gameMap, mapFileName);
    }

    public void assignCountries(GameSession gameSession) {
        List<Player> players = gameSession.getPlayers();
        List<Country> countries = gameSession.getMap().getCountries();

        if (countries.size() < players.size()) {
            System.out.println("=== Player Adjustment Required ===");
            System.out.println("The number of players exceeds the available countries. Adjusting accordingly...");
            for (int i = players.size(); i > countries.size(); i--) {
                String message = "--- Player " + players.get(i - 1).getName() + " has been removed due to an imbalance.";
                System.out.println(message);
                players.remove(i - 1);
            }
            System.out.println("✔ Adjustment complete.");
        }

        Random random = new Random();
        for (int i = 0, j = 0; i < countries.size(); i++, j++) {
            if (j == players.size()) j = 0;

            int randomIndex;
            while (true) {
                randomIndex = random.nextInt(countries.size());
                if (countries.get(randomIndex).getD_ownedBy() == null) {
                    countries.get(randomIndex).setD_ownedBy(players.get(j));
                    players.get(j).setD_countries_owned(countries.get(randomIndex));
                    break;
                }
            }
        }

        System.out.println("=== Territory Assignment Complete ===");
        System.out.println("Countries have been strategically assigned to all players.");

        LogEntryBuffer.getInstance().setValue("Countries are successfully assigned to the players.");
    }

    public void assignReinforcements(GameSession gameSession) {
        for (Player player : gameSession.getPlayers()) {
            player.reinforcement(3);
        }
        LogEntryBuffer.getInstance().setValue("Reinforcements assigned to players.");
    }

    public void addContinent(GameMap gameMap, String continentName, int continentValue) {
        Continent continent = new Continent(continentName, new ArrayList<>(), continentValue);
        gameMap.getContinents().add(continent);
        d_displayToUser.instructionMessage("Continent " + continentName + " added.");
    }

    public void removeContinent(GameMap gameMap, String continentName) {
        gameMap.getContinents().removeIf(continent -> continent.getName().equals(continentName));
        Continent.continentIdCounter--;
        d_displayToUser.instructionMessage("Continent " + continentName + " removed.");
    }

    public void addCountry(GameMap gameMap, String countryName, String continentName) {
        for (Continent continent : gameMap.getContinents()) {
            if (continent.getName().equals(continentName)) {
                Country country = new Country(gameMap.getCountries().size() + 1, countryName, new ArrayList<>(), 0);
                continent.getCountries().add(country);
                d_displayToUser.instructionMessage("Country " + countryName + " added to continent " + continentName + ".");
                return;
            }
        }
        d_displayToUser.instructionMessage("Error: Continent " + continentName + " does not exist.");
    }

    public void removeCountry(GameMap gameMap, String countryName) {
        for (Continent continent : gameMap.getContinents()) {
            continent.getCountries().removeIf(country -> country.getName().equals(countryName));
        }
        d_displayToUser.instructionMessage("Country " + countryName + " removed.");
    }

    public void addNeighbor(GameMap gameMap, String countryName, String neighborName) {
        Country country = findCountry(gameMap, countryName);
        Country neighbor = findCountry(gameMap, neighborName);

        if (country == null || neighbor == null) {
            d_displayToUser.instructionMessage("Error: One of the countries does not exist.");
            return;
        }

        if (country.getAdjacentCountries().contains(neighbor)) {
            d_displayToUser.instructionMessage("Neighbor already exists.");
            return;
        }

        country.getAdjacentCountries().add(neighbor);
        neighbor.getAdjacentCountries().add(country);
        d_displayToUser.instructionMessage("Neighbor " + neighborName + " added to country " + countryName + ".");
    }

    public void removeNeighbor(GameMap gameMap, String countryName, String neighborName) {
        Country country = findCountry(gameMap, countryName);
        Country neighbor = findCountry(gameMap, neighborName);

        if (country == null || neighbor == null) {
            d_displayToUser.instructionMessage("Error: One of the countries does not exist.");
            return;
        }

        if (!country.getAdjacentCountries().contains(neighbor)) {
            d_displayToUser.instructionMessage("Error: They are not neighbors.");
            return;
        }

        country.getAdjacentCountries().remove(neighbor);
        neighbor.getAdjacentCountries().remove(country);
        d_displayToUser.instructionMessage("Neighbor " + neighborName + " removed from country " + countryName + ".");
    }

    public void showMap(GameMap gameMap) {
        if (gameMap == null) {
            d_displayToUser.instructionMessage("No map loaded.");
            return;
        }

        System.out.println("==============================================================================");
        System.out.println("                                  GAME MAP OVERVIEW                           ");
        System.out.println("==============================================================================");
        for (Continent continent : gameMap.getContinents()) {
            System.out.println("Continent: " + continent.getName());
            System.out.println("------------------------------------------------------------------------------");
            for (Country country : continent.getCountries()) {
                System.out.println("Country: " + country.getName() + " (ID: " + country.getCountryId() + ")");
                System.out.println(">>>> Owned by: " + (country.getD_ownedBy() == null ? "Unclaimed" : country.getD_ownedBy().getName()));
                System.out.print(">>>> Neighbors: ");
                for (Country neighbor : country.getAdjacentCountries()) {
                    System.out.print(neighbor.getName() + " ");
                }
                System.out.println("\n------------------------------------------------------------------------------");
            }
        }
        System.out.println("==============================================================================");
        System.out.println("                                  END OF MAP OVERVIEW                         ");
        System.out.println("==============================================================================");
    }

    public boolean validateMap(GameMap gameMap) {
        int continentsNumber = Continent.continentIdCounter - 1;
        int countriesNumber = 0;
        int bordersNumber = 0;

        for (Continent continent : gameMap.getContinents()) {
            for (Country country : continent.getCountries()) {
                countriesNumber++;
                bordersNumber += country.getAdjacentCountries().size();
            }
        }

        bordersNumber /= 2;

        if (continentsNumber < 3) {
            d_displayToUser.instructionMessage("Invalid map: at least 3 continents required. Found: " + continentsNumber);
            return false;
        }

        if (countriesNumber < 5) {
            d_displayToUser.instructionMessage("Invalid map: at least 5 countries required. Found: " + countriesNumber);
            return false;
        }

        if (bordersNumber < 5) {
            d_displayToUser.instructionMessage("Invalid map: at least 5 borders required. Found: " + bordersNumber);
            return false;
        }

        HashSet<Country> connectedCountries = new HashSet<>();
        if (!gameMap.getContinents().isEmpty() && !gameMap.getContinents().getFirst().getCountries().isEmpty()) {
            connectionVerification(gameMap.getContinents().getFirst().getCountries().getFirst(), connectedCountries);
        }

        boolean countriesAreConnected = countriesNumber == connectedCountries.size();
        if (!countriesAreConnected) {
            d_displayToUser.instructionMessage("Invalid map: countries are not fully connected.");
        }

        return countriesAreConnected;
    }

    private void connectionVerification(Country country, Set<Country> connectedCountries) {
        connectedCountries.add(country);
        for (Country neighbor : country.getAdjacentCountries()) {
            if (!connectedCountries.contains(neighbor)) {
                connectionVerification(neighbor, connectedCountries);
            }
        }
    }

    private Country findCountry(GameMap gameMap, String countryName) {
        for (Continent continent : gameMap.getContinents()) {
            for (Country country : continent.getCountries()) {
                if (country.getName().equalsIgnoreCase(countryName)) {
                    return country;
                }
            }
        }
        return null;
    }
}