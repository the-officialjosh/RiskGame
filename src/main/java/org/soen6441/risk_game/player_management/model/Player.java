package org.soen6441.risk_game.player_management.model;

import org.soen6441.risk_game.game_map.model.Country;
import org.soen6441.risk_game.game_map.view.DisplayToUser;
import org.soen6441.risk_game.monitoring.LogEntryBuffer;
import org.soen6441.risk_game.orders.model.Deploy;
import org.soen6441.risk_game.orders.model.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Player {
    private String d_name;
    private int d_numberOfReinforcementsArmies;
    private List<Order> d_orders;
    private List<Country> d_countries_owned;
    private int[] d_cards_owned;
    private final DisplayToUser d_displayToUser;

    public Player(String p_name, int p_numberOfReinforcementsArmies, List<Order> p_orders) {
        this.d_name = p_name;
        this.d_numberOfReinforcementsArmies = p_numberOfReinforcementsArmies;
        this.d_orders = p_orders;
        this.d_countries_owned = new ArrayList<>();
        this.d_cards_owned = new int[]{0, 0, 0, 0, 0}; // Bomb, Reinforcement, Blockade, Airlift, Diplomacy
        this.d_displayToUser = new DisplayToUser();
    }

    public String getName() {
        return d_name;
    }

    public void setName(String p_name) {
        this.d_name = p_name;
    }

    public int getNumberOfReinforcementsArmies() {
        return d_numberOfReinforcementsArmies;
    }

    public void setNumberOfReinforcementsArmies(int p_numberOfReinforcementsArmies) {
        this.d_numberOfReinforcementsArmies = p_numberOfReinforcementsArmies;
    }

    public void reinforcement(int p_minimumNumberOfReinforcementsArmies) {
        int l_totalNewArmies = Math.max(d_countries_owned.size() / 3, p_minimumNumberOfReinforcementsArmies);
        setNumberOfReinforcementsArmies(l_totalNewArmies);
    }

    public List<Order> getOrders() {
        return d_orders;
    }

    public void setOrders(Order p_order) {
        this.d_orders.add(p_order);
    }

    public void next_order() {
        for (Order order : this.getOrders()) {
            order.execute();
        }
    }

    public List<Country> getD_countries_owned() {
        return d_countries_owned;
    }

    public void setD_countries_owned(Country d_country_owned) {
        this.d_countries_owned.add(d_country_owned);
    }

    public boolean hasReinforcementsArmies() {
        return d_numberOfReinforcementsArmies > 0;
    }

    public boolean isReinforcementPhaseComplete() {
        return d_numberOfReinforcementsArmies == 0;
    }

    public Country findCountryById(List<Country> countries, int countryID) {
        return countries.stream().filter(country -> country.getCountryId() == countryID).findFirst().orElse(null);
    }

    public boolean validNumberOfReinforcementArmies(int l_numOfArmies) {
        return l_numOfArmies <= d_numberOfReinforcementsArmies;
    }

    public int get_specific_cards_count(String p_cardType) {
        return switch (p_cardType.toLowerCase()) {
            case "bomb" -> d_cards_owned[0];
            case "reinforcement" -> d_cards_owned[1];
            case "blockade" -> d_cards_owned[2];
            case "airlift" -> d_cards_owned[3];
            case "diplomacy" -> d_cards_owned[4];
            default -> 0;
        };
    }

    public int[] getD_cards_owned() {
        return d_cards_owned;
    }

    public void assignCard() {
        Random rn = new Random();
        int randomNumber = rn.nextInt(5);
        this.d_cards_owned[randomNumber - 1] += 1;
    }

    public boolean hasBombCard() {
        return d_cards_owned != null && d_cards_owned.length > 0 && d_cards_owned[0] > 0;
    }

    public void useBombCard() {
        if (hasBombCard()) {
            d_cards_owned[0] -= 1;
        }
    }

    public boolean hasDiplomacyCard() {
        return d_cards_owned != null && d_cards_owned.length > 4 && d_cards_owned[4] > 0;
    }

    public void useDiplomacyCard() {
        if (hasDiplomacyCard()) {
            d_cards_owned[4] -= 1;
        }
    }

    public void issue_order(boolean b) {
        Scanner l_scanner = new Scanner(System.in);
        if (d_numberOfReinforcementsArmies <= 0) {
            d_displayToUser.instructionMessage(this.d_name + " has no reinforcement left.");
        }
        while (true) {
            String l_command = l_scanner.nextLine().trim();
            LogEntryBuffer.getInstance().setValue(l_command);
            String[] parts = l_command.split(" ");

            try {
                switch (parts[0].toLowerCase()) {
                    case "deploy" -> {
                        if (!b) {
                            d_displayToUser.instructionMessage("Deploy is only allowed during the reinforcement phase.");
                            continue;
                        }
                        if (parts.length != 3) {
                            d_displayToUser.instructionMessage("Usage: deploy <countryID> <numberOfArmies>");
                            continue;
                        }
                        int countryID = Integer.parseInt(parts[1]);
                        int armies = Integer.parseInt(parts[2]);

                        if (!validNumberOfReinforcementArmies(armies)) {
                            d_displayToUser.instructionMessage("Cannot deploy more armies than available. You have " + d_numberOfReinforcementsArmies + " left.");
                            continue;
                        }
                        if (findCountryById(this.d_countries_owned, countryID) == null) {
                            d_displayToUser.instructionMessage("You can only deploy to countries you own.");
                            continue;
                        }
                        setOrders(new Deploy(this, armies, countryID));
                        d_numberOfReinforcementsArmies -= armies;
                        return;
                    }
                    case "airlift" -> {
                        if (b) {
                            d_displayToUser.instructionMessage("Airlift is not allowed during the reinforcement phase.");
                            continue;
                        }
                        if (parts.length != 4) {
                            d_displayToUser.instructionMessage("Usage: airlift <fromCountryID> <toCountryID> <numberOfArmies>");
                            continue;
                        }
                        int fromID = Integer.parseInt(parts[1]);
                        int toID = Integer.parseInt(parts[2]);
                        int moveArmies = Integer.parseInt(parts[3]);

                        Country fromCountry = findCountryById(this.d_countries_owned, fromID);
                        Country toCountry = findCountryById(this.d_countries_owned, toID);
                        if (fromCountry == null || toCountry == null) {
                            d_displayToUser.instructionMessage("You must own both source and destination countries.");
                            continue;
                        }
                        if (fromCountry.getExistingArmies().get(this) < moveArmies) {
                            d_displayToUser.instructionMessage("Not enough armies in source country.");
                            continue;
                        }
                        if (d_cards_owned[3] <= 0) {
                            d_displayToUser.instructionMessage("You don't have an Airlift card.");
                            continue;
                        }
                        d_cards_owned[3]--;
                        setOrders(new org.soen6441.risk_game.orders.model.Airlift(fromCountry, toCountry, moveArmies));
                        return;
                    }
                    case "blockade" -> {
                        if (b) {
                            d_displayToUser.instructionMessage("Blockade is not allowed during the reinforcement phase.");
                            continue;
                        }
                        if (parts.length != 2) {
                            d_displayToUser.instructionMessage("Usage: blockade <countryID>");
                            continue;
                        }
                        int countryID = Integer.parseInt(parts[1]);
                        Country country = findCountryById(this.d_countries_owned, countryID);
                        if (country == null) {
                            d_displayToUser.instructionMessage("You can only blockade your own country.");
                            continue;
                        }
                        if (country.getExistingArmies().get(this) <= 0) {
                            d_displayToUser.instructionMessage("No armies in selected country.");
                            continue;
                        }
                        if (d_cards_owned[2] <= 0) {
                            d_displayToUser.instructionMessage("You don't have a Blockade card.");
                            continue;
                        }
                        d_cards_owned[2]--;
                        setOrders(new org.soen6441.risk_game.orders.model.Blockade(this, country));
                        return;
                    }
                    case "bomb" -> {
                        if (b) {
                            d_displayToUser.instructionMessage("Bomb is not allowed during the reinforcement phase.");
                            continue;
                        }
                        if (parts.length != 4) {
                            d_displayToUser.instructionMessage("Usage: bomb <targetCountryID> <sourceCountryID> <targetPlayerName>");
                            continue;
                        }
                        int targetCountryID = Integer.parseInt(parts[1]);
                        int sourceCountryID = Integer.parseInt(parts[2]);
                        String targetPlayerName = parts[3];

                        Country targetCountry = findCountryById(org.soen6441.risk_game.game_engine.model.GameSession.getInstance().getMap().getCountries(), targetCountryID);
                        if (targetCountry == null || this.d_countries_owned.contains(targetCountry)) {
                            d_displayToUser.instructionMessage("Invalid target country.");
                            continue;
                        }

                        boolean adjacent = this.d_countries_owned.stream().anyMatch(c -> c.getAdjacentCountries().contains(targetCountry));
                        if (!adjacent) {
                            d_displayToUser.instructionMessage("You can only bomb adjacent enemy countries.");
                            continue;
                        }

                        if (d_cards_owned[0] <= 0) {
                            d_displayToUser.instructionMessage("You don't have a Bomb card.");
                            continue;
                        }

                        Country sourceCountry = findCountryById(this.d_countries_owned, sourceCountryID);
                        if (sourceCountry == null) {
                            d_displayToUser.instructionMessage("Invalid source country. You must own the source country.");
                            continue;
                        }

                        d_cards_owned[0]--;
                        System.out.println("🎴 Bomb card used. Remaining: " + d_cards_owned[0]);
                        setOrders(new org.soen6441.risk_game.orders.model.Bomb(sourceCountry, this, targetCountry));
                        return;
                    }
                    case "diplomacy" -> {
                        if (b) {
                            d_displayToUser.instructionMessage("Diplomacy is not allowed during the reinforcement phase.");
                            continue;
                        }
                        if (parts.length != 2) {
                            d_displayToUser.instructionMessage("Usage: diplomacy <targetPlayerName>");
                            continue;
                        }
                        if (d_cards_owned[4] <= 0) {
                            d_displayToUser.instructionMessage("You don't have a Diplomacy card.");
                            continue;
                        }
                        Player targetPlayer = org.soen6441.risk_game.game_engine.model.GameSession.getInstance().getPlayerByName(parts[1]);
                        if (targetPlayer == null) {
                            d_displayToUser.instructionMessage("Invalid player name. Target player not found.");
                            continue;
                        }
                        d_cards_owned[4]--;
                        setOrders(new org.soen6441.risk_game.orders.model.Diplomacy(this, targetPlayer));
                        return;
                    }
                    default -> {
                        d_displayToUser.instructionMessage("Unknown command.");
                    }
                }
            } catch (Exception e) {
                d_displayToUser.instructionMessage("Invalid command format or data. Try again.");
            }
        }
    }
}
