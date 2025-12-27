package pokedex.controllers;

import org.springframework.beans.factory.annotation.Qualifier;
import pokedex.service.PokemonLocationEncounterService;
import pokedexapi.service.PokemonService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import skaro.pokeapi.client.PokeApiClient;
import skaro.pokeapi.resource.NamedApiResource;
import skaro.pokeapi.resource.NamedApiResourceList;
import skaro.pokeapi.resource.pokemon.LocationEncounterArea;
import skaro.pokeapi.resource.pokemon.Pokemon;
import skaro.pokeapi.resource.pokemon.PokemonSprites;
import skaro.pokeapi.resource.pokemon.PokemonType;
import skaro.pokeapi.resource.pokemonspecies.PokemonSpecies;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static pokedexapi.utilities.Constants.*;
import static skaro.pokeapi.utils.PokeApiConstants.POKEAPI_JSON_DECODER_BEAN;

@Controller
public class PokemonListController extends BaseController
{
    /* Logging instance */
    private static final Logger LOGGER = LogManager.getLogger(PokemonListController.class);
    private final PokemonLocationEncounterService pokemonLocationEncounterService;
    private final @Qualifier(POKEAPI_JSON_DECODER_BEAN) JsonMapper jsonMapper;
    Map<Integer, Pokemon> pokemonMap = new TreeMap<>();
    int totalPokemon = 0;
    boolean defaultImagePresent = false,
            officialImagePresent = false,
            gifImagePresent = false,
            showGifs = false;
    String chosenType;

    @Autowired
    public PokemonListController(@Qualifier("PokemonSpringBootService") PokemonService pokemonService,
                                 PokeApiClient pokeApiClient, PokemonLocationEncounterService pokemonLocationEncounterService, JsonMapper jsonMapper)
    {
        super(pokemonService, pokeApiClient);
        this.pokemonLocationEncounterService = pokemonLocationEncounterService;
        this.jsonMapper = jsonMapper;
    }

    @GetMapping("/")
    public ModelAndView homepage(ModelAndView mav)
    {
        lastPageSearched = page;
        pokemonMap.clear();
        mav.addObject("pokemonMap", getPokemonMap());
        //this.page = lastPageSearched;
        mav.addObject("pokemonIds", new ArrayList<>(pokemonMap.keySet()));
        mav.addObject("defaultImagePresent", defaultImagePresent);
        mav.addObject("officialImagePresent", officialImagePresent);
        mav.addObject("gifImagePresent", gifImagePresent);
        mav.addObject("showGifs", showGifs);
        mav.addObject("pkmnPerPage", pkmnPerPage);
        mav.addObject("totalPokemon", totalPokemon);
        mav.addObject("totalPages", (int)Math.ceil((double) totalPokemon/pkmnPerPage));
        mav.addObject("page", page);
        mav.addObject("uniqueTypes", getUniqueTypes());
        mav.addObject("chosenType", chosenType);
        mav.setViewName("index");
        return mav;
    }

    @GetMapping("/toggleGifs")
    @ResponseBody
    public Boolean toggleGifs() {
        LOGGER.info("showGifs: {}", !showGifs);
        showGifs = !showGifs;
        return showGifs;
    }

    @GetMapping(value="/page")
    //@ResponseBody
    public ModelAndView page(@RequestParam(name="pageNumber", required=false, defaultValue="10") int pageNumber, ModelAndView mav)
    {
        LOGGER.info("pagination, page to view: {}", pageNumber);
        if (pageNumber < 0) {
            LOGGER.error("Page number cannot be negative");
            return mav;
        }
        else if (pageNumber > Math.round((float) totalPokemon /pkmnPerPage)) {
            LOGGER.error("Cannot pick a number more than there are pages");
            return mav;
        }
        page = pageNumber;
        //if (lastPageSearched != page) page = lastPageSearched;
        //logger.info("page updated to {}", page);
        return homepage(mav);
    }

    @GetMapping("/pkmnPerPage")
    @ResponseBody
    public ResponseEntity<String> getPokemonPerPage(@RequestParam(name="pkmnPerPage", required=false, defaultValue="10") int pkmnPerPage)
    {
        if (pkmnPerPage <= 0) {
            return ResponseEntity.badRequest().body("Invalid number of Pokemon per page");
        }
        else {
            if (pkmnPerPage > 50) LOGGER.info(pkmnPerPage + " is too high. Defaulting to 50");
            this.pkmnPerPage = pkmnPerPage;
        }
        LOGGER.info("pkmnPerPage updated to: {}", pkmnPerPage);
        return ResponseEntity.ok().body("PkmnPerPage set");
    }

