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
import skaro.pokeapi.resource.NamedApiResourceList;
import skaro.pokeapi.resource.pokemon.Pokemon;
import skaro.pokeapi.resource.pokemon.PokemonSprites;
import skaro.pokeapi.resource.pokemon.PokemonType;
import skaro.pokeapi.resource.pokemonspecies.PokemonSpecies;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Controller
public class PokemonListController extends BaseController {

    private static final Logger logger = LogManager.getLogger(PokemonListController.class);
    Map<Integer, com.example.pokedex.entities.Pokemon> pokemonMap = new TreeMap<>();
    int totalPokemon = 0;
    boolean defaultImagePresent = false,
            officialImagePresent = false,
            gifImagePresent = false,
            showGifs = false;

    @Autowired
    public PokemonListController(PokemonService pokemonService) {
        super(pokemonService);
    }

    @GetMapping("/")
    public ModelAndView homepage(ModelAndView mav) {
        mav.addObject("pokemonMap", getPokemonMap());
        mav.addObject("pokemonIds", new ArrayList<>(pokemonMap.keySet()));
        mav.addObject("defaultImagePresent", defaultImagePresent);
        mav.addObject("officialImagePresent", officialImagePresent);
        mav.addObject("gifImagePresent", gifImagePresent);
        mav.addObject("showGifs", showGifs);
        mav.addObject("pkmnPerPage", pkmnPerPage);
        mav.addObject("totalPokemon", totalPokemon);
        mav.addObject("totalPages", (int)Math.ceil((double) totalPokemon/pkmnPerPage));
        mav.addObject("page", page);
        mav.setViewName("index");
        return mav;
    }

    public Map<Integer, com.example.pokedex.entities.Pokemon> getPokemonMap() {
        logger.info("page number: {}", page);
        logger.info("pkmnPerPage: {}", pkmnPerPage);
        NamedApiResourceList<Pokemon> pokemonList = pokemonService.getPokemonList(pkmnPerPage, ((page-1) * pkmnPerPage));
        if (null != pokemonList && !pokemonList.getResults().isEmpty()) {
            logger.debug("pokemonList size: " + pokemonList.getResults().size());
            List<NamedApiResource<Pokemon>> listOfPokemon = pokemonList.getResults();
            logger.debug("pokemonList limit size: " + listOfPokemon.size());
            pokemonMap = new TreeMap<>();
            totalPokemon = pokemonService.getTotalPokemon(null);
            listOfPokemon.parallelStream().forEach(pkmn -> {
                Pokemon pokemonResource = pokemonService.getPokemonByName(pkmn.getName());
                com.example.pokedex.entities.Pokemon pokemon = new com.example.pokedex.entities.Pokemon(pokemonResource);
                PokemonSprites sprites = pokemon.getSprites();
                List<PokemonType> types = pokemon.getTypes();
                String pokemonType = null;
                if (types.size() > 1) {
                    logger.debug("More than 1 pokemonType");
                    pokemonType = types.get(0).getType().getName().substring(0,1).toUpperCase() + types.get(0).getType().getName().substring(1)
                            + " & " + types.get(1).getType().getName().substring(0,1).toUpperCase() + types.get(1).getType().getName().substring(1);
                } else {
                    logger.debug("One pokemonType");
                    pokemonType = types.get(0).getType().getName().substring(0,1).toUpperCase() + types.get(0).getType().getName().substring(1);
                }
                pokemon.setType(pokemonType);
                pokemon.setDefaultImage(sprites.getFrontDefault());
                pokemon.setOfficialImage("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/"+pokemon.getId()+".png");
                HttpResponse<String> response = pokemonService.callUrl("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/versions/generation-v/black-white/animated/"+pokemon.getId()+".gif");
                if (response.statusCode() == 404) {
                    pokemon.setGifImage(null);
                } else {
                    pokemon.setGifImage("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/versions/generation-v/black-white/animated/"+pokemon.getId()+".gif");
                }
                PokemonSpecies speciesData;
                try {
                    speciesData = pokemonService.getPokemonSpeciesData(pokemon.getId().toString());
                    if (null != speciesData) {
                        logger.info("speciesData: {}", speciesData);
                        if (null != speciesData.getColor()) {
                            pokemon.setColor(speciesData.getColor().getName());
                        }
                    }
                } catch (Exception e) {
                    logger.error("No speciesData found using {}", pokemon.getId());
                    logger.warn("setting color to white");
                    pokemon.setColor("white");
                }
                pokemonMap.put(pokemon.getId(), pokemon);
            });
        }
        return pokemonMap;
    }

    @GetMapping("/toggleGifs")
    @ResponseBody
    public Boolean toggleGifs() {
        logger.info("showGifs: {}", !showGifs);
        showGifs = !showGifs;
        return showGifs;
    }

    @GetMapping(value="/page")
    @ResponseBody
    public void page(@RequestParam(name="pageNumber", required=false, defaultValue="10") int pageNumber, ModelAndView mav) {
        logger.info("pagination, page to view: {}", pageNumber);
        if (pageNumber < 0) {
            logger.error("Page number cannot be negative");
            return;
        }
        else if (pageNumber > Math.round((float) totalPokemon /pkmnPerPage)) {
            logger.error("Cannot pick a number more than there are pages");
            return;
        }
        page = pageNumber;
        logger.info("page updated to {}", page);
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
        logger.info("pkmnPerPage updated to: {}", pkmnPerPage);
        return ResponseEntity.ok().body("PkmnPerPage set");
    }

}