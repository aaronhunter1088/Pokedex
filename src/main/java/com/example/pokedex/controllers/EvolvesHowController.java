package com.example.pokedex.controllers;

import com.example.pokedex.service.PokemonService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigInteger;
import java.util.*;

@Controller
public class EvolvesHowController extends BaseController {

    private static final Logger logger = LogManager.getLogger(EvolvesHowController.class);

    Integer pokemonID;
    Map<Integer, Map<String, Object>> pokemonIdAndAttributesMap = new HashMap<>();
    Map<String, Object> specificAttributesMap = Collections.emptyMap();
    Map<Integer, List<List<Integer>>> pokemonIDToEvolutionChainMap;
    Integer pokemonChainID = 0;
    List<Integer> allIDs = new ArrayList<>();
    List<List<Integer>> family = new ArrayList<>(); //number[][] = []
    Integer minimumLevel = 0;
    String useItem = "";
    String heldItem = "";
    String minimumHappiness = "";
    String dayNight = ""; // day or night
    List<String> locations = new ArrayList<>(); // String locations
    Integer minimumAffection = 0;
    Integer minimumBeauty = 0;
    List<String> knownMoves = new ArrayList<>(); // String moves
    List<String> knownMoveTypes = new ArrayList<>(); // String types
    String needsRain = ""; // yes or no
    String turnUpsideDown = ""; // yes or no
    boolean doesPokemonEvolve = false,
            hasMinimumLevel = false,
            hasUseItem = false,
            hasHeldItem = false,
            hasMinimumHappiness = false,
            isABaby = false,
            hasDayNight = false,
            hasLocations = false,
            hasMinimumAffection = false,
            hasBeauty = false,
            hasKnownMoves = false,
            hasKnownMoveType = false,
            hasNeedsRain = false,
            // other attributes
            hasTurnUpsideDown = false,
            emptyChain = true;

    PokemonService pokemonService;

    @Autowired
    public EvolvesHowController(PokemonService pokemonService) {
        super(pokemonService);
        this.pokemonService = pokemonService;
    }

    @GetMapping(value="/evolves-how")
    public ModelAndView evolvesHow(@RequestParam(name="pokemonId") int pokemonId, ModelAndView mav) {
        logger.info("pokemonId: {}", pokemonId);
        this.pokemonID = pokemonId;
        setupEvolvesHow();
        mav.addObject("pokemonId", pokemonId);
        mav.addObject("pokemonChainId", pokemonChainID);
        mav.addObject("doesPokemonEvolve", doesPokemonEvolve);
        mav.addObject("attributesMap", pokemonIdAndAttributesMap.get(pokemonId));
        mav.addObject("emptyChain", emptyChain);
        mav.addObject("hasMinimumLevel", hasMinimumLevel);
        mav.addObject("hasUseItem", hasUseItem);
        mav.addObject("hasHeldItem", hasHeldItem);
        mav.addObject("hasMinimumHappiness", hasMinimumHappiness);
        mav.addObject("isABaby", isABaby);
        mav.addObject("hasDayNight", hasDayNight);
        mav.addObject("hasLocations", hasLocations);
        mav.addObject("hasMinimumAffection", hasMinimumAffection);
        mav.addObject("hasBeauty", hasBeauty);
        mav.addObject("hasKnownMoves", hasKnownMoves);
        mav.addObject("hasKnownMoveType", hasKnownMoveType);
        mav.addObject("hasNeedsRain", hasNeedsRain);
        mav.addObject("hasTurnUpsideDown", hasTurnUpsideDown);
        mav.setViewName("evolves-how");
        return mav;
    }

