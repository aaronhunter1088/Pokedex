package com.example.pokedex.controllers;

import com.example.pokedex.service.PokemonService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.tags.shaded.org.apache.xpath.operations.Bool;
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

    Map<Integer, Map<String, Object>> pokemonIdAndAttributesMap;
    Map<String, Object> specificAttributesMap = Collections.emptyMap();
    Map<Integer, List<List<Integer>>> pokemonIDToEvolutionChainMap;
    Integer pokemonChainID = 0;
    List<Integer> allIDs = new ArrayList<>();
    List<List<Integer>> family = new ArrayList<>();
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
            hasTurnUpsideDown = false,
            emptyChain = true;

    @Autowired
    public EvolvesHowController(PokemonService pokemonService) {
        super(pokemonService);
    }

    @GetMapping(value="/evolves-how")
    public ModelAndView evolvesHow(@RequestParam(name="pokemonId") int pokemonId, ModelAndView mav) {
        logger.info("pokemonId: {}", pokemonId);
        this.pokemonId = String.valueOf(pokemonId);
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
        this.pokemonIdAndAttributesMap = new HashMap<>();
        this.specificAttributesMap = generateDefaultAttributesMap();
        this.pokemonChainID = getEvolutionChainID(pokemonIDToEvolutionChainMap, String.valueOf(pokemonId));
        this.family = this.pokemonIDToEvolutionChainMap.get(this.pokemonChainID);
        logger.info("family: {}", family);
        this.allIDs = family.stream().flatMap(Collection::stream).toList();
        logger.info("allIDs: {}", allIDs);
        if (pokemonChainID != 0) {
            Map<String, Object> chainResponse = pokemonService.getPokemonChainData(String.valueOf(pokemonChainID));
            logger.info("chainResponse: {}", chainResponse);
            if (!chainResponse.isEmpty()) {
                emptyChain = false;
                getEvolutionDetails((LinkedHashMap<String, Object>) chainResponse.get("chain"));
                for(Integer id : allIDs) {
                    logger.info("checking map for existence: {}", id);
                    if (!this.pokemonIdAndAttributesMap.containsKey(id)) {
                        logger.info("{} not found! populating with default attrMap", id);
                        this.pokemonIdAndAttributesMap.put(id, this.generateDefaultAttributesMap());
                    }
                }
                cleanupAttributesMap();
                logger.info("Attributes map cleaned up in Evolves-how");
                setupBooleans(pokemonIdAndAttributesMap.get(Integer.valueOf(pokemonId)));
                doesPokemonEvolve = pokemonEvolves();
                logger.info("does pokemon evolve: {}", doesPokemonEvolve);
            }
        }
    }

    private void getEvolutionDetails(Map<String, Object> chain) {
        logger.info("chain: {}", chain);
        String name = (String) ((Map<String,Object>)chain.get("species")).get("name");
        String pkmnId = ((String) ((Map<String,Object>)chain.get("species")).get("url")).split("/")[6];
        Map<String, Object> evolutionDetails;
        logger.info("name: {}, id: {}", name, pkmnId);
        for (int i=0; i<((List<Object>)chain.get("evolves_to")).size(); i++) {
            Map<String,Object> evolvesTo = (Map<String,Object>) ((List<Object>) chain.get("evolves_to")).get(i);
            try {
                for (int j = 0; j < ((List<Object>) evolvesTo.get("evolution_details")).size(); j++) {
                    evolutionDetails = (Map<String, Object>) ((List<Object>) evolvesTo.get("evolution_details")).get(j);
                    evolutionDetails.put("is_baby", chain.get("is_baby"));
                    evolutionDetails.put("id", pkmnId);
                    evolutionDetails.put("name", name);
                    if (!pokemonIdAndAttributesMap.containsKey(Integer.parseInt(pkmnId))) {
                        setAttributesMap(evolutionDetails);
                    } else {
                        updateAttributesMap(evolutionDetails, pokemonIdAndAttributesMap.get(Integer.parseInt(pkmnId)));
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
                        evolutionDetails = generateDefaultAttributesMap();
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
        this.specificAttributesMap = new TreeMap<>();
        this.specificAttributesMap.put("name", details.get("name"));
        this.specificAttributesMap.put("id", details.get("id"));
        this.specificAttributesMap.put("gender", null != details.get("gender") ? details.get("gender") : null);
        this.specificAttributesMap.put("isBaby", details.get("is_baby"));
        Map<?,?> map = ((Map<?, ?>)details.get("held_item"));
        this.specificAttributesMap.put("heldItem", (null != map) ? map.get("name") : null);
        map = ((Map<?,?>)details.get("item"));
        this.specificAttributesMap.put("useItem", (null != map) ? map.get("name") : null);
        this.specificAttributesMap.put("minHappiness", details.get("min_happiness"));
        this.specificAttributesMap.put("minLevel", details.get("min_level"));
        this.specificAttributesMap.put("timeOfDay", (details.get("time_of_day") != "") ? details.get("time_of_day") : null);
        map = ((Map<?,?>)details.get("location"));
        this.specificAttributesMap.put("location", (null != map) ? map.get("name") : null);
        this.specificAttributesMap.put("needsRain", details.get("needs_overworld_rain"));
        this.specificAttributesMap.put("minAffection", details.get("min_affection"));
        this.specificAttributesMap.put("minBeauty", details.get("min_beauty"));
        map = ((Map<?,?>)details.get("known_move"));
        this.specificAttributesMap.put("knownMove", (null != map) ? map.get("name") : null);
        map = ((Map<?,?>)details.get("known_move_type"));
        this.specificAttributesMap.put("knownMoveType", (null != map) ? map.get("name") : null);
        this.specificAttributesMap.put("partySpecies", details.get("party_species"));
        this.specificAttributesMap.put("relativePhysicalStats", details.get("relative_physical_stats"));
        this.specificAttributesMap.put("tradeSpecies", details.get("trade_species"));
        this.specificAttributesMap.put("turnUpsideDown", details.get("turn_upside_down"));

        logger.info("attrMap for: {} = {}", details.get("name"), this.specificAttributesMap);
        this.pokemonIdAndAttributesMap.put(Integer.valueOf((String) details.get("id")), this.specificAttributesMap);
    }

    public void updateAttributesMap(Map<String, Object> details, Map<String, Object> attributesMap) {
        logger.info("evolution_detailsUpdate for: {} {}", attributesMap.get("name"), details);
        Map<?,?> detailsMap = null;
        List<String> checkDetails = Arrays.asList("gender", "held_item", "item", "min_happiness",
                "time_of_day", "location", "needs_overworld_rain", "min_affection", "min_beauty",
                "known_move", "known_move_type", "party_species", "relative_physical_stats",
                "trade_species", "turn_upside_down");
        for(String detail : checkDetails) {
            if (details.get(detail) != null) {
                if (attributesMap.get(convertToCamelCase(detail)) == null) {
                    if (details.get(detail) instanceof Map) {
                        attributesMap.put(convertToCamelCase(detail), ((Map<?,?>)details.get(detail)).get("name"));
                    } else {
                        attributesMap.put(convertToCamelCase(detail),details.get(detail));
                    }
                } else {
                    var dtl = attributesMap.get(convertToCamelCase(detail));
                    Object newDetail = details.get(detail);
                    List<String> updateDetails = new ArrayList<>();
                    if (dtl instanceof String d) updateDetails.add(d);
                    else if (dtl instanceof List<?> d) {
                        @SuppressWarnings("unchecked")
                        List<String> detailsList = (List<String>)d;
                        updateDetails.addAll(detailsList);
                    }
                    updateDetails.add(String.valueOf(newDetail));
                    attributesMap.put(convertToCamelCase(detail), updateDetails);
                }
            }
        }
        this.pokemonIdAndAttributesMap.put(Integer.parseInt((String) details.get("id")), attributesMap);
    }

    /**
     * Used in updateAttributesMap to set the key
     * @param underscored the api key value
     * @return camelCase version of the api key value
     */
    private String convertToCamelCase(String underscored) {
        StringBuilder sb = new StringBuilder();
        if ("needs_overworld_rain".equals(underscored)) sb.append("needsRain");
        else {
            String[] strs = underscored.split("_");
            for (int i=0; i<strs.length; i++) {
                if (i==0) sb.append(strs[i]);
                else {
                    sb.append(strs[i].substring(0,1).toUpperCase());
                    sb.append(strs[i].substring(1));
                }
            }
        }
        return sb.toString();
    }

    // clean up map, remove unnecessary duplicates
    private void cleanupAttributesMap() {
        logger.info("All attributes maps created: {}", pokemonIdAndAttributesMap.size());
        pokemonIdAndAttributesMap.forEach((key, mapValue) -> {
            logger.info("id {}, map {}", key, mapValue);
            if (null != mapValue.get("timeOfDay") && mapValue.get("timeOfDay").equals("")) mapValue.put("timeOfDay", null);
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

            List<String> needsRainValues = (mapValue.get("needsRain") instanceof List) ? (List<String>) mapValue.get("needsRain") : Arrays.asList(String.valueOf(mapValue.get("needsRain")));
            if (needsRainValues != null) {
                HashSet<Boolean> needsRainSet = new HashSet<>();
                for(String needsRain : needsRainValues) {
                    if (!needsRainSet.contains(Boolean.valueOf(needsRain))) {
                        needsRainSet.add(Boolean.valueOf(needsRain));
                        logger.debug("adding {} to needsRain set", needsRain);
                    }
                }
                mapValue.put("needsRain", needsRainSet.stream().toList().get(0));
            }
            logger.info("needsRain: {}", mapValue.get("needsRain"));

            List<String> turnUpsideDownValues = (mapValue.get("turnUpsideDown") instanceof List) ? (List<String>) mapValue.get("turnUpsideDown") : Arrays.asList(String.valueOf(mapValue.get("turnUpsideDown")));
            if (turnUpsideDownValues != null) {
                Set<Boolean> upsideDownSet = new HashSet<>();
                for(String upsideDown : turnUpsideDownValues) {
                    if (!upsideDownSet.contains(Boolean.valueOf(upsideDown))) {
                        upsideDownSet.add(Boolean.valueOf(upsideDown));
                        logger.debug("adding {} to needsRain set", upsideDown);
                    }
                }
                mapValue.put("turnUpsideDown", upsideDownSet.stream().toList().get(0));
            }
            logger.info("turnUpsideDown: {}", mapValue.get("turnUpsideDown"));
        });
        logger.info("attribute map cleaned up");
    }

    private void setupBooleans(Map<String,Object> pokemonAttributesMap) {
        this.hasMinimumLevel = pokemonAttributesMap.get("minLevel") != null;
        this.hasHeldItem = pokemonAttributesMap.get("heldItem") != null;
        this.hasUseItem = pokemonAttributesMap.get("useItem") != null;
        this.isABaby = pokemonAttributesMap.get("isBaby") != null && (Boolean)pokemonAttributesMap.get("isBaby");
        this.hasMinimumHappiness = pokemonAttributesMap.get("minHappiness") != null;
        this.hasDayNight = pokemonAttributesMap.get("timeOfDay") != null;
        this.hasLocations = pokemonAttributesMap.get("location") != null;
        this.hasMinimumAffection = pokemonAttributesMap.get("minAffection") != null;
        this.hasBeauty = pokemonAttributesMap.get("minBeauty") != null;
        this.hasKnownMoves = pokemonAttributesMap.get("knownMove") != null;
        this.hasKnownMoveType = pokemonAttributesMap.get("knownMoveType") != null;
        this.hasNeedsRain = pokemonAttributesMap.get("needsRain") != null && (Boolean)pokemonAttributesMap.get("needsRain");
        this.hasTurnUpsideDown = pokemonAttributesMap.get("turnUpsideDown") != null && (Boolean)pokemonAttributesMap.get("turnUpsideDown");
    }

    /**
     * Determines is a Pokemon evolves based on the
     * properties from this particular Pokemon
     * @return true or false
     */
    private boolean pokemonEvolves() {
        return hasMinimumLevel ||
               hasHeldItem ||
               hasUseItem ||
               isABaby ||
               hasMinimumHappiness ||
               hasDayNight ||
               hasLocations ||
               hasMinimumAffection ||
               hasBeauty ||
               hasKnownMoves ||
               hasKnownMoveType ||
               hasNeedsRain ||
               hasTurnUpsideDown;
    }
}
