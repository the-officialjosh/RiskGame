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

import java.io.Serializable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * GameMapController class is responsible for handling all map-related logic.
 * Includes map loading, editing, validation, and game phase interactions.
 *
 * @author Joshua Onyema
 * @version 2.0 (Refactored)
 */
public class GameMapController implements Serializable {

    private final DisplayToUser d_displayToUser = new DisplayToUser();


    /**
     * Handles the assign countries step.
     *
     * @param gameSession The game session.
     */
    public void assignCountries(GameSession gameSession) {
        List<Player> players = gameSession.getPlayers();
        List<Country> countries = gameSession.getMap().getCountries();

        if (countries.size() < players.size()) {
            System.out.println("=== Player Adjustment Required ===");
            System.out.println("The number of players exceeds the available countries. Adjusting accordingly...");
            for (int i = players.size(); i > countries.size(); i--) {
                Player removedPlayer = players.remove(i - 1);
                System.out.println("--- Player " + removedPlayer.getName() + " has been removed due to imbalance.");
            }
            System.out.println("✔ Adjustment complete.");
        }

        Random random = new Random();
        for (int i = 0, j = 0; i < countries.size(); i++, j++) {
            if (j == players.size()) {
                j = 0;
            }

            int randomIndex = random.nextInt(countries.size());
            while (countries.get(randomIndex).getD_ownedBy() != null) {
                randomIndex = random.nextInt(countries.size());
            }

            Country selectedCountry = countries.get(randomIndex);
            Player assignedPlayer = players.get(j);

            selectedCountry.setD_ownedBy(assignedPlayer);
            assignedPlayer.setD_countries_owned(selectedCountry);
        }

        System.out.println("=== Territory Assignment Complete ===");
        LogEntryBuffer.getInstance().setValue("Countries assigned to players.");
    }
    /**
     * Assigns reinforcements to players.
     *
     * @param gameSession The game session.
     */
    public void assignReinforcements(GameSession gameSession) {
        for (Player player : gameSession.getPlayers()) {
            player.reinforcement(4);
        }
        LogEntryBuffer.getInstance().setValue("Reinforcements assigned to players.");
    }

    /**
     * Adds a continent to the map.
     */
    public void addContinent(GameMap gameMap, String continentName, int continentValue) {
        Continent continent = new Continent(continentName, new ArrayList<>(), continentValue);
        gameMap.getContinents().add(continent);
        d_displayToUser.instructionMessage("Continent " + continentName + " added.");
    }

    /**
     * Removes a continent from the map.
     */
    public void removeContinent(GameMap gameMap, String continentName) {
        gameMap.getContinents().removeIf(continent -> continent.getName().equals(continentName));
        Continent.continentIdCounter--;
        d_displayToUser.instructionMessage("Continent " + continentName + " removed.");
    }

    /**
     * Adds a country to a continent.
     */
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

    /**
     * Removes a country from the map.
     */
    public void removeCountry(GameMap gameMap, String countryName) {
        for (Continent continent : gameMap.getContinents()) {
            continent.getCountries().removeIf(country -> country.getName().equals(countryName));
        }
        d_displayToUser.instructionMessage("Country " + countryName + " removed.");
    }

    /**
     * Adds a neighbour to a country.
     */
    public void addNeighbor(GameMap gameMap, String countryName, String neighborName) {
        Country country = findCountry(gameMap, countryName);
        Country neighbor = findCountry(gameMap, neighborName);

        if (country == null || neighbor == null) {
            d_displayToUser.instructionMessage("Error: One or both countries do not exist.");
            return;
        }
        if (country.getAdjacentCountries().contains(neighbor)) {
            d_displayToUser.instructionMessage("Neighbor " + neighborName + " already exists for " + countryName + ".");
            return;
        }
        country.getAdjacentCountries().add(neighbor);
        neighbor.getAdjacentCountries().add(country);
        d_displayToUser.instructionMessage("Neighbor " + neighborName + " added to country " + countryName + ".");
    }

    /**
     * Removes a neighbour from a country.
     */
    public void removeNeighbor(GameMap gameMap, String countryName, String neighborName) {
        Country country = findCountry(gameMap, countryName);
        Country neighbor = findCountry(gameMap, neighborName);

        if (country == null || neighbor == null) {
            d_displayToUser.instructionMessage("Error: One or both countries do not exist.");
            return;
        }
        if (!country.getAdjacentCountries().contains(neighbor)) {
            d_displayToUser.instructionMessage("Neighbor " + neighborName + " is not a neighbor of " + countryName + ".");
            return;
        }
        country.getAdjacentCountries().remove(neighbor);
        neighbor.getAdjacentCountries().remove(country);
        d_displayToUser.instructionMessage("Neighbor " + neighborName + " removed from country " + countryName + ".");
    }

    private Country findCountry(GameMap gameMap, String countryName) {
        return gameMap.getCountries().stream()
                .filter(country -> country.getName().equals(countryName))
                .findFirst()
                .orElse(null);
    }