    private void setupEvolvesHow() {
        this.pokemonIDToEvolutionChainMap = this.pokemonService.getEvolutionsMap();
        this.specificAttributesMap = this.generateDefaultAttributesMap();
        this.pokemonChainID = getEvolutionChainID(pokemonIDToEvolutionChainMap, String.valueOf(pokemonID));
        logger.info("chainId: {}", pokemonChainID);
        this.family = this.pokemonIDToEvolutionChainMap.get(this.pokemonChainID);
        this.allIDs = family.stream().flatMap(Collection::stream).toList();
        logger.info("family: " + allIDs);
        if (pokemonChainID != 0) {
            Map<String, Object> chainResponse = pokemonService.getPokemonChainData(String.valueOf(pokemonChainID));
            logger.info("chainResponse: {}", chainResponse);
            /*
            "baby_trigger_item": null,
            "chain": {
                "evolution_details": List,
                "evolves_to": List,
                "is_baby": boolean
                "species": {
                    "name": "Pichu" String
                    "url": https://pokeapi.co/api/v2/pokemon-species/172/
                },
            },
            "id": "10"
             */
            if (chainResponse.isEmpty()) {
                return;
            } else {
                emptyChain = false;
                getEvolutionDetails((LinkedHashMap<String, Object>) chainResponse.get("chain"));
                for(Integer id : allIDs) {
                    logger.info("checking map for existence: {}", id);
                    if (!this.pokemonIdAndAttributesMap.containsKey(id)) {
                        // FIRST, check if there are similar forms. if so, populate missing id with existing map
                        //this.matchSiblingPokemonWithSiblingAttributeMap(id)
                        logger.info("{} not found! populating with default attrMap", id);
                        this.pokemonIdAndAttributesMap.put(id, this.generateDefaultAttributesMap());
                    }
                }
                // clean up map, remove unnecessary duplicates
                cleanupAttributesMap();
                logger.info("Attributes map cleaned up in Evolves-how");
                setupBooleans(pokemonIdAndAttributesMap.get(pokemonID));
                doesPokemonEvolve = pokemonEvolves();
                logger.info("does pokemon evolve: {}", doesPokemonEvolve);
            }
        }
    }

    private Map<String, Object> generateDefaultAttributesMap() {
        return new TreeMap<>() {{
            put("name", null); // on screen
            put("gender", null);
            put("isBaby", null);
            put("heldItem", null); // on screen
            put("useItem", null); // on screen
            put("knownMove", null); // on screen
            put("knownMoveType", null); // on screen
            put("location", null); // on screen
            put("minAffection", null); // on screen
            put("minBeauty", null); // on screen
            put("minHappiness", null); // on screen
            put("minLevel", null); // on screen
            put("needsRain", null); // on screen
            put("timeOfDay", null); // on screen
            put("partySpecies", null);
            put("relativePhysicalStats", null);
            put("tradeSpecies", null);
            put("turnUpsideDown", null); // on screen
        }};
    }

//    private Integer getEvolutionChainID(int pokemonId) {
//        List<Integer> keys = this.pokemonIDToEvolutionChainMap.keySet().stream().toList();
//        Integer keyToReturn = 0;
//        keysLoop:
//        for(Integer key: keys) {
//            List<List<Integer>> pokemonIds = pokemonIDToEvolutionChainMap.get(key);
//            for (List<Integer> chainIds : pokemonIds) {
//                if (chainIds.contains(pokemonId)) {
//                    keyToReturn = key;
//                    break keysLoop;
//                }
//            }
//        }
//        return keyToReturn;
//    }

