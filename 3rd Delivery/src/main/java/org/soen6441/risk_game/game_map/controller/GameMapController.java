package org.soen6441.risk_game.game_map.controller;

import org.soen6441.risk_game.game_engine.controller.GameEngine;
import org.soen6441.risk_game.game_engine.controller.user_input.UserInputScanner;
import org.soen6441.risk_game.game_engine.model.GameSession;
import org.soen6441.risk_game.game_map.model.Continent;
import org.soen6441.risk_game.game_map.model.Country;
import org.soen6441.risk_game.game_map.model.GameMap;
import org.soen6441.risk_game.game_map.view.DisplayToUser;
import org.soen6441.risk_game.monitoring.LogEntryBuffer;
import org.soen6441.risk_game.player_management.model.Player;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;
import java.util.*;


/**
 * GameMap controller class which is responsible for handling all
 * the map related logics.
 *
 * @author Ahmed Fakhir
 * @author Irfan Maknojia
 * @author Joshua Onyema
 * @author Kawshik Kumar Ghosh
 * @version 1.0
 */
public class GameMapController implements Serializable {
    private DisplayToUser d_displayToUser = new DisplayToUser();
    private final String d_mapFolderPath = "maps/";

    /**
     * Handles the load map step
     *
     * @param p_gameSession The game session.
     */
    public void loadMap(GameSession p_gameSession, String p_mapFileName) {
        GameMap d_gameMap = new GameMap(p_mapFileName, new ArrayList<>());
        // Reset Continent Id counter
        Continent.continentIdCounter = 1;

        try (BufferedReader d_br = new BufferedReader(new FileReader(d_mapFolderPath + p_mapFileName))) {
            String d_line;
            boolean d_readingContinents = false;
            boolean d_readingCountries = false;
            boolean d_readingBorders = false;

            while ((d_line = d_br.readLine()) != null) {
                d_line = d_line.trim();
                if (d_line.isEmpty() || d_line.startsWith(";")) {
                    // If we are in the last step, and we are reading the last empty
                    // line, then we can finish the reading process
                    if (d_readingBorders)
                        break;
                    continue;
                }
                switch (d_line) {
                    case "[continents]" -> {
                        d_readingContinents = true;
                        d_readingCountries = false;
                        d_readingBorders = false;
                        continue;
                    }
                    case "[countries]" -> {
                        d_readingContinents = false;
                        d_readingCountries = true;
                        d_readingBorders = false;
                        continue;
                    }
                    case "[borders]" -> {
                        d_readingContinents = false;
                        d_readingCountries = false;
                        d_readingBorders = true;
                        continue;
                    }
                }
                if (d_readingContinents) {
                    String[] d_parts = d_line.split(" ");
                    String d_continentName = d_parts[0];
                    int d_continentValue = Integer.parseInt(d_parts[1]);
                    Continent d_continent = new Continent(d_continentName, new ArrayList<>(), d_continentValue);
                    d_gameMap.getContinents().add(d_continent);
                }
                if (d_readingCountries) {
                    String[] d_parts = d_line.split(" ");
                    int d_countryId = Integer.parseInt(d_parts[0]);
                    String d_countryName = d_parts[1];
                    int d_continentId = Integer.parseInt(d_parts[2]);
                    Country d_country = new Country(d_countryId, d_countryName, new ArrayList<>(), 0);
                    for (Continent d_continent : d_gameMap.getContinents()) {
                        if (d_continent.getD_continentId() == d_continentId) {
                            d_continent.getCountries().add(d_country);
                            break;
                        }
                    }
                }
                if (d_readingBorders) {
                    String[] d_parts = d_line.split(" ");
                    d_gameMap.getContinents().forEach(p_continent ->
                            p_continent.getCountries().forEach(p_country ->
                                    {
                                        if (p_country.getCountryId() == Integer.parseInt(d_parts[0])) {
                                            List<Country> d_adjacentCountries = new ArrayList<>();
                                            // Check for the adjacent countries by using the retrieved ids
                                            d_gameMap.getContinents().forEach(d_oneOfTheContinents ->
                                                    d_oneOfTheContinents.getCountries().forEach(oneOfTheCountries ->
                                                    {
                                                        for (int i = 1; i < d_parts.length; i++) {
                                                            if (Integer.parseInt(d_parts[i]) == oneOfTheCountries.getCountryId())
                                                                d_adjacentCountries.add(oneOfTheCountries);
                                                        }
                                                    })
                                            );
                                            p_country.setAdjacentCountries(d_adjacentCountries);
                                        }
                                    }
                            ));
                }
            }
            p_gameSession.setMap(d_gameMap);
            if (validateMap(d_gameMap)){
                d_displayToUser.instructionMessage("\nThe Map \"" + p_mapFileName + "\" is valid and has been loaded into the game.");
                d_displayToUser.instructionMessage("You can run other map instructions or \"mapeditordone\" to finish the Map Management step.\n");
            }
            else
                d_displayToUser.instructionMessage("The Map \"" + p_mapFileName + "\" has been loaded into the game.\n" +
                        "However, the Map is not valid and should be modified accordingly before saving or moving to the next step in the game.");
        } catch (IOException e) {
            p_gameSession.setMap(d_gameMap);
            d_displayToUser.instructionMessage("The Map \"" + p_mapFileName + "\" was not found in the maps folder. A new empty map will be loaded instead.\n");
        }
    }