    /**
     * Displays the map in a readable format.
     */
    public void showMap(GameMap p_gameMap) {
        if (p_gameMap == null) {
            d_displayToUser.instructionMessage("Can not run \"showMap\" since not map was loaded.");
            return;
        }
        System.out.println("==============================================================================");
        System.out.println("                                  GAME MAP OVERVIEW                           ");
        System.out.println("==============================================================================");
        for (Continent l_continent : p_gameMap.getContinents()) {
            System.out.println("                               Continent: " + l_continent.getName());
            System.out.println("------------------------------------------------------------------------------");
            for (Country l_country : l_continent.getCountries()) {
                System.out.println("Country: " + l_country.getName() + " (ID: " + l_country.getCountryId() + ")");
                System.out.println(">>>> Owned by: " + (l_country.getD_ownedBy() == null ? "Unclaimed Territory" : l_country.getD_ownedBy().getName()));

                System.out.println(">>>> Armies:");
                if (l_country.getD_ownedBy() != null)
                    System.out.println(" - " + l_country.getExistingArmies() + " units under the command of " + l_country.getD_ownedBy().getName());

                System.out.print(">>>> Neighbors: ");
                for (Country neighbor : l_country.getAdjacentCountries()) {
                    System.out.print(neighbor.getName() + " ");
                }
                System.out.println("\n------------------------------------------------------------------------------");
            }
        }
        System.out.println("==============================================================================");
        System.out.println("                                  END OF MAP OVERVIEW                         ");
        System.out.println("==============================================================================");
    }

    /**
     * Validates the current map.
     */
    public boolean validateMap(GameMap gameMap) {
        int continents = gameMap.getContinents().size();
        int countries = gameMap.getCountries().size();
        int borders = gameMap.getCountries().stream().mapToInt(c -> c.getAdjacentCountries().size()).sum() / 2;

        if (continents < 3) {
            d_displayToUser.instructionMessage("Invalid map: at least 3 continents required. Found: " + continents);
            return false;
        }
        if (countries < 5) {
            d_displayToUser.instructionMessage("Invalid map: at least 5 countries required. Found: " + countries);
            return false;
        }
        if (borders < 5) {
            d_displayToUser.instructionMessage("Invalid map: at least 5 borders required. Found: " + borders);
            return false;
        }

        Set<Country> connected = new HashSet<>();
        explore(gameMap.getCountries().getFirst(), connected);

        if (connected.size() != countries) {
            d_displayToUser.instructionMessage("Invalid map: countries are not fully connected.");
            return false;
        }

        return true;
    }

    private void explore(Country country, Set<Country> visited) {
        visited.add(country);
        for (Country neighbor : country.getAdjacentCountries()) {
            if (!visited.contains(neighbor)) {
                explore(neighbor, visited);
            }
        }
    }

    /**
     * Handles map management step using user input.
     */
    public void handleMapManagementStep(GameSession gameSession) {
        Scanner scanner = UserInputScanner.getInstance().getScanner();
        String command;

        d_displayToUser.instructionMessage("Use the following commands to manage the game map:");
        d_displayToUser.instructionMessage(String.format("%-20s %-20s %-20s %-20s", "⛁ loadmap", "✎ editmap", "⎙ savemap", "⚲ showmap"));
        d_displayToUser.instructionMessage(String.format("%-20s %-20s %-20s %-20s", "✔ validatemap", "✎ editcontinent", "✎ editcountry", "✎ editneighbor"));
        d_displayToUser.instructionMessage("-----------------------------------------");
        d_displayToUser.instructionMessage("\uD83D\uDCA1 Please note that you can run the instruction: 'tournament' to go into the tournament mode.\n");


        boolean active = true;
        while (active) {
            command = scanner.nextLine();
            LogEntryBuffer.getInstance().setValue(command);

            handleCommand(command, gameSession);

            if (command.startsWith("mapeditordone")) {
                if (validateMap(gameSession.getMap())) {
                    d_displayToUser.instructionMessage("✔ The map is valid and will be used for the game.");
                    active = false;
                } else {
                    d_displayToUser.instructionMessage("⚠ The map is invalid. Please fix it before proceeding.");
                }
            }
        }

        d_displayToUser.instructionMessage("=====================================");
        d_displayToUser.instructionMessage("    MAP MANAGEMENT STEP COMPLETED    ");
        d_displayToUser.instructionMessage("=====================================");
    }

    /**
     * Handles game map commands.
     */
    public void handleCommand(String command, GameSession gameSession) {
        Pattern pattern = Pattern.compile("(\\w+\\s+-\\w+\\s+[^\\s]+\\s+\\d+|\\w+\\s+-\\w+\\s+[^\\s]+\\s+[^\\s]+|\\w+\\s+-\\w+\\s+[^\\s]+|\\w+\\s+[^\\s]+|\\w+)");
        Matcher matcher = pattern.matcher(command);

        while (matcher.find()) {
            String cmd = matcher.group(1).trim();
            String[] parts = cmd.split(" ");
            String action = parts[0];

            try {
                switch (action) {
                    case "loadmap", "editmap" -> {
                        if (parts.length < 2) {
                            d_displayToUser.instructionMessage("Error: Map name is missing.");
                            break;
                        }
                        String mapName = parts[1];
                        MapFileHandler handler = getMapHandler(mapName);
                        handler.loadMap(gameSession, mapName);
                    }
                    case "showmap" -> showMap(gameSession.getMap());
                    case "validatemap" -> validateMap(gameSession.getMap());
                    case "mapeditordone" -> d_displayToUser.instructionMessage("Map editing session ended.");
                    case "tournament" -> {
                        GameEngine.startTournamentModeGame(command);
                        System.exit(0);
                    }
                    default -> d_displayToUser.instructionMessage("Error: Unknown command '" + action + "'.");
                }
            } catch (Exception e) {
                d_displayToUser.instructionMessage("Error: " + e.getMessage());
            }
        }
    }

    /**
     * Returns the appropriate map handler based on file format.
     */
    private MapFileHandler getMapHandler(String fileName) {
        String format = MapFormatDetector.detectFormat(fileName);
        return format.equals("conquest") ? new ConquestMapFileAdapter() : new DominationMapFileHandler();
    }
}