    private void getEvolutionDetails(Map<String, Object> chain) {
        logger.info("chain: {}", chain);
        String name = (String) ((Map<String,Object>)chain.get("species")).get("name");
        String pkmnId = ((String) ((Map<String,Object>)chain.get("species")).get("url")).split("/")[6];
        Map<String, Object> evolutionDetails = generateDefaultAttributesMap(); //new HashMap<>();
        logger.info("name: {}, id: {}", name, pkmnId);
        for (int i=0; i<((List<Object>)chain.get("evolves_to")).size(); i++) {
            Map<String,Object> evolvesTo = (Map<String,Object>) ((List<Object>) chain.get("evolves_to")).get(i);
            try {
                for (int j = 0; j < ((List<Object>) evolvesTo.get("evolution_details")).size(); j++) {
                    evolutionDetails = (Map<String, Object>) ((List<Object>) evolvesTo.get("evolution_details")).get(j);
                    evolutionDetails.put("is_baby", chain.get("is_baby"));
                    evolutionDetails.put("id", pkmnId);
                    evolutionDetails.put("name", name);
                    if (!pokemonIdAndAttributesMap.containsKey(pkmnId)) {
                        setAttributesMap(evolutionDetails);
                    } else {
                        updateAttributesMap(evolutionDetails, pokemonIdAndAttributesMap.get(pkmnId));
                    }
                    getEvolutionDetails(evolvesTo);
                }
                if ( ((List<Object>) evolvesTo.get("evolves_to")).size() >= 0) {
                    if ( ((List<Object>) evolvesTo.get("evolves_to")).size() > 0) {
                        logger.info("Printing final stage names");
                    } else {
                        logger.info("Printing final stage name");
                    }
                    for (int k = 0; k < ((List<Object>) evolvesTo.get("evolves_to")).size(); k++) {
                        name = (String) ((Map<String,Object>) ((Map<String,Object>) ((List<Object>)evolvesTo.get("evolves_to")).get(k)).get("species")).get("name");
                        pkmnId = ((String) ((Map<String,Object>) ((Map<String, Object>) ((List<Object>) evolvesTo.get("evolves_to")).get(k)).get("species")).get("url")).split("/")[6];
                        evolutionDetails = generateDefaultAttributesMap(); //new HashMap<>();
                        evolutionDetails.put("is_baby", ((Map<String,Object>) ((List<Object>)evolvesTo.get("evolves_to")).get(k)).get("is_baby"));
                        evolutionDetails.put("id", pkmnId);
                        evolutionDetails.put("name", name);
                        setAttributesMap(evolutionDetails);
                        pokemonIdAndAttributesMap.put(Integer.valueOf(String.valueOf(pkmnId)), specificAttributesMap);
                    }
                }
            } catch (Exception e) {
                logger.error("Parse Exception: {}", e.getMessage());
            }
        }
    }

    public void setAttributesMap(Map<String, Object> details) {
        logger.info("evolution_details for: {} = {}", details.get("name"), details);
        this.specificAttributesMap = this.generateDefaultAttributesMap();
        //if (details == null) return attributesMap
        this.specificAttributesMap.put("name", details.get("name"));
        this.specificAttributesMap.put("gender", null != details.get("gender") ? details.get("gender") : null);
        this.specificAttributesMap.put("isBaby", details.get("is_baby"));
        Map<String,Object> map = ((Map<String, Object>)details.get("held_item"));
        this.specificAttributesMap.put("heldItem", (null != map) ? map.get("name") : null);
        map = ((Map<String,Object>)details.get("item"));
        this.specificAttributesMap.put("useItem", (null != map) ? map.get("name") : null);
        this.specificAttributesMap.put("minHappiness", details.get("min_happiness"));
        this.specificAttributesMap.put("minLevel", details.get("min_level"));
        this.specificAttributesMap.put("timeOfDay", (details.get("time_of_day") != "") ? details.get("time_of_day") : null);
        map = ((Map<String,Object>)details.get("location"));
        this.specificAttributesMap.put("location", (null != map) ? map.get("name") : null);
        this.specificAttributesMap.put("needsRain", details.get("needs_overworld_rain"));
        this.specificAttributesMap.put("minAffection", details.get("min_affection"));
        this.specificAttributesMap.put("minBeauty", details.get("min_beauty"));
        map = ((Map<String,Object>)details.get("known_move"));
        this.specificAttributesMap.put("knownMove", (null != map) ? map.get("name") : null);
        map = ((Map<String,Object>)details.get("known_move_type"));
        this.specificAttributesMap.put("knownMoveType", (null != map) ? map.get("name") : null);
        this.specificAttributesMap.put("partySpecies", details.get("party_species"));
        this.specificAttributesMap.put("relativePhysicalStats", details.get("relative_physical_stats"));
        this.specificAttributesMap.put("tradeSpecies", details.get("trade_species"));
        this.specificAttributesMap.put("turnUpsideDown", details.get("turn_upside_down"));

        this.pokemonIdAndAttributesMap.put(Integer.valueOf((String) details.get("id")), this.specificAttributesMap);
    }