    /**
     * Handles the assign countries step.
     *
     * @param p_gameSession The game session.
     */
    public void assignCountries(GameSession p_gameSession) {
        List<Player> l_players = p_gameSession.getPlayers();
        List<Country> l_countries = p_gameSession.getMap().getCountries();

        if (l_countries.size() < l_players.size()) {
            System.out.println("=== Player Adjustment Required ===");
            System.out.println("The number of players exceeds the available countries. Adjusting accordingly...");
            System.out.println("---------------------------------------");

            for (int i = l_players.size(); i > l_countries.size(); i--) {
                String l_message = "--- Player " + l_players.get(i - 1).getName() + " has been removed due to an imbalance in player-country allocation.";
                System.out.println(l_message);
                l_players.remove(i - 1);
            }
            System.out.println("✔ Adjustment complete. Some players have been removed to ensure a fair game.");
            System.out.println("---------------------------------------");
        }

        for (int i = 0, j = 0; i < l_countries.size(); i++, j++) {
            if (j == l_players.size())
                j = 0;
            Random rn = new Random();
            int l_random = rn.nextInt(l_countries.size());

            while (true) {
                if (l_countries.get(l_random).getD_ownedBy() == null) {
                    l_countries.get(l_random).setD_ownedBy(l_players.get(j));
                    l_players.get(j).setD_countries_owned(l_countries.get(l_random));
                    break;
                }
                l_random = rn.nextInt(l_countries.size());
            }
        }

        System.out.println("=== Territory Assignment Complete ===");
        System.out.println("Countries have been strategically assigned to all players.");

        // Catch user action for monitoring observer
        LogEntryBuffer.getInstance().setValue("Countries are successfully assigned to the players.");
    }

    /**
     * Handles the assignReinforcements step.
     *
     * @param p_gameSession The game session.
     */
    public void assignReinforcements(GameSession p_gameSession) {
        for (int i = 0; i < p_gameSession.getPlayers().size(); i++) {
            p_gameSession.getPlayers().get(i).reinforcement(3);
        }

        // Catch user action for monitoring observer
        LogEntryBuffer.getInstance().setValue("Reinforcements are successfully assigned to the players.");
    }

