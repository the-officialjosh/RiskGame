# RiskGame by CODING_HOLICS - Instruction Guide

This document provides step-by-step instructions for interacting with the **RiskGame** system, including loading and editing maps, adding players, and deploying armies.

---

## 1. Loading and Editing a Map
Before modifying a map, you must first **load** or **edit** it.  
⚠️ *Commands marked with `*` are required to complete the map setup.*

### Load an Existing Map
```sh
loadmap europe.map*
```

### Edit an Existing Map
```sh
editmap europe.map
```

---

## 2. Editing the Map
Once the map is loaded, you can **add or remove continents, countries, and neighboring connections**.

### Adding a Continent
```sh
editcontinent -add <continent_name> <control_value>
```
**Example:**
```sh
editcontinent -add Test 3
```

### Adding a Country to a Continent
```sh
editcountry -add <country_name> <continent_name>
```
**Example:**
```sh
editcountry -add TestCountry Test
```

### Adding Neighboring Connections
```sh
editneighbor -add <country_name> <neighbor_country>
```
**Examples:**
```sh
editneighbor -add TestCountry Portugal
editneighbor -add TestCountry Luxembourg
```

### Removing Neighboring Connections
```sh
editneighbor -remove <country_name> <neighbor_country>
```
**Examples:**
```sh
editneighbor -remove TestCountry Portugal
editneighbor -remove TestCountry Luxembourg
```

### Removing a Country from a Continent
```sh
editcountry -remove <country_name>
```
**Example:**
```sh
editcountry -remove TestCountry
```

### Removing a Continent
```sh
editcontinent -remove <continent_name>
```
**Example:**
```sh
editcontinent -remove Test
```

---

## 3. Saving and Validating the Map
After making changes, save and validate the map to ensure it's properly formatted.

### Save the Map
```sh
savemap europe.map
```

### Validate the Map
```sh
validatemap
```

### Finish Editing the Map
```sh
mapeditordone*
```

---

## 4. Player Setup
Once the map is set up, players can be added to the game.

### Add Players
```sh
gameplayer -add <player_name>*
```
**Examples:**
```sh
gameplayer -add Ahmed*
gameplayer -add Irfan*
```

---

## 5. Assigning Countries
After players are added, countries are assigned automatically.

```sh
assigncountries*
```

---

## 6. Deploy Phase
During the deploy phase, players can assign armies to their countries.

```sh
deploy <country_id> <number_of_armies>*
```
**Example:**
```sh
deploy 24 5
```

---

## 7. Final Notes
- Commands marked with `*` are **mandatory** for completing that phase.
- Follow the sequence of steps to ensure proper game setup.
- If you encounter issues, validate the map before proceeding.