    public void updateAttributesMap(Map<String, Object> details, Map<String, Object> attributesMap) {
        logger.info("evolution_detailsUpdate for: {} {}", attributesMap.get("name"), details);
        Map<String,Object> map = null;
        if (details.get("gender") != null) {
            if (attributesMap.get("gender") == null) {
                attributesMap.put("gender",details.get("gender"));
            } else {
                var gender = attributesMap.get("gender");
                String newGender = (String) details.get("gender");
                List<String> genders = (gender instanceof List) ? (List<String>) gender : Collections.singletonList(gender.toString());
                genders.add(newGender);
                attributesMap.put("gender", genders);
            }
        }
        if (details.get("held_item") != null) {
            map = ((Map<String, Object>) details.get("held_item"));
            if (attributesMap.get("heldItem") == null) {
                attributesMap.put("heldItem", (map != null) ? Collections.singletonList(map.get("name")) : null);
            } else {
                var heldItem = attributesMap.get("heldItem");
                String newHeldItem = (map != null) ? (String) map.get("name") : "";
                List<String> heldItems = (heldItem instanceof List) ? (List<String>) heldItem : Collections.singletonList(heldItem.toString());
                heldItems.add(newHeldItem);
                attributesMap.put("heldItem", heldItems);
            }
        }
        if (details.get("item") != null) {
            map = ((Map<String, Object>) details.get("item"));
            if (attributesMap.get("useItem") == null) {
                attributesMap.put("useItem", (map != null) ? Collections.singletonList(map.get("name")) : null);
            } else {
                var item = attributesMap.get("useItem");
                String newItem = (map != null) ? (String) map.get("name") : "";
                List<String> items = (item instanceof List) ? (List<String>) item : Collections.singletonList(item.toString());
                items.add(newItem);
                attributesMap.put("useItem", items);
            }
        }
        if (details.get("min_happiness") != null) {
            if (attributesMap.get("minHappiness") == null) {
                attributesMap.put("minHappiness", Collections.singletonList(details.get("min_happiness")));
            } else {
                var minHappy = attributesMap.get("minHappiness");
                String newMinHappy = (String) details.get("min_happiness");
                List<String> happinesses = (minHappy instanceof List) ? (List<String>) minHappy : Collections.singletonList(minHappy.toString());
                happinesses.add(newMinHappy);
                attributesMap.put("minHappiness", happinesses);
            }
        }
        if (details.get("time_of_day") != null) {
            if (attributesMap.get("timeOfDay") == null) {
                attributesMap.put("timeOfDay", Collections.singletonList(details.get("time_of_day")));
            } else {
                var timeOfDay = attributesMap.get("timeOfDay");
                String newTimeOfDay = (String) details.get("time_of_day");
                List<String> timeOfDays = (timeOfDay instanceof List) ? (List<String>) timeOfDay : Collections.singletonList(timeOfDay.toString());
                timeOfDays.add(newTimeOfDay);
                attributesMap.put("timeOfDay", timeOfDays);
            }
        }
        if (details.get("location") != null) {
            map = ((Map<String,Object>)details.get("location"));
            if (attributesMap.get("location") == null) {
                attributesMap.put("location", (map != null) ? Collections.singletonList(map.get("name")) : null);
            } else {
                var location = attributesMap.get("location");
                String newLocation = (map != null) ? (String) map.get("name") : "";
                List<String> locations = (location instanceof List) ? (List<String>) location : Collections.singletonList(location.toString());
                locations.add(newLocation);
                attributesMap.put("location", locations);
            }
        }
        if (details.get("needs_overworld_rain") != null) {
            if (attributesMap.get("needsRain") == null) {
                attributesMap.put("needsRain", Collections.singletonList(details.get("needs_overworld_rain")));
            } else {
                var needsRain = attributesMap.get("needsRain");
                String newNeedsRain = (String) details.get("needs_overworld_rain");
                List<String> needsRains = (needsRain instanceof List) ? (List<String>) needsRain : Collections.singletonList(needsRain.toString());
                needsRains.add(newNeedsRain);
                attributesMap.put("needsRain", needsRains);
            }
        }
        if (details.get("min_affection") != null) {
            if (attributesMap.get("minAffection") == null) {
                attributesMap.put("minAffection", Collections.singletonList(details.get("min_affection")));
            } else {
                var minAffection = attributesMap.get("minAffection");
                String newMinAffection = (String) details.get("min_affection");
                List<String> minAffections = (minAffection instanceof List) ? (List<String>) minAffection : Collections.singletonList(minAffection.toString());
                minAffections.add(newMinAffection);
                attributesMap.put("minAffection", minAffections);
            }
        }
        if (details.get("min_beauty") != null) {
            if (attributesMap.get("minBeauty") == null) {
                attributesMap.put("minBeauty", Collections.singletonList(details.get("min_beauty")));
            } else {
                var minBeauty = attributesMap.get("minBeauty");
                String newMinBeauty = (String) details.get("min_beauty");
                List<String> minBeauties = (minBeauty instanceof List) ? (List<String>) minBeauty : Collections.singletonList(minBeauty.toString());
                minBeauties.add(newMinBeauty);
                attributesMap.put("minBeauty", minBeauties);
            }
        }
        if (details.get("known_move") != null) {
            map = ((Map<String,Object>)details.get("known_move"));
            if (attributesMap.get("knownMove") == null) {
                attributesMap.put("knownMove", (map != null) ? Collections.singletonList(map.get("name")) : null );
            } else {
                var knownMove = attributesMap.get("knownMove");
                String newKnownMove = (map != null) ? (String) map.get("name") : "";
                List<String> knownMoves = (knownMove instanceof List) ? (List<String>) knownMove : Collections.singletonList(knownMove.toString());
                knownMoves.add(newKnownMove);
                attributesMap.put("knownMove", knownMoves);
            }
        }
        if (details.get("known_move_type") != null) {
            map = ((Map<String,Object>)details.get("known_move"));
            if (attributesMap.get("knownMoveType") == null) {
                attributesMap.put("knownMoveType", (map != null) ? Collections.singletonList(map.get("name")) : null);
            } else {
                var knownMoveType = attributesMap.get("knownMoveType");
                String newKnownMoveType = (map != null) ? (String) map.get("name") : "";
                List<String> knownMoveTypes = (knownMoveType instanceof List) ? (List<String>) knownMoveType : Collections.singletonList(knownMoveType.toString());
                knownMoveTypes.add(newKnownMoveType);
                attributesMap.put("knownMoveType", knownMoveTypes);
            }
        }
        if (details.get("party_species") != null) {
            if (attributesMap.get("partySpecies") == null) {
                attributesMap.put("partySpecies", Collections.singletonList(details.get("party_species")));
            } else {
                var partySpecies = attributesMap.get("partySpecies");
                String newPartySpecies = (String) details.get("party_species");
                List<String> partySpeciesList = (partySpecies instanceof List) ? (List<String>) partySpecies : Collections.singletonList(partySpecies.toString());
                partySpeciesList.add(newPartySpecies);
                attributesMap.put("partySpecies", partySpeciesList);
            }
        }
        if (details.get("relative_physical_stats") != null) {
            if (attributesMap.get("relativePhysicalStats") == null) {
                attributesMap.put("relativePhysicalStats", Collections.singletonList(details.get("relative_physical_stats")));
            } else {
                var relPhysicalStat = attributesMap.get("relativePhysicalStats");
                String newRelPhysicalStat = (String) details.get("relative_physical_stats");
                List<String> relPhysicalStats = (relPhysicalStat instanceof List) ? (List<String>) relPhysicalStat : Collections.singletonList(relPhysicalStat.toString());
                relPhysicalStats.add(newRelPhysicalStat);
                attributesMap.put("relativePhysicalStats", relPhysicalStats);
            }
        }
        if (details.get("trade_species") != null) {
            if (attributesMap.get("tradeSpecies") == null) {
                attributesMap.put("tradeSpecies", Collections.singletonList(details.get("trade_species")));
            } else {
                var tradeSpecies = attributesMap.get("tradeSpecies");
                String newTradeSpecies = (String) details.get("trade_species");
                List<String> tradeSpeciesList = (tradeSpecies instanceof List) ? (List<String>) tradeSpecies : Collections.singletonList(tradeSpecies.toString());
                tradeSpeciesList.add(newTradeSpecies);
                attributesMap.put("tradeSpecies", tradeSpeciesList);
            }
        }
        if (details.get("turn_upside_down") != null) {
            if (attributesMap.get("turnUpsideDown") == null) {
                attributesMap.put("turnUpsideDown", Collections.singletonList(details.get("turn_upside_down")));
            } else {
                var turnUpsideDown = attributesMap.get("turnUpsideDown");
                String newTurnUpsideDown = (String) details.get("turn_upside_down");
                List<String> turnUpsideDownList = (turnUpsideDown instanceof List) ? (List<String>) turnUpsideDown : Collections.singletonList(turnUpsideDown.toString());
                turnUpsideDownList.add(newTurnUpsideDown);
                attributesMap.put("turnUpsideDown", turnUpsideDownList);
            }
        }
        this.pokemonIdAndAttributesMap.put(Integer.parseInt((String) details.get("id")), attributesMap);
    }

