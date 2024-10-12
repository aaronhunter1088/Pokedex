package com.pokedex.app.controllers;

import com.pokedex.app.entities.Pokemon;
import com.pokedex.app.service.PokemonService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import skaro.pokeapi.resource.FlavorText;
import skaro.pokeapi.resource.NamedApiResource;
import skaro.pokeapi.resource.pokemon.PokemonType;
import skaro.pokeapi.resource.pokemonspecies.PokemonSpecies;

import java.net.http.HttpResponse;
import java.util.*;

@Controller
public class BaseController {

    private static final Logger logger = LogManager.getLogger(BaseController.class);

    String pokemonId = "";
    int page = 1;
    int lastPageSearched = 1;
    int pkmnPerPage = 10;

    protected PokemonService pokemonService;

    @Autowired
    public BaseController(PokemonService pokemonService) {
        this.pokemonService = pokemonService;
    }

    protected Integer getEvolutionChainID(Map<Integer, List<List<Integer>>> pokemonIDToEvolutionChainMap, String pokemonId) {
        logger.info("id: {}", pokemonId);
        List<Integer> keys = pokemonIDToEvolutionChainMap.keySet().stream().toList();
        Integer keyToReturn = 0;
        keysLoop:
        for(Integer key: keys) {
            List<List<Integer>> pokemonIds = pokemonIDToEvolutionChainMap.get(key);
            for (List<Integer> chainIds : pokemonIds) {
                if (chainIds.contains(Integer.valueOf(pokemonId))) {
                    keyToReturn = key;
                    break keysLoop;
                }
            }
        }
        logger.info("chainKey: {}", keyToReturn);
        return keyToReturn;
    }

    /**
     * Returns a Pokemon with application specific properties
     * @param pokemonResource from pokeapi-reactor
     * @param speciesData from pokeapi-reactor
     * @return Pokemon object
     */
    protected Pokemon createPokemon(skaro.pokeapi.resource.pokemon.Pokemon pokemonResource, PokemonSpecies speciesData) {
        Pokemon pokemon = new Pokemon(pokemonResource);
        pokemon.setDefaultImage(null != pokemon.getSprites().getFrontDefault() ? pokemon.getSprites().getFrontDefault() : "/images/pokeball1.jpg");
        pokemon.setOfficialImage("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/"+pokemon.getId()+".png");
        setGifImage(pokemon);
        pokemon.setShinyImage(pokemon.getSprites().getFrontShiny());
        pokemon.setColor(speciesData.getColor().getName());
        pokemon.setDescriptions(speciesData.getFlavorTextEntries());
        List<FlavorText> pokemonDescriptions = pokemon.getDescriptions()
                .stream().filter(entry -> entry.getLanguage().getName().equals("en"))
                .toList();
        int randomEntry = new Random().nextInt(pokemonDescriptions.size());
        String description = pokemonDescriptions.get(randomEntry).getFlavorText().replace("\n", "");
        pokemon.setDescriptions(pokemonDescriptions);
        pokemon.setDescription(description);
        List<PokemonType> types = pokemon.getTypes();
        if (types.size() > 1) {
            logger.debug("More than 1 pokemonType");
            pokemon.setType(types.get(0).getType().getName().substring(0,1).toUpperCase() + types.get(0).getType().getName().substring(1)
                    + " & " + types.get(1).getType().getName().substring(0,1).toUpperCase() + types.get(1).getType().getName().substring(1));
        } else {
            logger.debug("One pokemonType");
            pokemon.setType(types.get(0).getType().getName().substring(0,1).toUpperCase() + types.get(0).getType().getName().substring(1));
        }
        String pokemonLocation = pokemon.getLocationAreaEncounters();
        pokemon.setLocations(pokemonService.getPokemonLocationEncounters(pokemonLocation));

        pokemon.setPokemonMoves(pokemon.getMoves().stream()
                .map(skaro.pokeapi.resource.pokemon.PokemonMove::getMove)
                .map(NamedApiResource::getName)
                .sorted()
                .toList());
        return pokemon;
    }

    private void setGifImage(Pokemon pokemon) {
        pokemon.setGifImage("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/versions/generation-v/black-white/animated/"+pokemon.getId()+".gif");
        HttpResponse<String> response = pokemonService.callUrl(pokemon.getGifImage());
        if (response.statusCode() == 404) pokemon.setGifImage(null);
    }

    protected Map<String, Object> generateDefaultAttributesMap() {
        return new TreeMap<>() {{
            put("name", null); // on screen
            put("gender", null);
            put("id", null);
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
            put("partyType", null); // not implemented
            put("trigger", null); // not implemented
            put("relativePhysicalStats", null);
            put("tradeSpecies", null);
            put("turnUpsideDown", null); // on screen
        }};
    }
}