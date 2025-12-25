package pokedex.controllers;

import pokedex.service.PokemonService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;
import skaro.pokeapi.client.PokeApiClient;
import skaro.pokeapi.resource.pokemon.Pokemon;
import skaro.pokeapi.resource.pokemonspecies.PokemonSpecies;

import java.util.Random;

@Controller
public class PokedexController extends BaseController
{
    /* Logging instance */
    private static final Logger logger = LogManager.getLogger(PokedexController.class);

    @Autowired
    public PokedexController(PokemonService pokemonService, PokeApiClient pokeApiClient)
    {
        super(pokemonService, pokeApiClient);
    }

    @GetMapping(value="/pokedex/{pokemonId}")
    public ModelAndView pokedex(@PathVariable(name="pokemonId") String nameOrId, ModelAndView mav)
    {
        Pokemon pokemon = setupPokedex(nameOrId);
        mav.addObject("pokemonId", super.pokemonId);
        mav.addObject("defaultImage", pokemon.defaultImage());
        mav.addObject("officialImage", pokemon.officialImage());
        mav.addObject("gifImage", pokemon.gifImage());
        mav.addObject("shinyImage", pokemon.shinyImage());
        mav.addObject("pokemon", pokemon);
        mav.addObject("randomDescriptionNumber", !pokemon.descriptions().isEmpty() ?
                new Random().nextInt(pokemon.descriptions().size()) : 0);
        mav.setViewName("pokedex");
        return mav;
    }

    public Pokemon setupPokedex(String nameOrId)
    {
        logger.info("loading pokedex info for {}", nameOrId);
        skaro.pokeapi.resource.pokemon.Pokemon pokemonResource = pokemonService.getPokemonByName(nameOrId.toLowerCase());
        PokemonSpecies speciesData;
        Pokemon pokemon = null;
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