    // clean up map, remove unnecessary duplicates
    private void cleanupAttributesMap() {
        logger.info("All attributes maps created: {}", pokemonIdAndAttributesMap.size());
        pokemonIdAndAttributesMap.forEach((key, mapValue) -> {
            logger.info("id {}, map {}", key, mapValue);
            List<BigInteger> happyValues = Arrays.asList((BigInteger)mapValue.get("minHappiness"));
            if (happyValues != null && happyValues.size() > 1) {
                HashSet<BigInteger> happySet = new HashSet<>();
                for(BigInteger happy : happyValues) {
                    if ( null != happy && !happySet.contains(happy)) {
                        happySet.add(happy);
                        logger.debug("adding {} to happy set", happy);
                    }
                }
                mapValue.put("minHappiness", happySet);
            }
            logger.info("minHappiness: {}", mapValue.get("minHappiness"));

            List<BigInteger> affectionValues = Arrays.asList((BigInteger)mapValue.get("minAffection"));
            if (affectionValues != null && affectionValues.size() > 1) {
                HashSet<BigInteger> minAffectionSet = new HashSet<>();
                for(BigInteger affection : affectionValues) {
                    if ( null != affection && !minAffectionSet.contains(affection)) {
                        minAffectionSet.add(affection);
                        logger.debug("adding {} to affection set", affection);
                    }
                }
                mapValue.put("minAffection", minAffectionSet);
            }
            logger.info("minAffection: {}", mapValue.get("minAffection"));

            List<BigInteger> beautyValues = Arrays.asList((BigInteger)mapValue.get("minBeauty"));
            if (beautyValues != null && beautyValues.size() > 1) {
                HashSet<BigInteger> minBeautySet = new HashSet<>();
                for(BigInteger beauty : beautyValues) {
                    if (null != beauty && !minBeautySet.contains(beauty)) {
                        minBeautySet.add(beauty);
                        logger.debug("adding {} to beauty set", beauty);
                    }
                }
                mapValue.put("minBeauty", minBeautySet);
            }
            logger.info("minBeauty: {}", mapValue.get("minBeauty"));

            List<String> knownMovesValues = Arrays.asList((String)mapValue.get("knownMove"));
            if (knownMovesValues != null && knownMovesValues.size() > 1) {
                HashSet<String> knownMovesSet = new HashSet<>();
                for(String move : knownMovesValues) {
                    if (null != move && !knownMovesSet.contains(move)) {
                        knownMovesSet.add(move);
                        logger.debug("adding {} to knownMoves set", move);
                    }
                }
                mapValue.put("knownMove", knownMovesSet);
            }
            logger.info("knownMove: {}", mapValue.get("knownMove"));

            List<String> knownMovesTypeValues = Arrays.asList((String)mapValue.get("knownMoveType"));
            if (knownMovesTypeValues != null && knownMovesTypeValues.size() > 1) {
                HashSet<String> knownMovesTypeSet = new HashSet<>();
                for(String moveType : knownMovesTypeValues) {
                    if (null != moveType && !knownMovesTypeSet.contains(moveType)) {
                        knownMovesTypeSet.add(moveType);
                        logger.debug("adding {} to knownMovesType set", moveType);
                    }
                }
                mapValue.put("knownMoveType", knownMovesTypeSet);
            }
            logger.info("knownMoveType: {}", mapValue.get("knownMoveType"));

            List<Boolean> needsRainValues = Arrays.asList((Boolean)mapValue.get("needsRain"));
            if (needsRainValues != null && needsRainValues.size() > 1) {
                HashSet<Boolean> needsRainSet = new HashSet<>();
                for(Boolean needsRain : needsRainValues) {
                    if (!needsRainSet.contains(needsRain)) {
                        needsRainSet.add(needsRain);
                        logger.debug("adding {} to needsRain set", needsRain);
                    }
                }
                mapValue.put("needsRain", needsRainSet);
            }
            logger.info("needsRain: {}", mapValue.get("needsRain"));

            List<Boolean> turnUpsideDownValues = Arrays.asList((Boolean)mapValue.get("turnUpsideDown"));
            if (turnUpsideDownValues != null && turnUpsideDownValues.size() > 1) {
                HashSet<Boolean> upsideDownSet = new HashSet<>();
                for(Boolean upsideDown : turnUpsideDownValues) {
                    if (!upsideDownSet.contains(upsideDown)) {
                        upsideDownSet.add(upsideDown);
                        logger.debug("adding {} to needsRain set", upsideDown);
                    }
                }
                mapValue.put("needsRain", upsideDownSet);
            }
            logger.info("turnUpsideDown: {}", mapValue.get("turnUpsideDown"));
        });
        logger.info("attribute map cleaned up");
    }