    /**
     * Adds a continent to the map.
     *
     * @param p_gameMap        The game map.
     * @param p_continentName  The name of the continent.
     * @param p_continentValue The value of the continent.
     */
    public void addContinent(GameMap p_gameMap, String p_continentName, int p_continentValue) {
        Continent d_continent = new Continent(p_continentName, new ArrayList<>(), p_continentValue);
        p_gameMap.getContinents().add(d_continent);
        d_displayToUser.instructionMessage("Continent " + p_continentName + " added.");
    }

    /**
     * Removes a continent from the map.
     *
     * @param p_gameMap       The game map.
     * @param p_continentName The name of the continent.
     */
    public void removeContinent(GameMap p_gameMap, String p_continentName) {
        p_gameMap.getContinents().removeIf(d_continent -> d_continent.getName().equals(p_continentName));
        Continent.continentIdCounter--;
        d_displayToUser.instructionMessage("Continent " + p_continentName + " removed.");
    }

    /**
     * Adds a country to the map.
     *
     * @param p_gameMap       The game map.
     * @param p_countryName   The name of the country.
     * @param p_continentName The name of the continent.
     */
    public void addCountry(GameMap p_gameMap, String p_countryName, String p_continentName) {
        for (Continent l_continent : p_gameMap.getContinents()) {
            if (l_continent.getName().equals(p_continentName)) {
                Country l_country = new Country(p_gameMap.getCountries().size() + 1, p_countryName, new ArrayList<>(), 0);
                l_continent.getCountries().add(l_country);
                d_displayToUser.instructionMessage("Country " + p_countryName + " added to continent " + p_continentName + ".");
                break;
            }
        }
    }

    /**
     * Removes a country from the map.
     *
     * @param p_gameMap     The game map.
     * @param p_countryName The name of the country.
     */
    public void removeCountry(GameMap p_gameMap, String p_countryName) {
        for (Continent l_continent : p_gameMap.getContinents()) {
            l_continent.getCountries().removeIf(country -> country.getName().equals(p_countryName));
        }
        d_displayToUser.instructionMessage("Country " + p_countryName + " removed.");
    }

    /**
     * Adds a neighbor to a country.
     *
     * @param p_gameMap      The game map.
     * @param p_countryName  The name of the country.
     * @param p_neighborName The name of the neighbor country.
     */
    public void addNeighbor(GameMap p_gameMap, String p_countryName, String p_neighborName) {
        Country l_country = null;
        Country l_neighbor = null;
        for (Continent l_continent : p_gameMap.getContinents()) {
            for (Country c : l_continent.getCountries()) {
                if (c.getName().equals(p_countryName)) {
                    l_country = c;
                }
                if (c.getName().equals(p_neighborName)) {
                    l_neighbor = c;
                }
            }
        }
        if (l_country == null) {
            d_displayToUser.instructionMessage("Error: Country " + p_countryName + " does not exist.");
            return;
        }
        if (l_neighbor == null) {
            d_displayToUser.instructionMessage("Error: Country " + p_neighborName + " does not exist.");
            return;
        }
        if (l_country.getAdjacentCountries().contains(l_neighbor)) {
            d_displayToUser.instructionMessage("Neighbor " + p_neighborName + " is already added to country " + p_countryName + ".");
            return;
        }
        l_country.getAdjacentCountries().add(l_neighbor);
        l_neighbor.getAdjacentCountries().add(l_country);
        d_displayToUser.instructionMessage("Neighbor " + p_neighborName + " added to country " + p_countryName + ".");
    }


