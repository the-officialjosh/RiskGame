# RiskGame by CODING_HOLICS - Instruction Guide

Welcome to the Risk Game User Guide!  
This document provides a complete overview of the game’s functionalities, gameplay flow, and usage instructions.

---

## Table of Contents

1. [Introduction](#1-introduction)  
2. [Map Editing](#2-map-editing)  
3. [Player Management](#3-player-management)  
4. [Assigning Countries](#4-assigning-countries)  
5. [Reinforcement Phase](#5-reinforcement-phase)  
6. [Orders and Cards](#6-orders-and-cards)  
7. [Saving and Loading Maps](#7-saving-and-loading-maps)  
8. [Game Flow Overview](#8-game-flow-overview)  
9. [Tournament Mode](#9-tournament-mode)  
10. [Logging and Observability](#10-logging-and-observability)  
11. [Map Formats Supported](#11-map-formats-supported)  
12. [Miscellaneous Commands](#12-miscellaneous-commands)  
13. [Conclusion](#13-conclusion)  

---

## 1. Introduction

This guide provides comprehensive steps and explanations for playing and managing the Risk Game.  
The game allows users to edit maps, manage players, assign territories, issue orders, and play through various game phases, including an automated tournament mode.

---

## 2. Map Editing

Use map commands to create and modify game maps.

### Commands:

- `loadmap <map_name>`: Load an existing map.
- `editmap <map_name>`: Start editing the specified map.
- `editcontinent -add <continent_name> <control_value>`: Add a continent.
- `editcontinent -remove <continent_name>`: Remove a continent.
- `editcountry -add <country_name> <continent_name>`: Add a country to a continent.
- `editcountry -remove <country_name>`: Remove a country.
- `editneighbor -add <country_name> <neighbor_name>`: Add neighbour connections.
- `editneighbor -remove <country_name> <neighbor_name>`: Remove neighbour connections.
- `validatemap`: Validate the map for playability.
- `showmap`: Display the current map details.
- `savemap <map_name>`: Save the map to a file.
- `mapeditordone`: Finish map editing.

> **Note:** A valid map must include at least:
> - 3 continents
> - 5 countries
> - 5 borders  
> All countries must be fully connected.

---

## 3. Player Management

Manage the players participating in the game.

### Commands:

- `gameplayer -add <player_name>`: Add a new player.
- `gameplayer -remove <player_name>`: Remove an existing player.
- `assigncountries`: Randomly assign countries to all players.

> **Note:** If the number of players exceeds the available countries, excess players are automatically removed.

---

## 4. Assigning Countries

- Countries are assigned randomly among players.
- Each country is assigned to only one player.
- Ownership is visible during the map display.

---

## 5. Reinforcement Phase

At the start of each round, players receive reinforcements.

- Players get armies based on the formula:  `Max(countries_owned / 3, minimum_reinforcement_value)`
- The minimum reinforcement value is **4 armies**.
- Additional bonuses are granted for full control of continents.

---

## 6. Orders and Cards

During the order phase, players issue commands.

### Available Orders:

- **Deploy**: Place armies in controlled territories.
- **Advance**: Move armies between controlled countries.
- **Blockade**: Blockade a country.
- **Bomb**: Attack an enemy country.
- **Airlift**: Move armies between any two controlled countries.
- **Diplomacy**: Establish non-aggression pacts.

### Cards:

- Cards can be earned and played during orders.
- Card types include:
  - Bomb
  - Reinforcement
  - Blockade
  - Airlift
  - Diplomacy
- Use cards strategically to gain advantages.

### Issuing Orders:

- Players input commands during their turn.
- Orders are executed in sequence.

---

## 7. Saving and Loading Maps

- `savemap <map_name>`: Save the current map state.
- Maps are saved in the `maps/` directory.
- Saved maps retain continent, country, and neighbour data.

---

## 8. Game Flow Overview

The game follows structured phases:

1. Map Editing (optional)
2. Player Management
3. Assigning Countries
4. Reinforcement Phase
5. Order Issuing Phase
6. Order Execution Phase
7. Repeat from Step 4 until a winner is determined.

> Game state updates and player actions are displayed after each phase.

---

## 9. Tournament Mode

Automate multiple games across different maps and strategies.

### To start Tournament Mode:

Use the `tournament` command with parameters:
- Map names
- Player strategies
- Number of games
- Number of turns

Tournament mode runs the specified number of games automatically and provides results at the end.

---

## 10. Logging and Observability

- The game uses a log observer pattern.
- Key events such as country assignments and reinforcements are logged.
- Logs help track the flow of the game and debug issues.

⸻

## 11. Map Formats Supported

The game supports two map formats:
	•	Domination: Standard map format for the game.
	•	Conquest: Alternative map format.

Maps are auto-detected based on their content during loading.

⸻

12. Miscellaneous Commands
	•	validatemap: Check the validity of the current map.
	•	showmap: Display the current state of the game map.
	•	mapeditordone: Conclude the map editing phase.

⸻

13. Conclusion

This guide provides all the necessary information to successfully play and manage your Risk Game.
Use the provided commands to edit maps, manage players, issue orders, and engage in strategic gameplay.

Enjoy conquering the world!

⸻
