package com.example.pokedex.controllers;

import com.example.pokedex.entities.Pokemon;
import com.example.pokedex.service.PokemonService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;
import skaro.pokeapi.resource.pokemonspecies.PokemonSpecies;

import java.net.http.HttpResponse;

@Controller
public class PokedexController extends BaseController {

    private static final Logger logger = LogManager.getLogger(PokedexController.class);

    @Autowired
    public PokedexController(PokemonService pokemonService) {
        super(pokemonService);
    }

    @GetMapping(value="/pokedex/{pokemonId}")
    public ModelAndView pokedex(@PathVariable(name="pokemonId") String nameOrId, ModelAndView mav) {
        Pokemon pokemon = setupPokedex(nameOrId);
        mav.addObject("pokemonId", super.pokemonId);
        mav.addObject("defaultImage", pokemon.getDefaultImage());
        mav.addObject("officialImage", pokemon.getOfficialImage());
        mav.addObject("gifImage", pokemon.getGifImage());
        mav.addObject("shinyImage", pokemon.getShinyImage());
        mav.addObject("pokemon", pokemon);
        mav.setViewName("pokedex");
        return mav;
    }

    public Pokemon setupPokedex(String nameOrId) {
        logger.info("loading pokedex info for {}", nameOrId);
        skaro.pokeapi.resource.pokemon.Pokemon pokemonResource = pokemonService.getPokemonByName(nameOrId.toLowerCase());
        PokemonSpecies speciesData;
        com.example.pokedex.entities.Pokemon pokemon = null;
        try {
            speciesData = pokemonService.getPokemonSpeciesData(String.valueOf(pokemonResource.getId()));
            pokemon = createPokemon(pokemonResource, speciesData);
            super.pokemonId = pokemon.getId().toString();
        } catch (Exception e) {
            logger.error("No species data found using {}", pokemonResource.getId());
        }
        return pokemon;
    }

}