    /**
     * Removes a neighbor from a country.
     *
     * @param p_gameMap      The game map.
     * @param p_countryName  The name of the country.
     * @param p_neighborName The name of the neighbor country.
     */
    public void removeNeighbor(GameMap p_gameMap, String p_countryName, String p_neighborName) {
        Country l_country = null;
        Country l_neighbor = null;
        for (Continent l_continent : p_gameMap.getContinents()) {
            for (Country c : l_continent.getCountries()) {
                if (c.getName().equals(p_countryName)) {
                    l_country = c;
                }
                if (c.getName().equals(p_neighborName)) {
                    l_neighbor = c;
                }
            }
        }
        if (l_country == null) {
            d_displayToUser.instructionMessage("Error: Country " + p_countryName + " does not exist.");
            return;
        }
        if (l_neighbor == null) {
            d_displayToUser.instructionMessage("Error: Country " + p_neighborName + " does not exist.");
            return;
        }
        if (!l_country.getAdjacentCountries().contains(l_neighbor)) {
            d_displayToUser.instructionMessage("Neighbor " + p_neighborName + " is not a neighbor of country " + p_countryName + ".");
            return;
        }
        l_country.getAdjacentCountries().remove(l_neighbor);
        l_neighbor.getAdjacentCountries().remove(l_country);
        d_displayToUser.instructionMessage("Neighbor " + p_neighborName + " removed from country " + p_countryName + ".");
        d_displayToUser.instructionMessage("Neighbor " + p_countryName + " removed from country " + p_neighborName + ".");
    }