    private void setupBooleans(Map<String,Object> pokemonAttributesMap) {
        this.hasMinimumLevel = pokemonAttributesMap.get("minLevel") != null;
        this.hasHeldItem = pokemonAttributesMap.get("heldItem") != null;
        this.hasUseItem = pokemonAttributesMap.get("useItem") != null;
        this.isABaby = pokemonAttributesMap.get("isBaby") != null && (Boolean)pokemonAttributesMap.get("isBaby") != false;
        this.hasMinimumHappiness = pokemonAttributesMap.get("minHappiness") != null;
        this.hasDayNight = pokemonAttributesMap.get("timeOfDay") != null;
        this.hasLocations = pokemonAttributesMap.get("location") != null;
        this.hasMinimumAffection = pokemonAttributesMap.get("minAffection") != null;
        this.hasBeauty = pokemonAttributesMap.get("minBeauty") != null;
        this.hasKnownMoves = pokemonAttributesMap.get("knownMove") != null;
        this.hasKnownMoveType = pokemonAttributesMap.get("knownMoveType") != null;
        this.hasNeedsRain = pokemonAttributesMap.get("needsRain") != null && (Boolean)pokemonAttributesMap.get("needsRain") != false;
        this.hasTurnUpsideDown = pokemonAttributesMap.get("turnUpsideDown") != null && (Boolean)pokemonAttributesMap.get("turnUpsideDown") != false;
    }

    private boolean pokemonEvolves() {
        return hasMinimumLevel ||
               isABaby ||
               hasUseItem ||
               hasHeldItem ||
               hasMinimumHappiness ||
               hasBeauty ||
               hasMinimumAffection ||
               hasDayNight ||
               hasKnownMoves ||
               hasNeedsRain;
    }
}
