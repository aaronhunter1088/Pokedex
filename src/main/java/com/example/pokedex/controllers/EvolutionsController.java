package com.example.pokedex.controllers;

import com.example.pokedex.entities.Pokemon;
import com.example.pokedex.service.PokemonService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import skaro.pokeapi.resource.pokemon.PokemonSprites;
import skaro.pokeapi.resource.pokemonspecies.PokemonSpecies;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Controller
public class EvolutionsController extends BaseController {

    private static final Logger logger = LogManager.getLogger(EvolutionsController.class);
    String pokemonID;
    Map<Integer,List<List<Integer>>> pokemonIDToEvolutionChainMap;
    Integer pokemonChainID;
    List<List<Integer>> pokemonFamilyIDs;
    List<Integer> allIDs;
    List<List<Pokemon>> pokemonFamily;
    List<Integer> pokemonFamilyAltLevels;
    Map<String,Object> pokemonIdAndAttributesMap;
    Map<String,Object> specificAttributesMap;
    Map pokemonMap;
    Integer pokemonFamilySize;
    Boolean defaultImagePresent = false,
            gifImagePresent = false,
            doesPokemonEvolve = false,
            isBabyPokemon = false;
    PokemonSprites sprites;
    List<Integer> stages;
    Integer stage = 0;
    Integer counter = 0;
    Integer itemCounter = 0;
    Integer attrCounter = 0;
    Integer babyCounter = 0;

    PokemonService pokemonService;

    @Autowired
    public EvolutionsController(PokemonService pokemonService) {
        super(pokemonService);
        this.pokemonService = pokemonService;
        pokemonIDToEvolutionChainMap = pokemonService.getEvolutionsMap();
        specificAttributesMap = this.generateDefaultAttributesMap();
        pokemonChainID = 0;
        pokemonFamilySize = 0;
        pokemonFamily = new ArrayList<List<Pokemon>>();
        stages = new ArrayList<Integer>();
        allIDs = new ArrayList<Integer>();
    }

    @GetMapping(value="/evolutions/{pokemonId}")
    public ModelAndView getEvolutions(@PathVariable(name="pokemonId") String pokemonId, ModelAndView mav) {
        resetEvolutionParameters();
        this.pokemonID = pokemonId;
        this.pokemonChainID = getEvolutionChainID(pokemonIDToEvolutionChainMap, pokemonId);
        setupEvolutions();
        mav.addObject("pokemonId", pokemonId);
        mav.addObject("stages", stages);
        mav.addObject("pokemonFamily", pokemonFamily);
        mav.addObject("allIDs", allIDs);
        mav.setViewName("evolutions");
        return mav;
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

    private void resetEvolutionParameters() {
        if (null != pokemonFamily) pokemonFamily.clear();
        this.pokemonFamilySize = 0;
        if (null != pokemonFamilyAltLevels) pokemonFamilyAltLevels.clear();
        if (null != allIDs) allIDs = new ArrayList<>();
        if (null != stages) stages.clear();
        stage = 0;
        counter = 0;
    }

    private void setupEvolutions() {
        List<List<Integer>> lists = pokemonIDToEvolutionChainMap.get(pokemonChainID);
        pokemonFamilyIDs = lists;
        setFamilySize();
        setStages();
        setAllIDs();
        createListOfPokemon();
    }

    private void setFamilySize() {
        pokemonFamilyIDs.forEach(idList -> {
            idList.forEach(id -> {
                pokemonFamilySize += 1;
            });
        });
        logger.info("familySize:{}", pokemonFamilySize);
    }

    private void setStages() {
        pokemonFamilyIDs.forEach(idList -> {
            stages.add(++stage);
        });
        logger.info("stages:{}", stages.size());
    }

    private void setAllIDs() {
        pokemonFamilyIDs.forEach(idList -> {
            allIDs.addAll(idList);
        });
        allIDs = allIDs.stream().sorted().toList();
        logger.info("allIDs:{}", allIDs);
    }

    private void createListOfPokemon() {
        pokemonFamilyIDs.forEach(this::createListOfPokemonForIDList);
    }

    public void createListOfPokemonForIDList(List<Integer> idList) {
        logger.info("IDList: {}, length: {}", idList, idList.size());
        List<Pokemon> pokemonList = new ArrayList<>();
        String previousId = "";
        for (Integer id : idList) {
            logger.info("id:{}", id);
            //pokemonList = new ArrayList<>();
            skaro.pokeapi.resource.pokemon.Pokemon pokemonResponse = pokemonService.getPokemonByName(String.valueOf(id));
            PokemonSpecies speciesData = null;
            try {
                speciesData = pokemonService.getPokemonSpeciesData(String.valueOf(pokemonResponse.getId()));
                if (null != speciesData) previousId = String.valueOf(id);
            } catch (Exception e) {
                logger.warn("No species data found for {}. Using previousId {}", pokemonResponse.getId(), previousId);
                speciesData = pokemonService.getPokemonSpeciesData(previousId);
                //previousId = String.valueOf(id);
            }
            Pokemon pokemon = createPokemon(pokemonResponse, speciesData);
            pokemonList.add(pokemon);
        }
        pokemonList = pokemonList.stream().sorted().toList();
        logger.info("adding list to familyList: {} length is {}", pokemonList, pokemonList.size());
        pokemonFamily.add(pokemonList);
    }

}