    /**
     * Displays the map as text.
     *
     * @param p_gameMap The game map.
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
     * Saves the map to a file.
     *
     * @param p_gameMap     The game map.
     * @param p_mapFileName The name of the file.
     */
    public void saveMap(GameMap p_gameMap, String p_mapFileName) {
        p_gameMap.setD_name(p_mapFileName);
        if (!validateMap(p_gameMap)) {
            d_displayToUser.instructionMessage("The Map \"" + p_gameMap.getD_name() + "\" can not be saved since it's not valid.\n" +
                    "Please modify the map accordingly before saving or moving to the next step in the game.");
            return;
        }
        d_displayToUser.instructionMessage("The Map \"" + p_gameMap.getD_name() + "\" is valid and it will be saved.\n");
        // Implement logic to save the map to a file
        try (BufferedWriter l_bufferedWriter = new BufferedWriter(new FileWriter(d_mapFolderPath + p_gameMap.getD_name()))) {
            l_bufferedWriter.write("[continents]");
            l_bufferedWriter.newLine();
            for (Continent l_continent : p_gameMap.getContinents()) {
                l_bufferedWriter.write(l_continent.getName() + " " + l_continent.getControlValue());
                l_bufferedWriter.newLine();
            }
            l_bufferedWriter.write("[countries]");
            l_bufferedWriter.newLine();
            for (Continent l_continent : p_gameMap.getContinents()) {
                for (Country l_country : l_continent.getCountries()) {
                    l_bufferedWriter.write(l_country.getCountryId() + " " + l_country.getName() + " " + l_continent.getD_continentId());
                    l_bufferedWriter.newLine();
                }
            }
            l_bufferedWriter.write("[borders]");
            l_bufferedWriter.newLine();
            for (Continent continent : p_gameMap.getContinents()) {
                for (Country country : continent.getCountries()) {
                    l_bufferedWriter.write(String.valueOf(country.getCountryId()));
                    for (Country adjacentCountry : country.getAdjacentCountries()) {
                        l_bufferedWriter.write(" " + adjacentCountry.getCountryId());
                    }
                    l_bufferedWriter.newLine();
                }
            }
            d_displayToUser.instructionMessage("The Map \"" + p_gameMap.getD_name() + "\" was successfully saved!\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method to handle validate Map command.
     *
     * @param p_gameMap The game map.
     */
    public void handleValidateMapCommand(GameMap p_gameMap) {
        if (validateMap(p_gameMap))
            d_displayToUser.instructionMessage("The Map is valid.");
    }

    /**
     * Validates the map. A map is valid if it has at least 3 continents,
     * 5 countries and 5 borders.
     * Also, a map is valid if their countries are connected.
     *
     * @param p_gameMap The game map.
     * @return True if the map is valid, false otherwise.
     */
    public boolean validateMap(GameMap p_gameMap) {
        // Implement logic to validate the map
        // First verify the number of continents, countries and borders.
        int l_continentsNumber = Continent.continentIdCounter - 1, l_countriesNumber = 0, l_bordersNumber = 0;
        for (Continent l_continent : p_gameMap.getContinents()) {
            for (Country l_country : l_continent.getCountries()) {
                l_countriesNumber++;
                l_bordersNumber += l_country.getAdjacentCountries().size();
            }
        }
        l_bordersNumber /= 2;
        if (l_continentsNumber < 3) {
            d_displayToUser.instructionMessage("Invalid map since it should have at least 3 continents! Currently it has only: " + l_continentsNumber + " continents.");
            return false;
        }
        if (l_countriesNumber < 5) {
            d_displayToUser.instructionMessage("Invalid map since it should have at least 5 countries! Currently it has only: " + l_countriesNumber + " countries.");
            return false;
        }
        if (l_bordersNumber < 5) {
            d_displayToUser.instructionMessage("Invalid map since it should have at least 5 borders! Currently it has only: " + l_bordersNumber + " borders.");
            return false;
        }
        // Then verify if all countries are connected
        HashSet<Country> l_connectedCountries = new HashSet<>();
        connectionVerification(p_gameMap.getContinents().getFirst().getCountries().getFirst(), l_connectedCountries);
        boolean l_countriesAreConnected = l_countriesNumber == l_connectedCountries.size();
        if (!l_countriesAreConnected)
            d_displayToUser.instructionMessage("Invalid map since the countries are not connected. The map should be totally connected for the game.");
        return l_countriesAreConnected;
    }

    /**
     * Internal method to verify countries connection.
     *
     * @param country the country used as a starting point.
     */
    private void connectionVerification(Country country, Set<Country> connectedCountries) {
        country.getAdjacentCountries().forEach(adjacentCountry -> {
            if (!connectedCountries.contains(adjacentCountry)) {
                connectedCountries.add(adjacentCountry);
                connectionVerification(adjacentCountry, connectedCountries);
            }
        });
    }





    /**
     * Handles various map-related commands for the game session.
     * <p>
     * This method parses the input command and executes the corresponding action
     * on the game map. It supports commands for loading, editing, saving, and
     * validating the map, as well as displaying the map and ending the map editing session.
     * </p>
     *
     * Supported commands:
     * <ul>
     *   <li><code>loadmap &lt;map_name&gt;</code>: Loads a map from the specified file.</li>
     *   <li><code>editmap &lt;map_name&gt;</code>: Edits the specified map.</li>
     *   <li><code>editcontinent -add &lt;continent_name&gt; &lt;control_value&gt;</code>: Adds a new continent with the specified control value.</li>
     *   <li><code>editcontinent -remove &lt;continent_name&gt;</code>: Removes the specified continent.</li>
     *   <li><code>editcountry -add &lt;country_name&gt; &lt;continent_name&gt;</code>: Adds a new country to the specified continent.</li>
     *   <li><code>editcountry -remove &lt;country_name&gt;</code>: Removes the specified country.</li>
     *   <li><code>editneighbor -add &lt;country_name&gt; &lt;neighbor_name&gt;</code>: Adds a neighbor to the specified country.</li>
     *   <li><code>editneighbor -remove &lt;country_name&gt; &lt;neighbor_name&gt;</code>: Removes a neighbor from the specified country.</li>
     *   <li><code>showmap</code>: Displays the current state of the map.</li>
     *   <li><code>savemap &lt;map_name&gt;</code>: Saves the current map to the specified file.</li>
     *   <li><code>validatemap</code>: Validates the current map.</li>
     *   <li><code>mapeditordone</code>: Ends the map editing session.</li>
     * </ul>
     *
     * @param p_command     The command to be executed, including any necessary parameters.
     * @param p_gameSession The current game session containing the game map and other session data.
     */
    public void handleCommand(String p_command, GameSession p_gameSession) {
        GameMap l_gameMap = p_gameSession.getMap(); // Initialize l_gameMap

        // Regular expression to match individual commands
        Pattern pattern = Pattern.compile("(\\w+\\s+-\\w+\\s+[^\\s]+\\s+\\d+|\\w+\\s+-\\w+\\s+[^\\s]+\\s+[^\\s]+|\\w+\\s+-\\w+\\s+[^\\s]+|\\w+\\s+[^\\s]+|\\w+)");
        Matcher matcher = pattern.matcher(p_command);

        while (matcher.find()) {
            String command = matcher.group(1).trim();
            String[] l_parts = command.split(" ");
            String l_cmd = l_parts[0];

            try {
                switch (l_cmd) {
                    case "loadmap":
                    case "editmap":
                        if (l_parts.length < 2) {
                            d_displayToUser.instructionMessage("Error: Missing map name for " + l_cmd + " command.");
                        } else {
                            loadMap(p_gameSession, l_parts[1]);
                            d_displayToUser.instructionMessage("The Map \"" + l_parts[1] + "\" is valid and has been loaded into the game.");
                        }
                        break;
                    case "editcontinent":
                        if (l_parts.length < 3 || (l_parts[1].equals("-remove") && l_parts.length > 3)) {
                            d_displayToUser.instructionMessage("Error: Invalid command format for editcontinent.");
                            break;
                        }
                        String action = l_parts[1];
                        String continentName = l_parts[2];

                        if (action.equals("-add")) {
                            if (l_gameMap.getContinents().stream().anyMatch(continent -> continent.getName().equals(continentName))) {
                                d_displayToUser.instructionMessage("Error: Continent " + continentName + " already exists.");
                            } else {
                                l_gameMap.addContinent(new Continent(continentName, new ArrayList<>(), Integer.parseInt(l_parts[3])));
                                d_displayToUser.instructionMessage("Continent " + continentName + " added.");
                            }
                        } else if (action.equals("-remove")) {
                            if (l_gameMap.getContinents().removeIf(continent -> continent.getName().equals(continentName))) {
                                d_displayToUser.instructionMessage("Continent " + continentName + " removed.");
                            } else {
                                d_displayToUser.instructionMessage("Error: Continent " + continentName + " does not exist.");
                            }
                        } else {
                            d_displayToUser.instructionMessage("Error: Invalid action for editcontinent.");
                        }
                        break;
                    case "editcountry":
                        if (l_parts.length < 3) {
                            d_displayToUser.instructionMessage("Error: Invalid command format for editcountry.");
                            break;
                        }
                        action = l_parts[1];
                        String countryName = l_parts[2];

                        if (action.equals("-add")) {
                            String continentNameForCountry = l_parts[3];
                            Continent continent = l_gameMap.getContinents().stream()
                                    .filter(c -> c.getName().equals(continentNameForCountry))
                                    .findFirst()
                                    .orElse(null);
                            if (continent == null) {
                                d_displayToUser.instructionMessage("Error: Continent " + continentNameForCountry + " does not exist.");
                            } else if (continent.getCountries().stream().anyMatch(country -> country.getName().equals(countryName))) {
                                d_displayToUser.instructionMessage("Error: Country " + countryName + " already exists in continent " + continentNameForCountry + ".");
                            } else {
                                continent.addCountry(new Country(continent.getD_continentId(), countryName, new ArrayList<>(), 0));
                                d_displayToUser.instructionMessage("Country " + countryName + " added to continent " + continentNameForCountry + ".");
                            }
                        } else if (action.equals("-remove")) {
                            boolean countryRemoved = false;
                            for (Continent continent : l_gameMap.getContinents()) {
                                if (continent.getCountries().removeIf(country -> country.getName().equals(countryName))) {
                                    d_displayToUser.instructionMessage("Country " + countryName + " removed.");
                                    countryRemoved = true;
                                    break;
                                }
                            }
                            if (!countryRemoved) {
                                d_displayToUser.instructionMessage("Error: Country " + countryName + " does not exist.");
                            }
                        } else {
                            d_displayToUser.instructionMessage("Error: Invalid action for editcountry.");
                        }
                        break;
                    case "editneighbor":
                        if (l_parts.length < 4) {
                            d_displayToUser.instructionMessage("Error: Invalid command format for editneighbor.");
                            break;
                        }
                        action = l_parts[1];
                        String countryName1 = l_parts[2];
                        String countryName2 = l_parts[3];

                        if (action.equals("-add")) {
                            addNeighbor(p_gameSession.getMap(), countryName1, countryName2);
                        } else if (action.equals("-remove")) {
                            removeNeighbor(p_gameSession.getMap(), countryName1, countryName2);
                        } else {
                            d_displayToUser.instructionMessage("Error: Invalid action for editneighbor.");
                        }
                        break;
                    case "showmap":
                        showMap(p_gameSession.getMap());
                        break;
                    case "savemap":
                        if (l_parts.length < 2) {
                            d_displayToUser.instructionMessage("Error: Missing map name for savemap command.");
                        } else {
                            saveMap(p_gameSession.getMap(), l_parts[1]);
                        }
                        break;
                    case "validatemap":
                        handleValidateMapCommand(p_gameSession.getMap());
                        break;
                    case "mapeditordone":
                        d_displayToUser.instructionMessage("Map editing session ended.");
                        break;
                    case "tournament":
                        // Tournament mode triggered
                        GameEngine.startTournamentModeGame(p_command);
                        System.exit(0);
                        break;
                    default:
                        d_displayToUser.instructionMessage("Error: Invalid command " + l_cmd + ".");
                        break;
                }
            } catch (Exception e) {
                d_displayToUser.instructionMessage("Error: " + e.getMessage());
            }
        }
    }
    /**
     * Handles the map management step.
     *
     * @param p_gameSession The game session.
     */
    public void handleMapManagementStep(GameSession p_gameSession) {
        Scanner l_scanner = UserInputScanner.getInstance().getScanner();
        String l_command;

        d_displayToUser.instructionMessage("=====================================");
        d_displayToUser.instructionMessage("       MAP MANAGEMENT STEP          ");
        d_displayToUser.instructionMessage("=====================================");

        d_displayToUser.instructionMessage("Use the following commands to manage the game map:");
        d_displayToUser.instructionMessage(String.format("%-20s %-20s %-20s %-20s", "⛁ loadmap", "✎ editmap", "⎙ savemap", "⚲ showmap"));
        d_displayToUser.instructionMessage(String.format("%-20s %-20s %-20s %-20s", "✔ validatemap", "✎ editcontinent", "✎ editcountry", "✎ editneighbor"));
        d_displayToUser.instructionMessage("----------------------------------------\n");
        d_displayToUser.instructionMessage(String.format("\uD83D\uDCA1 Please note that you can run the instruction: 'tournament' to go into the tournament mode.\n"));

        boolean l_isUserStillInTheStep = true;
        do {
            l_command = l_scanner.nextLine();

            // Catch user action for monitoring observer
            LogEntryBuffer.getInstance().setValue(l_command);

            handleCommand(l_command, p_gameSession);

            if (l_command.split(" ")[0].equals("mapeditordone")) {
                if (validateMap(p_gameSession.getMap())) {
                    d_displayToUser.instructionMessage("✔ The Map \"" + p_gameSession.getMap().getD_name() + "\" is valid and will be considered for the game.\n");
                    l_isUserStillInTheStep = false;
                } else {
                    d_displayToUser.instructionMessage("⚠ The Map \"" + p_gameSession.getMap().getD_name() + "\" is invalid. Please fix your Map accordingly before proceeding.\n");
                }
            }
        } while (l_isUserStillInTheStep);

        d_displayToUser.instructionMessage("=====================================");
        d_displayToUser.instructionMessage("    MAP MANAGEMENT STEP COMPLETED    ");
        d_displayToUser.instructionMessage("=====================================");
    }
}