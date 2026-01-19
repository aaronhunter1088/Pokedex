package pokedex.controllers;

import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import pokedexapi.service.PokemonApiService;
import skaro.pokeapi.client.PokeApiClient;
import skaro.pokeapi.resource.pokemon.Pokemon;
import skaro.pokeapi.resource.pokemonspecies.PokemonSpecies;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.Random;

@Controller
public class PokedexController extends BaseController
{
    /* Logging instance */
    private static final Logger logger = LogManager.getLogger(PokedexController.class);

    @Autowired
    public PokedexController(PokemonApiService pokemonService,
                             PokeApiClient pokeApiClient,
                             ObjectMapper objectMapper)
    {
        super(pokemonService, pokeApiClient, null, objectMapper);
    }

    @GetMapping(value = "/pokedex/{nameOrId}")
    public ModelAndView pokedex(@PathVariable(name = "nameOrId") String nameOrId, ModelAndView mav,
                                HttpSession httpSession,
                                @RequestParam(name = "darkmode", required = false, defaultValue = "false") String darkmode)
    {
        @SuppressWarnings("unchecked")
        Map<Integer, Pokemon> pokemonMap = (Map<Integer, Pokemon>) httpSession.getAttribute("pokemonMap");
        pokemonMap = updateSessionMap(pokemonMap);
        Pokemon pokemon = null;
        try { pokemon = pokemonMap.get(nameOrId); }
        catch (Exception _) {
            logger.warn("Couldn't find {} in pokemonMap", nameOrId);
            final var pokemonFound = pokemonMap.values().stream()
                    .anyMatch(pkmn -> pkmn.name().equalsIgnoreCase(nameOrId));
            if (pokemonFound) {
                pokemon = pokemonMap.values().stream()
                        .filter(pkmn -> pkmn.name().equalsIgnoreCase(nameOrId))
                        .findFirst()
                        .orElse(null);
            }
        }
        if (pokemon == null) pokemon = pokemonService.getPokemonByIdOrName(nameOrId);
        pokemon = setupPokedex(pokemon);
        mav.addObject("pokemonId", super.pokemonId);
        mav.addObject("defaultImage", pokemon.defaultImage());
        mav.addObject("officialImage", pokemon.officialImage());
        mav.addObject("gifImage", pokemon.gifImage());
        mav.addObject("shinyImage", pokemon.shinyImage());
        mav.addObject("pokemon", pokemon);
        mav.addObject("randomDescriptionNumber", !pokemon.descriptions().isEmpty() ?
                new Random().nextInt(pokemon.descriptions().size()) : 0);
        mav.addObject("isDarkMode", isDarkMode = darkmode.equals("true"));
        mav.setViewName("pokedex");
        return mav;
    }

    public Pokemon setupPokedex(Pokemon pokemon)
    {
        logger.info("Loading pokedex info for {}", pokemon.name());
        PokemonSpecies speciesData = null;
        try {
            speciesData = pokemonService.getPokemonSpeciesData(String.valueOf(pokemon.id()));
        }
        catch (Exception e) {
            logger.error("No species data found using {}", pokemon.id());
            logger.info("Trying with species: {}, url: {}", pokemon.species().name(), pokemon.species().url());
            Pokemon speciesPkmn = pokemonService.getPokemonByIdOrName(pokemon.species().name());
            speciesData = pokemonService.getPokemonSpeciesData(speciesPkmn.id().toString());
        }
        try {
            pokemon = createPokemon(pokemon, speciesData);
            super.pokemonId = pokemon.getId().toString();
        }
        catch (Exception e) {
            logger.error("An error occurred while creating Pokemon:{}", pokemon.id());
        }
        return pokemon;
    }

}