    public Map<Integer, Pokemon> getPokemonMap()
    {
        LOGGER.info("page number: {}", page);
        LOGGER.info("pkmnPerPage: {}", pkmnPerPage);
        NamedApiResourceList<Pokemon> pokemonList = pokemonService.getAllPokemons(pkmnPerPage, ((page-1) * pkmnPerPage));
        if (null != pokemonList && !pokemonList.results().isEmpty()) {
            LOGGER.debug("pokemonList size: " + pokemonList.results().size());
            List<NamedApiResource<Pokemon>> listOfPokemon = pokemonList.results();
            LOGGER.debug("pokemonList limit size: " + listOfPokemon.size());
            totalPokemon = pokemonService.getTotalPokemon(null);
            listOfPokemon.forEach(pkmn -> {
                Pokemon pokemon = pokemonService.getPokemonByName(pkmn.name());
                PokemonSprites sprites = pokemon.sprites();
                List<PokemonType> types = pokemon.types();
                String pokemonType = null;
                if (types.size() > 1) {
                    LOGGER.debug("More than 1 pokemonType");
                    pokemonType = types.get(0).getType().name().substring(0,1).toUpperCase() + types.get(0).getType().name().substring(1)
                            + " & " + types.get(1).getType().name().substring(0,1).toUpperCase() + types.get(1).getType().name().substring(1);
                } else {
                    LOGGER.debug("One pokemonType");
                    pokemonType = types.get(0).getType().name().substring(0,1).toUpperCase() + types.get(0).getType().name().substring(1);
                }
                boolean specificTypeToFind = false;
                for (PokemonType type : types) {
                    if ( (null != chosenType) && chosenType.equals(type.getType().name())) {
                        specificTypeToFind = true;
                        break;
                    }
                }
                List<LocationEncounterArea> listOfLeas = new ArrayList<>();
                ResponseEntity<String> locationEncounters = pokemonLocationEncounterService.getPokemonLocationEncounters(pokemon.id());
                try {
                    // readValue parses JSON text and produces Java objects; convertValue converts between already-parsed Java values (or JsonNode/Map) into a target type.
                    listOfLeas = jsonMapper.readValue(locationEncounters.getBody(), new TypeReference<List<LocationEncounterArea>>(){});
                } catch (Exception e) {
                    LOGGER.error("Error converting locationEncounters for pokemon id {}: {}", pokemon.id(), e.getMessage());
                }
                LOGGER.info("locationExchange: {}", listOfLeas);
                //pokemon = new Pokemon(pokemon.id(), pokemonType);
                //pokemon.setType(pokemonType);
                //pokemon.setDefaultImage(sprites.getFrontDefault());
                //pokemon.setOfficialImage(OFFICIAL_IMAGE_URL(pokemon.id()));
                //HttpResponse<String> response = pokemonService.callUrl("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/versions/generation-v/black-white/animated/"+pokemon.id()+".gif");
//                if (response.statusCode() == 404) {
//                    pokemon.setGifImage(null);
//                } else {
//                    pokemon.setGifImage(GIF_IMAGE_URL(pokemon.id()));
//                }
                String color = "white";
                PokemonSpecies speciesData = null;
                try {
                    speciesData = pokemonService.getPokemonSpeciesData(pokemon.id().toString());
                    if (speciesData != null && speciesData.getColor() != null) {
                        LOGGER.debug("speciesData.color: {}", speciesData.getColor().name());
                        color = speciesData.getColor().name();
                    }
                } catch (Exception e) {
                    LOGGER.error("No speciesData found using {}", pokemon.id());
                    //logger.warn("setting color to white");
                    //pokemon.setColor("white");
                }
                pokemon = Pokemon.from(pokemon, Map.of(
                        "id", pokemon.id(),
                        "type", pokemonType,
                        "defaultImage", sprites.getFrontDefault(),
                        "officialImage", OFFICIAL_IMAGE_URL(pokemon.id()),
                        "gifImage", GIF_IMAGE_URL(pokemon.id()),
                        "color", color,"flavorTexts", speciesData.getFlavorTextEntries(),
                        "descriptions", speciesData.getFlavorTextEntries(),
                        "description", speciesData.getFlavorTextEntries().getFirst().getFlavorText()
                ));
                pokemon = Pokemon.from(pokemon, Map.of(
                        "locationAreaEncounters", listOfLeas
//                        "moveNames", moves
                ));
                if (!("".equals(chosenType)) && specificTypeToFind) {
                    pokemonMap.put(pokemon.id(), pokemon);
                } else if (null == chosenType) {
                    pokemonMap.put(pokemon.id(), pokemon);
                }
            });
        }
        if (pokemonMap.size() >= pkmnPerPage) {
            return pokemonMap;
        } else {
            this.page = this.page+1;
            LOGGER.info("pokemonMap size: {}", pokemonMap.size());
            return getPokemonMap();
        }
    }

    public List<String> getUniqueTypes()
    {
        try
        {
            return pokemonService.getAllTypes();
        }
        catch (Exception e)
        {
            LOGGER.error("Error retrieving unique types: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @GetMapping(value="/getPokemonByType")
    @ResponseBody
    public ResponseEntity<String> getPokemonByType(@RequestParam(name="chosenType", required=false, defaultValue="") String chosenType, ModelAndView mav)
    {
        this.chosenType = !"none".equals(chosenType) ? chosenType : null;
        this.pokemonMap.clear();
        homepage(mav);
        LOGGER.info("lastPageSearched: {}", lastPageSearched);
        return ResponseEntity.ok().body("chosenType set");
    }
}