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
    public ModelAndView pokedex(@PathVariable(name="pokemonId") int pokemonId, ModelAndView mav) {
        skaro.pokeapi.resource.pokemon.Pokemon pokemonResource = pokemonService.getPokemonByName(String.valueOf(pokemonId));
        com.example.pokedex.entities.Pokemon pokemon = createPokemon(pokemonResource, pokemonService.getPokemonSpeciesData(String.valueOf(pokemonId)));
//        new com.example.pokedex.entities.Pokemon(pokemonResource);
//        pokemonName = pokemon.getName();
//        //sprites = pokemon.getSprites();
//        this.pokemonId = pokemon.getId();
//        defaultImage = null != pokemon.getSprites().getFrontDefault()
//                ? pokemon.getSprites().getFrontDefault() : "/images/pokeball1.jpg";
//        officialImage = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/"+pokemon.getId()+".png";
//        gifImage = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/versions/generation-v/black-white/animated/"+pokemon.getId()+".gif";
//        shinyImage = pokemon.getSprites().getFrontShiny();
        sprites = new TreeMap<>() {{
           put("default", pokemon.getDefaultImage());
           put("official", pokemon.getOfficialImage());
           put("shiny", pokemon.getShinyImage());
           put("gif", pokemon.getGifImage());
        }};
//        pokemonWeight = pokemon.getWeight();
//        pokemonHeight = pokemon.getHeight();
//        PokemonSpecies speciesData = pokemonService.getPokemonSpeciesData(String.valueOf(pokemonId));
//        pokemonColor = speciesData.getColor().getName();
//        pokemonDescriptions = speciesData.getFlavorTextEntries();
//        pokemonDescription = pokemonDescriptions.stream()
//                .filter(flavorText -> "en".equals(flavorText.getLanguage().getName()))
//                .findFirst().get().getFlavorText();
//        List<PokemonType> types = pokemon.getTypes();
//        if (types.size() > 1) {
//            //.info("More than 1 pokemonType");
//            pokemonType = types.get(0).getType().getName().substring(0,1).toUpperCase() + types.get(0).getType().getName().substring(1)
//                    + " & " + types.get(1).getType().getName().substring(0,1).toUpperCase() + types.get(1).getType().getName().substring(1);
//        } else {
//            //logger.info("One pokemonType");
//            pokemonType = types.get(0).getType().getName().substring(0,1).toUpperCase() + types.get(0).getType().getName().substring(1);
//        }
//        pokemonLocation = pokemon.getLocationAreaEncounters();
//        pokemonLocations = pokemonService.getPokemonLocationEncounters(pokemonLocation);
//
//        pokemonMoves = pokemon.getMoves().stream()
//                .map(skaro.pokeapi.resource.pokemon.PokemonMove::getMove)
//                .map(NamedApiResource::getName)
//                .sorted()
//                .toList();
        super.pokemonId = pokemonId;

        mav.addObject("pkmnName", pokemon.getName());
        mav.addObject("sprites", sprites);
        mav.addObject("pokemonId", super.pokemonId);
        mav.addObject("defaultImage", sprites.get("default"));
        mav.addObject("officialImage", sprites.get("official"));
        mav.addObject("gifImage", sprites.get("gif"));
        mav.addObject("shinyImage", sprites.get("shiny"));
        mav.addObject("weight", pokemon.getWeight());
        mav.addObject("height", pokemon.getHeight());
        mav.addObject("color", pokemon.getColor());
        mav.addObject("descriptions", pokemon.getDescriptions());
        mav.addObject("description", pokemon.getDescription());
        mav.addObject("pkmnType", pokemon.getType());
        mav.addObject("pkmnLocations", pokemon.getLocations());
        //mav.addObject("pkmnLocation", pokemonLocation);
        mav.addObject("pkmnMoves", pokemon.getPokemonMoves());
        mav.addObject("pokemon", pokemon);
        mav.setViewName("pokedex");
        return mav;
    }

}
