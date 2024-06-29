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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static java.util.stream.Collectors.toList;

@Controller
public class PokedexController {

    private static final Logger logger = LogManager.getLogger(PokedexController.class);
    Map<String, String> sprites;
    String defaultImage;
    String officialImage;
    String gifImage;
    String shinyImage;
    String pokemonName = "";
    Integer pokemonId;
    Integer pokemonHeight;
    Integer pokemonWeight;
    String pokemonColor = "";
    String pokemonType = "";
    List<FlavorText> pokemonDescriptions = Collections.emptyList();
    String pokemonDescription = "";
    String pokemonLocation; // url
    List<String> pokemonLocations = Collections.emptyList();
    List<String> pokemonMoves = Collections.emptyList();
    boolean descriptionDiv = true,
            locationsDiv = false,
            movesDiv = false,
            evolutionsDiv = false,
//    normal = 'normal'
//    bold = 'bold'
//    screenWidth: number = 0
//    screenHeight: number = 0
            styleFlag = false,
            showGifs = false;

    PokemonService pokemonService;

    @Autowired
    public PokedexController(PokemonService pokemonService) {
        this.pokemonService = pokemonService;

    }

    @GetMapping(value="/pokedex/{pokemonId}")
    public ModelAndView pokedex(@PathVariable(name="pokemonId") int pokemonId, ModelAndView mav) {
        skaro.pokeapi.resource.pokemon.Pokemon pokemonResource = pokemonService.getPokemonByName(String.valueOf(pokemonId));
        com.example.pokedex.entities.Pokemon pokemon = new com.example.pokedex.entities.Pokemon(pokemonResource);
        pokemonName = pokemon.getName();
        //sprites = pokemon.getSprites();
        this.pokemonId = pokemon.getId();
        defaultImage = null != pokemon.getSprites().getFrontDefault()
                ? pokemon.getSprites().getFrontDefault() : "/images/pokeball1.jpg";
        officialImage = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/"+pokemon.getId()+".png";
        gifImage = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/versions/generation-v/black-white/animated/"+pokemon.getId()+".gif";
        shinyImage = pokemon.getSprites().getFrontShiny();
        sprites = new TreeMap<>() {{
           put("default", defaultImage);
           put("official", officialImage);
           put("shiny", shinyImage);
           put("gif", gifImage);
        }};
        pokemonWeight = pokemon.getWeight();
        pokemonHeight = pokemon.getHeight();
        PokemonSpecies speciesData = pokemonService.getPokemonSpeciesData(String.valueOf(pokemonId));
        pokemonColor = speciesData.getColor().getName();
        pokemonDescriptions = speciesData.getFlavorTextEntries();
        pokemonDescription = pokemonDescriptions.stream()
                .filter(flavorText -> "en".equals(flavorText.getLanguage().getName()))
                .findFirst().get().getFlavorText();
        List<PokemonType> types = pokemon.getTypes();
        if (types.size() > 1) {
            //.info("More than 1 pokemonType");
            pokemonType = types.get(0).getType().getName().substring(0,1).toUpperCase() + types.get(0).getType().getName().substring(1)
                    + " & " + types.get(1).getType().getName().substring(0,1).toUpperCase() + types.get(1).getType().getName().substring(1);
        } else {
            //logger.info("One pokemonType");
            pokemonType = types.get(0).getType().getName().substring(0,1).toUpperCase() + types.get(0).getType().getName().substring(1);
        }
        pokemonLocation = pokemon.getLocationAreaEncounters();
        pokemonLocations = pokemonService.getPokemonLocationEncounters(pokemonLocation);

        pokemonMoves = pokemon.getMoves().stream()
                .map(skaro.pokeapi.resource.pokemon.PokemonMove::getMove)
                .map(NamedApiResource::getName)
                .sorted()
                .toList();

        mav.addObject("pkmnName", pokemonName);
        mav.addObject("sprites", sprites);
        mav.addObject("pkmnId", this.pokemonId);
        mav.addObject("defaultImage", defaultImage);
        mav.addObject("officialImage", officialImage);
        mav.addObject("gifImage", gifImage);
        mav.addObject("shinyImage", shinyImage);
        mav.addObject("weight", pokemonWeight);
        mav.addObject("height", pokemonHeight);
        mav.addObject("color", pokemonColor);
        mav.addObject("descriptions", pokemonDescriptions);
        mav.addObject("description", pokemonDescription);
        mav.addObject("pkmnType", pokemonType);
        mav.addObject("pkmnLocations", pokemonLocations);
        mav.addObject("pkmnLocation", pokemonLocation);
        mav.addObject("pkmnMoves", pokemonMoves);
        mav.addObject("pokemon", pokemon);
        mav.setViewName("pokedex");
        return mav;
    }
}
