package com.example.pokedex.controllers;

import com.example.pokedex.service.PokemonService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;
import skaro.pokeapi.resource.FlavorText;
import skaro.pokeapi.resource.NamedApiResource;
import skaro.pokeapi.resource.location.Location;
import skaro.pokeapi.resource.locationarea.LocationArea;
import skaro.pokeapi.resource.pokemon.PokemonSprites;
import skaro.pokeapi.resource.pokemon.PokemonType;
import skaro.pokeapi.resource.pokemonspecies.PokemonSpecies;

import java.util.*;

import static java.util.stream.Collectors.toList;

@Controller
public class PokedexController extends BaseController {

    private static final Logger logger = LogManager.getLogger(PokedexController.class);
    Map<String, String> sprites;
    String defaultImage;
    String officialImage;
    String gifImage;
    String shinyImage;
    String pokemonName = "";
    //Integer pokemonId;
    Integer pokemonHeight;
    Integer pokemonWeight;
    String pokemonColor = "";
    String pokemonType = "";
    List<FlavorText> pokemonDescriptions = new ArrayList<>();
    String pokemonDescription = "";
    String pokemonLocation; // url
    List<String> pokemonLocations = new ArrayList<>();
    List<String> pokemonMoves = new ArrayList<>();
    boolean
//            descriptionDiv = true,
//            locationsDiv = false,
//            movesDiv = false,
//            evolutionsDiv = false,
//    normal = 'normal'
//    bold = 'bold'
//    screenWidth: number = 0
//    screenHeight: number = 0
            styleFlag = false,
            showGifs = false;

    PokemonService pokemonService;

    @Autowired
    public PokedexController(PokemonService pokemonService) {
        super(pokemonService);
        this.pokemonService = pokemonService;

    }

    @GetMapping(value="/pokedex/{pokemonId}")
    public ModelAndView pokedex(@PathVariable(name="pokemonId") String nameOrId, ModelAndView mav) {
        logger.info("loading pokedex info for {}", nameOrId);
        skaro.pokeapi.resource.pokemon.Pokemon pokemonResource = pokemonService.getPokemonByName(nameOrId.toLowerCase());
        com.example.pokedex.entities.Pokemon pokemon = createPokemon(pokemonResource, pokemonService.getPokemonSpeciesData(String.valueOf(pokemonResource.getId())));
        sprites = new TreeMap<>() {{
           put("default", pokemon.getDefaultImage());
           put("official", pokemon.getOfficialImage());
           put("shiny", pokemon.getShinyImage());
           put("gif", pokemon.getGifImage());
        }};
        super.pokemonId = pokemon.getId();

        mav.addObject("sprites", sprites);
        mav.addObject("pokemonId", super.pokemonId);
        mav.addObject("defaultImage", sprites.get("default"));
        mav.addObject("officialImage", sprites.get("official"));
        mav.addObject("gifImage", sprites.get("gif"));
        mav.addObject("shinyImage", sprites.get("shiny"));
        mav.addObject("pokemon", pokemon);
        mav.setViewName("pokedex");
        return mav;
    }

}
