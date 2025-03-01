package org.soen6441.risk_game.game_map.controller;

import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.model.Continent;
import org.soen6441.risk_game.game_map.model.Country;
import org.soen6441.risk_game.game_map.model.GameMap;
import org.soen6441.risk_game.game_map.view.DisplayToUser;
import org.soen6441.risk_game.player_management.model.Player;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * GameMap controller class which is responsible for handling all
 * the map related logics.
 */
public class GameMapController {
    private DisplayToUser displayToUser = new DisplayToUser();
    /**
     * Handles the load map step
     * @param p_gameSession The game session.
     */
    public void loadMap(GameSession p_gameSession) {
        String filePath = "/home/kawshik/Documents/JavaProjectsVs/RiskGame/src/main/java/org/soen6441/risk_game/game_map/Maps/bigeurope.map"; // Update with actual path
        GameMap gameMap = new GameMap(new ArrayList<>());

         try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean readingContinents = false;
            boolean readingCountries = false;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith(";")) {
                    continue;
                }
                if (line.equals("[continents]")) {
                    readingContinents = true;
                    readingCountries = false;
                    continue;
                }
                if (line.equals("[countries]")) {
                    readingContinents = false;
                    readingCountries = true;
                    continue;
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
                    String continentName = parts[2];
                    Country country = new Country(countryId, countryName, new ArrayList<>(), new HashMap<Player, Integer>());
                    for (Continent continent : gameMap.getContinents()) {
                        if (continent.getName().equals(continentName)) {
                            continent.getCountries().add(country);
                            break;
                        }
                    }
                }
            }

            p_gameSession.setMap(gameMap);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the assign countries step.
     * @param p_gameSession The game session.
     */
    public void assignCountries(GameSession p_gameSession) {
        List<Player> d_players = p_gameSession.getPlayers();
        List<Continent> d_continents = p_gameSession.getMap().getContinents();
        List<Country> d_countries = new ArrayList<Country>();
        Map<Country, Player> d_assignedSigned = new HashMap<>();

        for (int i = 0; i < d_continents.size(); i++) {
            for (int j = 0; j < d_continents.get(i).getCountries().size(); j++) {
                d_countries.add(d_continents.get(i).getCountries().get(i));
            }
        }

        for (int i = 0; i < d_countries.size(); i++) {
            Random rn = new Random();
            int randomNumber = rn.nextInt(d_countries.size());

            while (true) {
                if (d_countries.get(randomNumber).getD_ownedBy().isEmpty()) {
                    d_countries.get(randomNumber).setD_ownedBy(d_players.get(i).getName());
                    d_players.get(i).setD_countries_owned(d_countries.get(randomNumber));
                    d_assignedSigned.put(d_countries.get(randomNumber), d_players.get(i));
                    break;
                }
                randomNumber = rn.nextInt(d_countries.size());
            }
        }
        p_gameSession.setCountriesControllers(d_assignedSigned);
    }

    /**
     * Handles the assignReinforcements step.
     * @param p_gameSession The game session.
     */
    public void assignReinforcements(GameSession p_gameSession) {
        // Implement logic to assign reinforcements
    }

    /**
     * Adds a continent to the map.
     * @param gameMap The game map.
     * @param continentName The name of the continent.
     * @param continentValue The value of the continent.
     */
    public void addContinent(GameMap gameMap, String continentName, int continentValue) {
        Continent continent = new Continent(continentName, new ArrayList<>(), continentValue);
        gameMap.getContinents().add(continent);
        displayToUser.displayMessage("Continent " + continentName + " added.");
    }

    /**
     * Removes a continent from the map.
     * @param gameMap The game map.
     * @param continentName The name of the continent.
     */
    public void removeContinent(GameMap gameMap, String continentName) {
        gameMap.getContinents().removeIf(continent -> continent.getName().equals(continentName));
        displayToUser.displayMessage("Continent " + continentName + " removed.");
    }

    /**
     * Adds a country to the map.
     * @param gameMap The game map.
     * @param countryName The name of the country.
     * @param continentName The name of the continent.
     */
    public void addCountry(GameMap gameMap, String countryName, String continentName) {
        for (Continent continent : gameMap.getContinents()) {
            if (continent.getName().equals(continentName)) {
                Country country = new Country(gameMap.getCountries().size() + 1, countryName, new ArrayList<Country>(), new HashMap<Player, Integer>());
                continent.getCountries().add(country);
                displayToUser.displayMessage("Country " + countryName + " added to continent " + continentName + ".");
                break;
            }
        }
    }

    /**
     * Removes a country from the map.
     * @param gameMap The game map.
     * @param countryName The name of the country.
     */
    public void removeCountry(GameMap gameMap, String countryName) {
        for (Continent continent : gameMap.getContinents()) {
            continent.getCountries().removeIf(country -> country.getName().equals(countryName));
        }
        displayToUser.displayMessage("Country " + countryName + " removed.");
    }

    /**
     * Adds a neighbor to a country.
     * @param gameMap The game map.
     * @param countryName The name of the country.
     * @param neighborName The name of the neighbor country.
     */
    public void addNeighbor(GameMap gameMap, String countryName, String neighborName) {
        Country country = null;
        Country neighbor = null;
        for (Continent continent : gameMap.getContinents()) {
            for (Country c : continent.getCountries()) {
                if (c.getName().equals(countryName)) {
                    country = c;
                }
                if (c.getName().equals(neighborName)) {
                    neighbor = c;
                }
            }
        }
        if (country != null && neighbor != null) {
            country.getAdjacentCountries().add(neighbor);
            displayToUser.displayMessage("Neighbor " + neighborName + " added to country " + countryName + ".");
        }
    }

    /**
     * Removes a neighbor from a country.
     * @param gameMap The game map.
     * @param countryName The name of the country.
     * @param neighborName The name of the neighbor country.
     */
    public void removeNeighbor(GameMap gameMap, String countryName, String neighborName) {
        for (Continent continent : gameMap.getContinents()) {
            for (Country country : continent.getCountries()) {
                if (country.getName().equals(countryName)) {
                    country.getAdjacentCountries().removeIf(neighbor -> neighbor.getName().equals(neighborName));
                    displayToUser.displayMessage("Neighbor " + neighborName + " removed from country " + countryName + ".");
                }
            }
        }
    }

    /**
     * Displays the map as text.
     * @param gameMap The game map.
     */
    public void showMap(GameMap gameMap) {
        for (Continent continent : gameMap.getContinents()) {
            System.out.println("Continent: " + continent.getName());
            for (Country country : continent.getCountries()) {
                System.out.println("  Country: " + country.getName());
                System.out.print("    Neighbors: ");
                for (Country neighbor : country.getAdjacentCountries()) {
                    System.out.print(neighbor.getName() + " ");
                }
                System.out.println();
            }
        }
    }

    /**
     * Saves the map to a file.
     * @param gameMap The game map.
     * @param filename The name of the file.
     */
    public void saveMap(GameMap gameMap, String filename) {
        // Implement logic to save the map to a file
    }

    /**
     * Validates the map.
     * @param gameMap The game map.
     */
    public void validateMap(GameMap gameMap) {
        // Implement logic to validate the map
    }

    /**
     * Handles the map editor commands.
     * @param command The command to be executed.
     * @param gameMap The game map.
     */
    public void handleCommand(String command, GameMap gameMap) {
        String[] parts = command.split(" ");
        String cmd = parts[0];

        switch (cmd) {
            case "editcontinent":
                if (parts[1].equals("-add")) {
                    addContinent(gameMap, parts[2], Integer.parseInt(parts[3]));
                } else if (parts[1].equals("-remove")) {
                    removeContinent(gameMap, parts[2]);
                }
                break;
            case "editcountry":
                if (parts[1].equals("-add")) {
                    addCountry(gameMap, parts[2], parts[3]);
                } else if (parts[1].equals("-remove")) {
                    removeCountry(gameMap, parts[2]);
                }
                break;
            case "editneighbor":
                if (parts[1].equals("-add")) {
                    addNeighbor(gameMap, parts[2], parts[3]);
                } else if (parts[1].equals("-remove")) {
                    removeNeighbor(gameMap, parts[2], parts[3]);
                }
                break;
            case "showmap":
                showMap(gameMap);
                break;
            case "savemap":
                saveMap(gameMap, parts[1]);
                break;
            case "validatemap":
                validateMap(gameMap);
                break;
            default:
                System.out.println("Invalid command");
        }
    }
}
