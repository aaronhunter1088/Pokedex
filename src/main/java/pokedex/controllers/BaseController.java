package pokedex.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import pokedexapi.service.PokemonApiService;
import pokedexapi.service.PokemonLocationEncounterService;
import skaro.pokeapi.client.PokeApiClient;
import skaro.pokeapi.resource.FlavorText;
import skaro.pokeapi.resource.NamedApiResource;
import skaro.pokeapi.resource.NamedApiResourceList;
import skaro.pokeapi.resource.pokemon.Pokemon;
import skaro.pokeapi.resource.pokemon.PokemonMove;
import skaro.pokeapi.resource.pokemon.PokemonSprites;
import skaro.pokeapi.resource.pokemon.PokemonType;
import skaro.pokeapi.resource.pokemonspecies.PokemonSpecies;

import java.util.*;
import java.util.stream.Collectors;

import static pokedexapi.utilities.Constants.GIF_IMAGE_URL;
import static pokedexapi.utilities.Constants.OFFICIAL_IMAGE_URL;

@Controller
public class BaseController
{
    /* Logging instance */
    private static final Logger LOGGER = LogManager.getLogger(BaseController.class);
    protected final PokemonApiService pokemonService;
    protected final PokeApiClient pokeApiClient;
    protected final PokemonLocationEncounterService pokemonLocationEncounterService;
    @Value("${skaro.pokeapi.baseUri}")
    protected String pokeApiBaseUrl;
    protected String pokemonId = "";
    protected int page = 1;
    protected int lastPageSearched = 1;
    protected int pkmnPerPage = 10;
    Map<Integer, Pokemon> pokemonMap = new TreeMap<>();
    int totalPokemon = 0;
    boolean defaultImagePresent = false,
            officialImagePresent = false,
            gifImagePresent = false,
            showGifs = false;
    String chosenType;
    boolean isDarkMode = false; // "light" or "dark"

    @Autowired
    public BaseController(PokemonApiService pokemonService,
                          PokeApiClient pokeApiClient,
                          PokemonLocationEncounterService pokemonLocationEncounterService)
    {
        this.pokemonService = pokemonService;
        this.pokeApiClient = pokeApiClient;
        this.pokemonLocationEncounterService = pokemonLocationEncounterService;
    }

