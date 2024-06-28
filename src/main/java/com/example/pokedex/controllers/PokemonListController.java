package com.example.pokedex.controllers;

import com.example.pokedex.service.PokemonService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import skaro.pokeapi.resource.NamedApiResource;
import skaro.pokeapi.resource.pokemon.Pokemon;
import skaro.pokeapi.resource.pokemon.PokemonSprites;
import skaro.pokeapi.resource.pokemon.PokemonType;
import skaro.pokeapi.resource.pokemonspecies.PokemonSpecies;

import javax.ws.rs.PathParam;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static java.util.stream.Collectors.toList;

@Controller
public class PokemonListController {

    private static final Logger logger = LogManager.getLogger(PokemonListController.class);
    Map<Integer, com.example.pokedex.entities.Pokemon> pokemonMap = new TreeMap<>();
    int page = 1;
    String blankPageNumber = "";
    int pkmnPerPage = 10; // itemsPerPage
    int totalPokemon = 0;
    boolean defaultImagePresent = false,
            officialImagePresent = false,
            gifImagePresent = false,
            showGifs = false;

    PokemonService pokemonService;

    @Autowired
    public PokemonListController(PokemonService pokemonService) {
        this.pokemonService = pokemonService;
        this.page = pokemonService.getSavedPage();
        this.pkmnPerPage = pokemonService.getNumberOfPokemonPerPage(); // default is 10
        this.getPokemonMap();
    }

    @GetMapping("/")
    public ModelAndView homepage(ModelAndView mav) {
        mav.addObject("pokemonMap", getPokemonMap());
        mav.addObject("pokemonIds", new ArrayList<>(getPokemonMap().keySet()));
        mav.addObject("defaultImagePresent", defaultImagePresent);
        mav.addObject("officialImagePresent", officialImagePresent);
        mav.addObject("gifImagePresent", gifImagePresent);
        mav.addObject("showGifs", showGifs);
        mav.addObject("pkmnPerPage", pkmnPerPage);
        mav.addObject("totalPokemon", totalPokemon);
        mav.addObject("totalPages", (int)Math.ceil((double) totalPokemon /pkmnPerPage));
        mav.addObject("page", page);
        mav.setViewName("index");
        return mav;
    }

    public Map<Integer, com.example.pokedex.entities.Pokemon> getPokemonMap() {
        logger.info("page number is {}", page);
        logger.info("itemsPerPage: {}", pkmnPerPage);
        // @ts-ignore this.pkmnPerPage
        List<NamedApiResource<Pokemon>> pokemonList = pokemonService.getPokemonList(pkmnPerPage, (this.page - 1) * this.pkmnPerPage);
        if (null != pokemonList && !pokemonList.isEmpty()) {
            logger.info("pokemonList size: " + pokemonList.size());
            pokemonList = pokemonList.stream().limit(pkmnPerPage).collect(toList());
            logger.info("pokemonList limit size: " + pokemonList.size());
            pokemonMap = new TreeMap<>();
            totalPokemon = pokemonService.getTotalPokemon(null);
            pokemonList.forEach(pkmn -> {
                Pokemon pokemonResource = pokemonService.getPokemonByName(pkmn.getName());
                com.example.pokedex.entities.Pokemon pokemon = new com.example.pokedex.entities.Pokemon(pokemonResource);
                PokemonSprites sprites = pokemon.getSprites();
                List<PokemonType> types = pokemon.getTypes();
                String pokemonType = null;
                if (types.size() > 1) {
                    logger.info("More than 1 pokemonType");
                    pokemonType = types.get(0).getType().getName().substring(0,1).toUpperCase() + types.get(0).getType().getName().substring(1)
                            + " & " + types.get(1).getType().getName().substring(0,1).toUpperCase() + types.get(1).getType().getName().substring(1);
                } else {
                    logger.info("One pokemonType");
                    pokemonType = types.get(0).getType().getName().substring(0,1).toUpperCase() + types.get(0).getType().getName().substring(1);
                }
                pokemon.setType(pokemonType);
                pokemon.setDefaultImage(sprites.getFrontDefault());
                if (null != pokemon.getDefaultImage()) defaultImagePresent = true;
                pokemon.setOfficialImage("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/"+pokemon.getId()+".png");
                if (null != pokemon.getOfficialImage()) officialImagePresent = true;
                pokemon.setGifImage("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/versions/generation-v/black-white/animated/"+pokemon.getId()+".gif");
                if (null != pokemon.getGifImage()) gifImagePresent = true;
                PokemonSpecies speciesData = pokemonService.getPokemonSpeciesData(pokemon.getId().toString());
                if (null != speciesData) {
                    //logger.info("speciesData: {}", speciesData);
                    if (null != speciesData.getColor()) {
                        pokemon.setColor(speciesData.getColor().getName());
                    }
                }
                pokemonMap.put(pokemon.getId(), pokemon);
            });
        }
        blankPageNumber = "";
        return pokemonMap;
    }

    @GetMapping("/toggleGifs")
    @ResponseBody
    public Boolean toggleGifs(ModelAndView mav) {
        logger.info("showGifs: {}", !showGifs);
        showGifs = !showGifs;
        //homepage(mav);
        return showGifs;
    }

    @GetMapping("/{page}")
    public void page(@PathVariable(value="page") String pageNumber, ModelAndView mav) {
        logger.info("pagination page: {}", pageNumber);
        try {
            this.page = Integer.parseInt(pageNumber);
        } catch (NumberFormatException nfe) {
            logger.error("Couldn't update page because {}", nfe.getMessage());
        }
        homepage(mav);
    }

    @GetMapping("/pkmnPerPage")
    @ResponseBody
    public ResponseEntity<String> getPokemonPerPage(@RequestParam(name="pkmnPerPage", required=false, defaultValue="10") int pkmnPerPage) {
        if (pkmnPerPage <= 0) {
            return ResponseEntity.badRequest().body("Invalid number of Pokemon per page");
        }
        else {
            if (pkmnPerPage > 50) logger.info(pkmnPerPage + " is too high. Defaulting to 50");
            this.pkmnPerPage = pkmnPerPage;
        }
        return ResponseEntity.ok().body("PkmnPerPage set");
    }
}
