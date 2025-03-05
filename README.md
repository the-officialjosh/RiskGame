# RiskGame

## Example of Map instructions
This example is using the "europe.map" Map (The ones with '*' are required to pass the Map step).

Map step examples:
* loadmap europe.map*
* editmap europe.map
* editcontinent -add Test 3
* editcountry -add TestCountry Test
* editneighbor -add TestCountry Portugal
* editneighbor -add TestCountry Luxembourg
* editneighbor -remove TestCountry Portugal
* editneighbor -remove TestCountry Luxembourg
* editcountry -remove TestCountry
* editcontinent -remove Test
* savemap europe.map
* validatemap
* mapeditordone*

Player creation example:
* gameplayer -add Ahmed*
* gameplayer -add Irfan*

Assign countries phase:
* assigncountries*

Deploy phase:
* deploy countryId numberOfArmies*