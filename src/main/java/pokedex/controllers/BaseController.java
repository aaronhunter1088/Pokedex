package pokedex.controllers;

import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import pokedex.service.PokemonSpringBootService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import pokedexapi.service.PokemonApiService;
import pokedexapi.service.PokemonService;
import skaro.pokeapi.client.PokeApiClient;
import skaro.pokeapi.resource.FlavorText;
import skaro.pokeapi.resource.NamedApiResource;
import skaro.pokeapi.resource.pokemon.Pokemon;
import skaro.pokeapi.resource.pokemon.PokemonType;
import skaro.pokeapi.resource.pokemonspecies.PokemonSpecies;

import java.util.*;

import static pokedexapi.utilities.Constants.GIF_IMAGE_URL;
import static pokedexapi.utilities.Constants.OFFICIAL_IMAGE_URL;

@Controller
public class BaseController
{
    /* Logging instance */
    private static final Logger LOGGER = LogManager.getLogger(BaseController.class);

    String pokemonId = "";
    int page = 1;
    int lastPageSearched = 1;
    int pkmnPerPage = 10;

    protected final PokemonSpringBootService pokemonService;
    protected final PokeApiClient pokeApiClient;
    @Value("${skaro.pokeapi.baseUri}")
    protected String pokeApiBaseUrl;

    @Autowired
    public BaseController(@Qualifier("PokemonSpringBootService") PokemonService pokemonService,
                          PokeApiClient pokeApiClient)
    {
        this.pokemonService = (PokemonSpringBootService) pokemonService;
        this.pokeApiClient = pokeApiClient;
    }

    protected Integer getEvolutionChainID(Map<Integer, List<List<Integer>>> pokemonIDToEvolutionChainMap, String pokemonId)
    {
        LOGGER.info("id: {}", pokemonId);
        List<Integer> keys = pokemonIDToEvolutionChainMap.keySet().stream().toList();
        Integer keyToReturn = 0;
        keysLoop:
        for(Integer key: keys) {
            List<List<Integer>> pokemonIds = pokemonIDToEvolutionChainMap.get(key);
            for (List<Integer> chainIds : pokemonIds) {
                if (chainIds.contains(Integer.valueOf(pokemonId))) {
                    keyToReturn = key;
                    break keysLoop;
                }
            }
        }
        LOGGER.info("chainKey: {}", keyToReturn);
        return keyToReturn;
    }

    /**
     * Returns a Pokemon with application specific properties
     * @param pokemon from pokeapi-reactor
     * @param speciesData from pokeapi-reactor
     * @return Pokemon object
     */
    protected Pokemon createPokemon(Pokemon pokemon, PokemonSpecies speciesData)
    {
        String defaultImage = null != pokemon.sprites().getFrontDefault() ? pokemon.sprites().getFrontDefault() : "/images/pokeball1.jpg";
        String officialImage = OFFICIAL_IMAGE_URL(pokemon.getId());
        String gifImage = GIF_IMAGE_URL(pokemon.id());
        String shinyImage = pokemon.sprites().getFrontShiny();
        String color = speciesData.getColor().name();
        List<FlavorText> flavorTexts = speciesData.getFlavorTextEntries();
        List<FlavorText> pokemonDescriptions = pokemon.descriptions() != null ?
                pokemon.descriptions().stream().filter(entry -> entry.getLanguage().name().equals("en"))
                .toList() : new ArrayList<>();
        String description = !pokemonDescriptions.isEmpty() ?
                pokemonDescriptions.get(new Random().nextInt(pokemonDescriptions.size())).getFlavorText().replace("\n", "")
                : "No description available.";
        //pokemon.setDescriptions(pokemonDescriptions);
        //pokemon.setDescription(description);
        List<PokemonType> types = pokemon.types();
        String typeString = "";
        if (types.size() > 1) {
            LOGGER.debug("More than 1 pokemonType");
            typeString = types.get(0).getType().name().substring(0,1).toUpperCase() + types.get(0).getType().name().substring(1)
                    + " & " + types.get(1).getType().name().substring(0,1).toUpperCase() + types.get(1).getType().name().substring(1);
        } else {
            LOGGER.debug("One pokemonType");
            typeString = types.get(0).getType().name().substring(0,1).toUpperCase() + types.get(0).getType().name().substring(1);
        }
        String pokemonLocation = pokemon.locationAreaEncounters();
        List<String> locationEncounters = pokemonService.getPokemonLocationEncounters(pokemonLocation);

        List<String> moves = pokemon.moves().stream()
                .map(skaro.pokeapi.resource.pokemon.PokemonMove::getMove)
                .map(NamedApiResource::name)
                .sorted()
                .toList();
        pokemon = Pokemon.from(pokemon, Map.of(
                "defaultImage", defaultImage,
                "officialImage", officialImage,
                "gifImage", gifImage,
                "shinyImage", shinyImage,
                "color", color,
                "flavorTexts", flavorTexts,
                "descriptions", pokemonDescriptions,
                "description", description,
                "type", typeString,
                "locationAreaEncounters", locationEncounters
        ));
        pokemon = Pokemon.from(pokemon, Map.of(
                "moveNames", moves
        ));
        return pokemon;
    }

    /**
     * Fetch the pokemon resource
     * @param nameOrId String the name or id of a Pokemon
     * @return the Pokemon or null
     */
    public Pokemon retrievePokemon(String nameOrId)
    {
        LOGGER.info("retrievePokemon");
        try {
            return pokeApiClient.getResource(Pokemon.class, nameOrId).block();
        } catch (Exception e) {
            LOGGER.error("Could not find pokemon with value: {}", nameOrId);
            return null;
        }
    }

    protected Map<String, Object> generateDefaultAttributesMap()
    {
        return new TreeMap<>() {{
            put("name", null); // on screen
            put("gender", null);
            put("id", null);
            put("isBaby", null);
            put("heldItem", null); // on screen
            put("useItem", null); // on screen
            put("knownMove", null); // on screen
            put("knownMoveType", null); // on screen
            put("location", null); // on screen
            put("minAffection", null); // on screen
            put("minBeauty", null); // on screen
            put("minHappiness", null); // on screen
            put("minLevel", null); // on screen
            put("needsRain", null); // on screen
            put("timeOfDay", null); // on screen
            put("partySpecies", null);
            put("partyType", null); // not implemented
            put("trigger", null); // not implemented
            put("relativePhysicalStats", null);
            put("tradeSpecies", null);
            put("turnUpsideDown", null); // on screen
        }};
    }
}