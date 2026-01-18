package pokedex.controllers;

import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import pokedexapi.service.PokemonApiService;
import pokedexapi.service.PokemonLocationEncounterService;
import skaro.pokeapi.client.PokeApiClient;
import skaro.pokeapi.resource.NamedApiResource;
import skaro.pokeapi.resource.NamedApiResourceList;
import skaro.pokeapi.resource.pokemon.Pokemon;
import skaro.pokeapi.resource.pokemon.PokemonSprites;
import skaro.pokeapi.resource.pokemon.PokemonType;
import skaro.pokeapi.resource.pokemonspecies.PokemonSpecies;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static pokedexapi.utilities.Constants.GIF_IMAGE_URL;
import static pokedexapi.utilities.Constants.OFFICIAL_IMAGE_URL;

@Controller
public class PokemonListController extends BaseController
{
    /* Logging instance */
    private static final Logger LOGGER = LogManager.getLogger(PokemonListController.class);

    @Autowired
    public PokemonListController(PokemonApiService pokemonService,
                                 PokeApiClient pokeApiClient,
                                 PokemonLocationEncounterService pokemonLocationEncounterService)
    {
        super(pokemonService, pokeApiClient, pokemonLocationEncounterService);
    }

    @GetMapping("/")
    public ModelAndView homepage(ModelAndView mav, HttpSession httpSession,
           @RequestParam(name = "mode", required = true, defaultValue = "false") String mode)
    {
        lastPageSearched = page;
        pokemonMap.clear();
        @SuppressWarnings("unchecked")
        Map<Integer, Pokemon> sessionMap = (Map<Integer, Pokemon>) httpSession.getAttribute("pokemonMap");
        pokemonMap = updateSessionMap(sessionMap);
        mav.addObject("pokemonMap", pokemonMap);
        // store populated map in session so it persists between requests
        httpSession.setAttribute("pokemonMap", pokemonMap);
        //this.page = lastPageSearched;
        mav.addObject("pokemonIds", new ArrayList<>(pokemonMap.keySet()));
        mav.addObject("defaultImagePresent", defaultImagePresent);
        mav.addObject("officialImagePresent", officialImagePresent);
        mav.addObject("gifImagePresent", gifImagePresent);
        mav.addObject("showGifs", showGifs);
        mav.addObject("pkmnPerPage", pkmnPerPage);
        mav.addObject("totalPokemon", totalPokemon);
        mav.addObject("totalPages", (int) Math.ceil((double) totalPokemon / pkmnPerPage));
        mav.addObject("page", page);
        mav.addObject("uniqueTypes", getUniqueTypes());
        mav.addObject("chosenType", chosenType);
        mav.addObject("isDarkMode", isDarkMode = mode.equals("true"));
        mav.setViewName("index");
        return mav;
    }

    @GetMapping("/toggleGifs")
    @ResponseBody
    public Boolean toggleGifs()
    {
        LOGGER.info("showGifs: {}", !showGifs);
        showGifs = !showGifs;
        return showGifs;
    }

    @GetMapping(value = "/page")
    //@ResponseBody
    public ModelAndView page(@RequestParam(name = "pageNumber", required = false, defaultValue = "10") int pageNumber,
                             ModelAndView mav, HttpSession httpSession)
    {
        LOGGER.info("pagination, page to view: {}", pageNumber);
        if (pageNumber < 0) {
            LOGGER.error("Page number cannot be negative");
            return mav;
        } else if (pageNumber > Math.round((float) totalPokemon / pkmnPerPage)) {
            LOGGER.error("Cannot pick a number more than there are pages");
            return mav;
        }
        page = pageNumber;
        //if (lastPageSearched != page) page = lastPageSearched;
        //logger.info("page updated to {}", page);
        return homepage(mav, httpSession, String.valueOf(isDarkMode));
    }

    @GetMapping("/pkmnPerPage")
    @ResponseBody
    public ResponseEntity<String> getPokemonPerPage(@RequestParam(name = "pkmnPerPage", required = false, defaultValue = "10") int pkmnPerPage)
    {
        if (pkmnPerPage <= 0) {
            return ResponseEntity.badRequest().body("Invalid number of Pokemon per page");
        } else {
            if (pkmnPerPage > 50) LOGGER.info(pkmnPerPage + " is too high. Defaulting to 50");
            this.pkmnPerPage = pkmnPerPage;
        }
        LOGGER.info("pkmnPerPage updated to: {}", pkmnPerPage);
        return ResponseEntity.ok().body("PkmnPerPage set");
    }



    public List<String> getUniqueTypes()
    {
        try {
            return pokemonService.getAllTypes();
        }
        catch (Exception e) {
            LOGGER.error("Error retrieving unique types: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @GetMapping(value = "/getPokemonByType")
    @ResponseBody
    public ResponseEntity<String> getPokemonByType(@RequestParam(name = "chosenType", required = false, defaultValue = "") String chosenType,
                                                   ModelAndView mav, HttpSession httpSession)
    {
        this.chosenType = !"none".equals(chosenType) ? chosenType : null;
        this.pokemonMap.clear();
        homepage(mav, httpSession, String.valueOf(isDarkMode));
        LOGGER.info("lastPageSearched: {}", lastPageSearched);
        return ResponseEntity.ok().body("chosenType set");
    }

}