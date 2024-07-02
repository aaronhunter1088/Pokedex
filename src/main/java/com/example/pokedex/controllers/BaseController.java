package com.example.pokedex.controllers;

import com.example.pokedex.entities.Pokemon;
import com.example.pokedex.service.PokemonService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import skaro.pokeapi.resource.NamedApiResource;
import skaro.pokeapi.resource.pokemon.PokemonType;
import skaro.pokeapi.resource.pokemonspecies.PokemonSpecies;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Controller
public class BaseController {

    private static final Logger logger = LogManager.getLogger(BaseController.class);

    Integer pokemonId = 0;

    protected PokemonService pokemonService;

    @Autowired
    public BaseController(PokemonService pokemonService) {
        this.pokemonService = pokemonService;
    }

    protected Integer getEvolutionChainID(Map<Integer, List<List<Integer>>> pokemonIDToEvolutionChainMap, String pokemonId) {
        logger.info("id: {} chain: {}", pokemonId, pokemonIDToEvolutionChainMap);
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
        return keyToReturn;
    }

    protected Pokemon createPokemon(skaro.pokeapi.resource.pokemon.Pokemon pokemonResource, PokemonSpecies speciesData) {
        com.example.pokedex.entities.Pokemon pokemon = new com.example.pokedex.entities.Pokemon(pokemonResource);
        pokemon.setDefaultImage(null != pokemon.getSprites().getFrontDefault() ? pokemon.getSprites().getFrontDefault() : "/images/pokeball1.jpg");
        pokemon.setOfficialImage("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/"+pokemon.getId()+".png");
        pokemon.setGifImage("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/versions/generation-v/black-white/animated/"+pokemon.getId()+".gif");
        pokemon.setShinyImage(pokemon.getSprites().getFrontShiny());
        pokemon.setColor(speciesData.getColor().getName());
        pokemon.setDescriptions(speciesData.getFlavorTextEntries());
        pokemon.setDescription(pokemon.getDescriptions().stream()
                .filter(flavorText -> "en".equals(flavorText.getLanguage().getName()))
                .findFirst().get().getFlavorText());
        List<PokemonType> types = pokemon.getTypes();
        if (types.size() > 1) {
            //logger.info("More than 1 pokemonType");
            pokemon.setType(types.get(0).getType().getName().substring(0,1).toUpperCase() + types.get(0).getType().getName().substring(1)
                    + " & " + types.get(1).getType().getName().substring(0,1).toUpperCase() + types.get(1).getType().getName().substring(1));
        } else {
            //logger.info("One pokemonType");
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

    protected Map<String, Object> generateDefaultAttributesMap() {
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
}