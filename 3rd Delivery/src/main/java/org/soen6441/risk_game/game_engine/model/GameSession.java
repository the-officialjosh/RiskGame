package org.soen6441.risk_game.game_engine.model;

import org.soen6441.risk_game.game_map.model.GameMap;
import org.soen6441.risk_game.orders.model.Diplomacy;
import org.soen6441.risk_game.player_management.model.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * GameSession class which represents the actual game state and holds
 * all the information about the game.
 *
 * @author Ahmed Fakhir
 * @author Irfan Maknojia
 * @version 1.0
 */
public class GameSession implements Serializable {
    private List<Player> d_players;
    private GameMap d_map;
    private List<Diplomacy> d_diplomacyPairs = new ArrayList<>();
    private final String d_saveGameFolderPath = "out/saved-games/";

    /**
     * Returns the list of players.
     *
     * @return d_players The list of players.
     */
    public List<Player> getPlayers() {
        return d_players;
    }

    /**
     * Sets the list of players
     *
     * @param p_players The list of players.
     */
    public void setPlayers(List<Player> p_players) {
        this.d_players = p_players;
    }

    /**
     * Gets player by name.
     *
     * @param p_playerName the player name
     * @return the player by name
     */
    public Player getPlayerByName(String p_playerName) {
        for (Player player : d_players) {
            if (player.getName().equals(p_playerName)) {
                return player;
            }
        }
        return null;
    }

    /**
     * Returns the game global map.
     *
     * @return d_map The game map.
     */
    public GameMap getMap() {
        return d_map;
    }

    /**
     * Sets the game map.
     *
     * @param p_map The game map.
     */
    public void setMap(GameMap p_map) {
        this.d_map = p_map;
    }

    /**
     * Adds a diplomacy pair for this turn.
     *
     * @param p1 First player
     * @param p2 Second player
     */
    public void addDiplomacyPair(Player p1, Player p2) {
        d_diplomacyPairs.add(new Diplomacy(p1, p2));
        d_diplomacyPairs.add(new Diplomacy(p2, p1)); // Ensure it's mutual
    }

    public List<Diplomacy> getD_diplomacyPairs() {
        return d_diplomacyPairs;
    }

    /**
     * Checks if two players are currently in a diplomacy relationship.
     *
     * @param p1 First player
     * @param p2 Second player
     * @return true if they are in diplomacy, false otherwise.
     */
    public boolean areInDiplomacy(Player p1, Player p2) {
        for (Diplomacy pair : d_diplomacyPairs) {
            if ((pair.getD_issuer().equals(p1) && pair.getD_target().equals(p2)) || (pair.getD_issuer().equals(p2) && pair.getD_target().equals(p1))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Clears all diplomacy pairs. Should be called at the end of each round.
     */
    public void clearDiplomacyPairs() {
        d_diplomacyPairs.clear();
    }

    /**
     * Sets d diplomacy pairs.
     *
     * @param d_diplomacyPairs the d diplomacy pairs
     */
    public void setD_diplomacyPairs(List<Diplomacy> d_diplomacyPairs) {
        this.d_diplomacyPairs = d_diplomacyPairs;
    }

    /**
     * Load game.
     *
     * @param gameName the game name
     */
    public void loadGame(String gameName) {
        String filename = d_saveGameFolderPath + gameName + ".dat";
        GameSession temp_gameSession = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename));
            try {
                while (true) {
                    temp_gameSession = (GameSession) ois.readObject();
                }
            } catch (EOFException _) {
            }
            ois.close();
            this.setPlayers(temp_gameSession.d_players);
            this.setMap(temp_gameSession.d_map);
            this.setD_diplomacyPairs(temp_gameSession.d_diplomacyPairs);
            System.out.println("File loaded.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Save game.
     *
     * @param gameName the game name
     */
    public void saveGame(String gameName) {
        String filename = d_saveGameFolderPath + gameName + ".dat";
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename));
            oos.writeObject(this);
            oos.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
