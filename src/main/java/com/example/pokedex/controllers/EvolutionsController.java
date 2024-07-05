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

import java.util.*;

@Controller
public class EvolutionsController extends BaseController {

    private static final Logger logger = LogManager.getLogger(EvolutionsController.class);
    Map<Integer,List<List<Integer>>> pokemonIDToEvolutionChainMap;
    Integer pokemonChainID;
    List<List<Integer>> pokemonFamilyIDs;
    List<Integer> allIDs;
    List<List<Pokemon>> pokemonFamily;
    List<Integer> pokemonFamilyAltLevels;
    Map<String,Object> specificAttributesMap;
    Integer pokemonFamilySize;
    List<Integer> stages;
    Integer stage = 0;
    Integer counter = 0;

    @Autowired
    public EvolutionsController(PokemonService pokemonService) {
        super(pokemonService);
        resetEvolutionParameters();
    }

    @GetMapping(value="/evolutions/{pokemonId}")
    public ModelAndView getEvolutions(@PathVariable(name="pokemonId") String pokemonId, ModelAndView mav) {
        resetEvolutionParameters();
        this.pokemonId = pokemonId;
        this.pokemonChainID = getEvolutionChainID(pokemonIDToEvolutionChainMap, pokemonId);
        setupEvolutions();
        mav.addObject("pokemonId", pokemonId);
        mav.addObject("stages", stages);
        mav.addObject("pokemonFamily", pokemonFamily);
        mav.addObject("allIDs", allIDs);
        mav.setViewName("evolutions");
        return mav;
    }

    private void resetEvolutionParameters() {
        pokemonIDToEvolutionChainMap = this.pokemonService.getEvolutionsMap();
        specificAttributesMap = generateDefaultAttributesMap();
        if (null != pokemonFamily) pokemonFamily.clear();
        this.pokemonFamilySize = 0;
        this.pokemonChainID = 0;
        if (null != pokemonFamilyAltLevels) pokemonFamilyAltLevels.clear();
        if (null != allIDs) allIDs = new ArrayList<>();
        if (null != stages) stages.clear();
        stage = 0;
    }

    private void setupEvolutions() {
        pokemonFamilyIDs = pokemonIDToEvolutionChainMap.get(pokemonChainID);
        if (pokemonFamilyIDs != null && pokemonFamilyIDs.size() != 1 ) {
            setFamilySize();
            setStages();
            setAllIDs();
            pokemonFamily = new ArrayList<>();
            pokemonFamilyIDs.forEach(this::createListOfPokemonForIDList);
        } else {
            pokemonFamilySize = 0;
            stages = null;
            allIDs = null;
            pokemonFamily = null;
        }
    }

    private void setFamilySize() {
        pokemonFamilySize = pokemonFamilyIDs.stream().flatMap(Collection::stream).toList().size();
        logger.info("familySize:{}", pokemonFamilySize);
    }

    private void setStages() {
        stages = new ArrayList<>();
        pokemonFamilyIDs.forEach(idList -> stages.add(++stage));
        logger.info("stages:{}", stages.size());
    }

    private void setAllIDs() {
        allIDs = pokemonFamilyIDs.stream()
                .flatMap(Collection::stream)
                .sorted()
                .toList();
        logger.info("allIDs:{}", allIDs);
    }

    public void createListOfPokemonForIDList(List<Integer> idList) {
        logger.info("idList: {}, size: {}", idList, idList.size());
        List<Pokemon> pokemonList = new ArrayList<>();
        String previousId = "";
        for (Integer id : idList) {
            logger.info("id:{}", id);
            skaro.pokeapi.resource.pokemon.Pokemon pokemonResponse = pokemonService.getPokemonByName(String.valueOf(id));
            PokemonSpecies speciesData = null;
            try {
                speciesData = pokemonService.getPokemonSpeciesData(String.valueOf(pokemonResponse.getId()));
                if (null != speciesData) previousId = String.valueOf(id);
            } catch (Exception e) {
                logger.warn("No species data found for {}. Using previousId {}", pokemonResponse.getId(), previousId);
                try { speciesData = pokemonService.getPokemonSpeciesData(previousId); }
                catch (Exception e2) { logger.error("No species data found using previousId {}", previousId); }
            }
            assert speciesData != null;
            Pokemon pokemon = createPokemon(pokemonResponse, speciesData);
            pokemonList.add(pokemon);
            logger.info("pokemon added to familyList: {} length is {}", pokemon, pokemonList.size());
        }
        pokemonList = pokemonList.stream().sorted().toList();
        pokemonFamily.add(pokemonList);
    }

}