    protected Integer getEvolutionChainID(Map<Integer, List<List<Integer>>> pokemonIDToEvolutionChainMap, String pokemonId)
    {
        LOGGER.info("id: {}", pokemonId);
        List<Integer> keys = pokemonIDToEvolutionChainMap.keySet().stream().toList();
        Integer keyToReturn = 0;
        keysLoop:
        for (Integer key : keys) {
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
    protected Pokemon createPokemon(@NonNull Pokemon pokemon, @NonNull PokemonSpecies speciesData)
    {
        String defaultImage = pokemon.defaultImage() != null
                ? pokemon.defaultImage()
                : pokemon.sprites().getFrontDefault() != null
                    ? pokemon.sprites().getFrontDefault()
                    : "/images/pokeball1.jpg";
        String officialImage = pokemon.officialImage() != null
                ? pokemon.officialImage()
                : OFFICIAL_IMAGE_URL(pokemon.getId()) != null
                    ? OFFICIAL_IMAGE_URL(pokemon.getId())
                    : "/images/pokeball1.jpg";
        String gifImage = pokemon.gifImage() != null
                ? pokemon.gifImage()
                : GIF_IMAGE_URL(pokemon.id()) != null
                    ? GIF_IMAGE_URL(pokemon.id())
                    : "/images/pokeball1.jpg";
        String shinyImage = pokemon.shinyImage() != null
                ? pokemon.shinyImage()
                : pokemon.sprites().getFrontShiny() != null
                    ? pokemon.sprites().getFrontShiny()
                    : "/images/pokeball1.jpg";
        List<FlavorText> flavorTexts = speciesData.getFlavorTextEntries();
        List<FlavorText> pokemonDescriptions = flavorTexts != null ?
                flavorTexts.stream().filter(entry -> entry.getLanguage().name().equals("en"))
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
            typeString = types.get(0).getType().name().substring(0, 1).toUpperCase() + types.get(0).getType().name().substring(1)
                    + " & " + types.get(1).getType().name().substring(0, 1).toUpperCase() + types.get(1).getType().name().substring(1);
        } else {
            LOGGER.debug("One pokemonType");
            typeString = types.get(0).getType().name().substring(0, 1).toUpperCase() + types.get(0).getType().name().substring(1);
        }
        String pokemonLocation = pokemon.locationAreaEncounters();
        List<String> locationEncounters = pokemonService.getPokemonLocationAreaEncounters(pokemonLocation);

        List<String> moves = pokemon.moves().stream()
                .map(PokemonMove::getMove)
                .map(NamedApiResource::name)
                .sorted()
                .toList();
        pokemon = Pokemon.from(pokemon, Map.of(
                "defaultImage", defaultImage,
                "officialImage", officialImage,
                "gifImage", gifImage,
                "shinyImage", shinyImage,
                "color", speciesData.getColor() != null ? speciesData.getColor().name() : "white",
                "descriptions", pokemonDescriptions,
                "description", description,
                "type", typeString,
                "locations", locationEncounters,
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
        }
        catch (Exception e) {
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

    protected Map<Integer, Pokemon> updateSessionMap(Map<Integer, Pokemon> pokemonMap)
    {
        if (pokemonMap.isEmpty()) {
            if (chosenType != null && !"none".equals(chosenType)) {
                while (this.pokemonMap.size() < pkmnPerPage) {
                    this.pokemonMap.putAll(getPokemonMap());
                    this.page++;
                }
                if (this.pokemonMap.size() > pkmnPerPage) {
                    List<Pokemon> limitedList = new ArrayList<>(this.pokemonMap.values()).subList(0, pkmnPerPage);
                    this.pokemonMap.clear();
                    for (Pokemon pkmn : limitedList) {
                        this.pokemonMap.put(pkmn.id(), pkmn);
                    }
                }
            } else {
                this.pokemonMap = getPokemonMap();
            }
            return this.pokemonMap;
        }
        else {
            if (chosenType != null && !"none".equals(chosenType)) {
                while (this.pokemonMap.size() < pkmnPerPage) {
                    // update the page number based on current last entry in the map
                    page = this.pokemonMap.entrySet().stream()
                            .reduce((first, second) -> second)
                            .map(Map.Entry::getValue)
                            .map(Pokemon::id)
                            .map(id -> id / 10 + 1)
                            .orElse(this.page);
                    this.pokemonMap.putAll(getPokemonMap()
                            .values().stream()
                            .filter(pkmn -> {
                                boolean hasType = false;
                                for (PokemonType type : pkmn.types()) {
                                    if (chosenType.equals(type.getType().name())) {
                                        hasType = true;
                                        break;
                                    }
                                }
                                return hasType;
                            }).collect(
                                    HashMap::new,
                                    (map, pkmn) -> map.put(pkmn.id(), pkmn),
                                    HashMap::putAll));
                    this.page++;
                }
            } else {
                this.pokemonMap = getPokemonMap();
            }
            return this.pokemonMap;
        }
    }

    public Map<Integer, Pokemon> getPokemonMap()
    {
        LOGGER.info("page number: {}", page);
        LOGGER.info("pkmnPerPage: {}", pkmnPerPage);
        NamedApiResourceList<Pokemon> pokemonList = pokemonService.getAllPokemons(pkmnPerPage, ((page - 1) * pkmnPerPage));
        if (null != pokemonList && !pokemonList.results().isEmpty()) {
            LOGGER.debug("pokemonList size: {}", pokemonList.results().size());
            List<NamedApiResource<Pokemon>> listOfPokemon = pokemonList.results();
            LOGGER.debug("pokemonList limit size: {}", listOfPokemon.size());
            totalPokemon = pokemonService.getTotalPokemon(null);
            listOfPokemon.forEach(pkmn -> {
                Pokemon pokemon = pokemonService.getPokemonByIdOrName(pkmn.name());
                String color = "white";
                PokemonSpecies speciesData = null;
                try {
                    if (chosenType != null && !"none".equals(chosenType)) {
                        LOGGER.info("chosenType: {}", chosenType);
                        if (!pokemon.types().stream().anyMatch(pokemonType -> chosenType.equalsIgnoreCase(pokemonType.getType().name()))) {
                            LOGGER.info("Skipping pokemon id {} as it is not of type {}", pokemon.id(), chosenType);
                            return;
                        } else {
                            speciesData = pokemonService.getPokemonSpeciesData(pokemon.id().toString());
                            if (speciesData != null && speciesData.getColor() != null) {
                                LOGGER.debug("speciesData.color: {}", speciesData.getColor().name());
                                color = speciesData.getColor().name();
                            }
                        }
                    } else {
                        speciesData = pokemonService.getPokemonSpeciesData(pokemon.id().toString());
                        if (speciesData != null && speciesData.getColor() != null) {
                            LOGGER.debug("speciesData.color: {}", speciesData.getColor().name());
                            color = speciesData.getColor().name();
                        }
                    }
                }
                catch (Exception e) {
                    LOGGER.error("No speciesData found using {}", pkmn);
                    //logger.warn("setting color to white");
                    //pokemon.setColor("white");
                }
                pokemon = Pokemon.from(pokemon, Map.of("color", color));

                PokemonSprites sprites = pokemon.sprites();
                List<PokemonType> types = pokemon.types();
                String pokemonType = null;
                if (types.size() > 1) {
                    LOGGER.debug("More than 1 pokemonType");
                    pokemonType = types.get(0).getType().name().substring(0, 1).toUpperCase() + types.get(0).getType().name().substring(1)
                            + " & " + types.get(1).getType().name().substring(0, 1).toUpperCase() + types.get(1).getType().name().substring(1);
                } else {
                    LOGGER.debug("One pokemonType");
                    pokemonType = types.get(0).getType().name().substring(0, 1).toUpperCase() + types.get(0).getType().name().substring(1);
                }
                boolean specificTypeToFind = false;
                for (PokemonType type : types) {
                    if ((null != chosenType) && chosenType.equals(type.getType().name())) {
                        specificTypeToFind = true;
                        break;
                    }
                }

                pokemon = Pokemon.from(pokemon, Map.of(
                        "id", pokemon.id(),
                        "type", pokemonType,
                        "defaultImage", sprites.getFrontDefault(),
                        "officialImage", OFFICIAL_IMAGE_URL(pokemon.id()),
                        "gifImage", GIF_IMAGE_URL(pokemon.id()),
                        "color", color,
                        "flavorTexts", speciesData.getFlavorTextEntries(),
                        "descriptions", speciesData.getFlavorTextEntries(),
                        "description", speciesData.getFlavorTextEntries().getFirst().getFlavorText()
                ));
                if (chosenType != null && !("none".equals(chosenType)) && specificTypeToFind) {
                    pokemonMap.put(pokemon.id(), pokemon);
                } else if (null == chosenType) {
                    pokemonMap.put(pokemon.id(), pokemon);
                }
            });
        }
        if (pokemonMap.size() >= pkmnPerPage) {
            return pokemonMap;
        } else {
            this.page = this.page + 1;
            LOGGER.info("pokemonMap size: {}", pokemonMap.size());
            return getPokemonMap();
        }
    }
}