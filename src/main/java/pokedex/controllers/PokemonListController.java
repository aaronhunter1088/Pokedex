package pokedex.controllers;

import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import tools.jackson.databind.ObjectMapper;

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

    @Value("${app.base.url}")
    private String baseUrl;

    @Autowired
    public PokemonListController(PokemonApiService pokemonService,
                                 PokeApiClient pokeApiClient,
                                 PokemonLocationEncounterService pokemonLocationEncounterService,
                                 ObjectMapper objectMapper)
    {
        super(pokemonService, pokeApiClient, pokemonLocationEncounterService, objectMapper);
    }

    @GetMapping("/")
    public ModelAndView homepage(ModelAndView mav, HttpSession httpSession,
           @RequestParam(name = "darkmode", required = true, defaultValue = "false") String darkmode)
    {

        lastPageSearched = page;
        if (pokemonMap.isEmpty() || chosenType != null) {
            //pokemonMap.clear();
            pokemonMap = updateSessionMap(pokemonMap);
            //httpSession.setAttribute("pokemonMap", pokemonMap);
        }
        
        // Start retroactive fetching of Pokemon by type in the background
        // This will happen after the initial page load and won't block it
        if (!retroactiveFetchingStarted.compareAndSet(false, true)) {
            LOGGER.debug("Retroactive fetching already started, skipping");
        } else {
            startRetroactiveFetchingByType();
        }
        
        mav.addObject("pokemonMap", pokemonMap);
        this.page = lastPageSearched;
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
        isDarkMode = darkmode.equals("true");
        mav.addObject("isDarkMode", isDarkMode);
        mav.addObject("baseUrl", baseUrl);
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
    public ModelAndView page(@RequestParam(name = "pageNumber", required = false, defaultValue = "1") int pageNumber,
                             @RequestParam(name = "darkmode", required = true, defaultValue = "false") String darkmode,
                             ModelAndView mav, HttpSession httpSession)
    {
        LOGGER.info("pagination, page to view: {}", pageNumber);
        if (pageNumber < 1) {
            LOGGER.error("Page number must be at least 1, received: {}", pageNumber);
            return mav;
        } else if (pageNumber > Math.round((float) totalPokemon / pkmnPerPage)) {
            LOGGER.error("Cannot pick a number more than there are pages");
            return mav;
        }
        page = pageNumber;
        // Clear pokemon map to force reload with new page
        this.pokemonMap.clear();
        // Clear session cache when changing pages
        httpSession.removeAttribute("pokemonMap");
        return homepage(mav, httpSession, darkmode);
    }

    @GetMapping("/pkmnPerPage")
    @ResponseBody
    public ResponseEntity<String> getPokemonPerPage(@RequestParam(name = "pkmnPerPage", required = false, defaultValue = "10") int pkmnPerPage,
                                                     HttpSession httpSession)
    {
        if (pkmnPerPage <= 0) {
            return ResponseEntity.badRequest().body("Invalid number of Pokemon per page");
        } else {
            if (pkmnPerPage > 50) LOGGER.info(pkmnPerPage + " is too high. Defaulting to 50");
            this.pkmnPerPage = pkmnPerPage;
        }
        // Clear pokemon map to force reload with new page size
        this.pokemonMap.clear();
        // Reset page to 1
        this.page = 1;
        // Clear session cache when Pokemon per page changes
        httpSession.removeAttribute("pokemonMap");
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
                                                   HttpSession httpSession)
    {
        String previousType = this.chosenType;
        this.chosenType = !"none".equals(chosenType) ? chosenType : null;
        // Reset page to 1 when changing filter
        this.page = 1;
        // Clear the pokemon map to force reload
        this.pokemonMap.clear();
        // If switching from a type to no filter, clear the filtered cache
        if (this.chosenType == null && previousType != null) {
            this.filteredPokemonByType.clear();
            this.filteringInProgress.clear();
        }
        LOGGER.info("Type filter set to: {}", this.chosenType);
        return ResponseEntity.ok().body("chosenType set");
    }

}