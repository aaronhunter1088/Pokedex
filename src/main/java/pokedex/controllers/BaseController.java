package pokedex.controllers;

import jakarta.annotation.PreDestroy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import tools.jackson.databind.ObjectMapper;

import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static pokedexapi.utilities.Constants.GIF_IMAGE_URL;
import static pokedexapi.utilities.Constants.OFFICIAL_IMAGE_URL;

@Controller
public class BaseController
{
    /* Logging instance */
    private static final Logger LOGGER = LogManager.getLogger(BaseController.class);
    private static final int WAIT_INTERVAL_MS = 100; // Time to wait between checks for filtered Pokemon
    private static final int MAX_WAIT_ITERATIONS = 30; // Max iterations to wait (30 * 100ms = 3 seconds)
    private static final int TIMEOUT_MULTIPLIER = 20; // Multiplier for extended timeout (20 * 3s = 60 seconds)
    private static final int PROGRESS_LOG_INTERVAL = 50; // Log progress every 50 iterations (5 seconds)
    
    protected final PokemonApiService pokemonService;
    protected final PokeApiClient pokeApiClient;
    protected final PokemonLocationEncounterService pokemonLocationEncounterService;
    private final ObjectMapper objectMapper;
    @Value("${skaro.pokeapi.baseUri}")
    protected String pokeApiBaseUrl;
    protected String pokemonId = "";
    protected int page = 1;
    protected int lastPageSearched = 1;
    protected int pkmnPerPage = 10;
    Map<Integer, Pokemon> pokemonMap = new TreeMap<>();
    Map<String, List<Pokemon>> filteredPokemonByType = new ConcurrentHashMap<>();
    Map<String, Boolean> filteringInProgress = new ConcurrentHashMap<>();
    private final ExecutorService filterExecutor = Executors.newFixedThreadPool(
        Math.min(Runtime.getRuntime().availableProcessors() * 2, 20));
    private volatile boolean retroactiveFetchingStarted = false;
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
                          PokemonLocationEncounterService pokemonLocationEncounterService,
                          ObjectMapper objectMapper)
    {
        this.pokemonService = pokemonService;
        this.pokeApiClient = pokeApiClient;
        this.pokemonLocationEncounterService = pokemonLocationEncounterService;
        this.objectMapper = objectMapper;
    }

    @PreDestroy
    public void cleanup()
    {
        LOGGER.info("Shutting down filter executor service");
        filterExecutor.shutdown();
        try {
            if (!filterExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                filterExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            filterExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
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
        if (chosenType != null && !"none".equals(chosenType)) {
            // Check if we already have filtered Pokemon for this type
            if (!filteredPokemonByType.containsKey(chosenType)) {
                LOGGER.info("Type {} not yet cached, starting fetch", chosenType);
                
                // Initialize with empty list for this type
                List<Pokemon> synchronizedList = Collections.synchronizedList(new ArrayList<>());
                filteredPokemonByType.put(chosenType, synchronizedList);
                filteringInProgress.put(chosenType, true);
                
                // Start background thread to fetch all Pokemon
                filterExecutor.submit(() -> {
                    try {
                        fetchAllPokemonByType(chosenType, synchronizedList);
                    } finally {
                        filteringInProgress.put(chosenType, false);
                        LOGGER.info("Completed gathering all Pokemon of type: {}", chosenType);
                    }
                });
            }
            
            // Get the list for this type
            List<Pokemon> allOfType = filteredPokemonByType.get(chosenType);
            
            // Determine how many Pokemon we need for the current page
            int startIndex = (page - 1) * pkmnPerPage;
            int minRequired = startIndex + pkmnPerPage;
            
            LOGGER.info("Waiting for at least {} Pokemon of type {} (page {} needs {}..{})", 
                minRequired, chosenType, page, startIndex, minRequired);
            
            int waitCount = 0;
            // Wait up to 3 seconds for minimum required Pokemon
            int maxWait = MAX_WAIT_ITERATIONS;
            
            // Wait only until we have enough Pokemon for the requested page
            while (filteringInProgress.getOrDefault(chosenType, false) && waitCount < maxWait) {
                int currentCount;
                synchronized (allOfType) {
                    currentCount = allOfType.size();
                }
                
                // If we have enough Pokemon for the current page, we can stop waiting
                if (currentCount >= minRequired) {
                    LOGGER.info("Found {} Pokemon of type {}, sufficient for page {}", currentCount, chosenType, page);
                    break;
                }
                
                try {
                    Thread.sleep(WAIT_INTERVAL_MS);
                    waitCount++;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LOGGER.error("Interrupted while waiting for Pokemon", e);
                    break;
                }
            }
            
            // Check if fetching is still in progress
            boolean stillFetching = filteringInProgress.getOrDefault(chosenType, false);
            int currentSize;
            synchronized (allOfType) {
                currentSize = allOfType.size();
            }
            
            if (stillFetching) {
                LOGGER.info("Fetching still in progress for type {}, but have {} Pokemon to display (continuing in background)", 
                    chosenType, currentSize);
                // Use current size as temporary total, will be updated when fetching completes
                totalPokemon = currentSize;
            } else {
                // Fetching complete, use final count
                totalPokemon = currentSize;
                LOGGER.info("Total Pokemon of type {}: {}", chosenType, totalPokemon);
            }
            
            int endIndex = Math.min(startIndex + pkmnPerPage, totalPokemon);
            
            this.pokemonMap.clear();
            if (startIndex < totalPokemon) {
                synchronized (allOfType) {
                    List<Pokemon> pageOfPokemon = new ArrayList<>(allOfType.subList(startIndex, endIndex));
                    for (Pokemon pkmn : pageOfPokemon) {
                        this.pokemonMap.put(pkmn.id(), pkmn);
                    }
                }
            }
            
            return this.pokemonMap;
        } else {
            // No filter, use normal pagination
            this.pokemonMap = getPokemonMap();
            return this.pokemonMap;
        }
    }
    
    private void fetchAllPokemonByType(String type, List<Pokemon> resultList)
    {
        LOGGER.info("Background thread: Gathering all Pokemon of type: {}", type);
        int currentPage = 1;
        int totalAllPokemon = pokemonService.getTotalPokemon(null);
        int maxPages = (int) Math.ceil((double) totalAllPokemon / 50); // Use larger page size for efficiency
        
        // Fetch all Pokemon and filter by type
        while (currentPage <= maxPages) {
            NamedApiResourceList<Pokemon> pokemonList = pokemonService.getAllPokemons(50, ((currentPage - 1) * 50));
            if (pokemonList != null && !pokemonList.results().isEmpty()) {
                for (NamedApiResource<Pokemon> pkmnResource : pokemonList.results()) {
                    try {
                        Pokemon pokemon = pokemonService.getPokemonByIdOrName(pkmnResource.name());
                        // Check if Pokemon has the chosen type
                        boolean hasType = pokemon.types().stream()
                                .anyMatch(pokemonType -> type.equalsIgnoreCase(pokemonType.getType().name()));
                        
                        if (hasType) {
                            // Get species data for color
                            String color = "white";
                            PokemonSpecies speciesData = null;
                            try {
                                speciesData = pokemonService.getPokemonSpeciesData(pokemon.id().toString());
                                if (speciesData != null && speciesData.getColor() != null) {
                                    color = speciesData.getColor().name();
                                }
                            } catch (Exception e) {
                                LOGGER.error("No speciesData found for pokemon id: {}", pokemon.id());
                            }
                            
                            // Build type string
                            String pokemonType;
                            List<PokemonType> types = pokemon.types();
                            if (types.size() > 1) {
                                pokemonType = types.get(0).getType().name().substring(0, 1).toUpperCase() + types.get(0).getType().name().substring(1)
                                        + " & " + types.get(1).getType().name().substring(0, 1).toUpperCase() + types.get(1).getType().name().substring(1);
                            } else {
                                pokemonType = types.get(0).getType().name().substring(0, 1).toUpperCase() + types.get(0).getType().name().substring(1);
                            }
                            
                            // Create enhanced Pokemon object
                            PokemonSprites sprites = pokemon.sprites();
                            pokemon = Pokemon.from(pokemon, Map.of(
                                    "id", pokemon.id(),
                                    "type", pokemonType,
                                    "defaultImage", sprites.getFrontDefault(),
                                    "officialImage", OFFICIAL_IMAGE_URL(pokemon.id()),
                                    "gifImage", GIF_IMAGE_URL(pokemon.id()),
                                    "color", color,
                                    "flavorTexts", speciesData != null ? speciesData.getFlavorTextEntries() : new ArrayList<>(),
                                    "descriptions", speciesData != null ? speciesData.getFlavorTextEntries() : new ArrayList<>(),
                                    "description", speciesData != null && !speciesData.getFlavorTextEntries().isEmpty() 
                                            ? speciesData.getFlavorTextEntries().getFirst().getFlavorText() 
                                            : "No description available."
                            ));

                            // Check pokemon gifImage for existence.
                            // If image returns 404, reset gifImage to official image.
                            // If image returns 200, no change is needed
                            try {
                                HttpResponse<String> response = pokemonService.callUrl(pokemon.gifImage());
                                if (response.statusCode() == 404) {
                                    LOGGER.debug("GIF image not found for {}, using official image instead", pokemon.name());
                                    pokemon = Pokemon.from(pokemon, Map.of(
                                            "gifImage", pokemon.officialImage()
                                    ));
                                }
                            } catch (Exception e) {
                                LOGGER.debug("Error validating GIF image for {}, using official image instead", pokemon.name());
                                pokemon = Pokemon.from(pokemon, Map.of(
                                        "gifImage", pokemon.officialImage()
                                ));
                            }
                            
                            resultList.add(pokemon);
                            LOGGER.debug("Added {} to filtered list. Total so far: {}", pokemon.name(), resultList.size());
                        }
                    } catch (Exception e) {
                        LOGGER.error("Error processing pokemon: {}", pkmnResource.name(), e);
                    }
                }
            }
            currentPage++;
        }
        
        LOGGER.info("Background thread: Total Pokemon of type {}: {}", type, resultList.size());
    }
    
    /**
     * Starts retroactive fetching of all Pokemon by type in the background.
     * This method should be called after the initial page load to pre-populate the cache.
     */
    protected void startRetroactiveFetchingByType()
    {
        if (retroactiveFetchingStarted) {
            LOGGER.debug("Retroactive fetching already started, skipping");
            return;
        }
        
        retroactiveFetchingStarted = true;
        LOGGER.info("Starting retroactive fetching of Pokemon by type");
        
        // Start in a separate thread to not block the page load
        filterExecutor.submit(() -> {
            try {
                // Get all types from the API
                List<String> allTypes = pokemonService.getAllTypes();
                LOGGER.info("Found {} types to fetch Pokemon for", allTypes.size());
                
                // For each type, start fetching Pokemon in parallel
                List<CompletableFuture<Void>> futures = new ArrayList<>();
                for (String type : allTypes) {
                    CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                        try {
                            // Use putIfAbsent to avoid race conditions
                            List<Pokemon> existingList = filteredPokemonByType.putIfAbsent(type, 
                                Collections.synchronizedList(new ArrayList<>()));
                            
                            if (existingList == null) {
                                // We successfully added the type, so we should fetch it
                                LOGGER.info("Retroactively fetching Pokemon of type: {}", type);
                                List<Pokemon> synchronizedList = filteredPokemonByType.get(type);
                                filteringInProgress.put(type, true);
                                
                                try {
                                    fetchAllPokemonByType(type, synchronizedList);
                                } finally {
                                    filteringInProgress.put(type, false);
                                    LOGGER.info("Completed retroactive fetch for type {}: {} Pokemon", type, synchronizedList.size());
                                }
                            } else {
                                LOGGER.debug("Type {} already being fetched, skipping", type);
                            }
                        } catch (Exception e) {
                            LOGGER.error("Error retroactively fetching Pokemon of type {}", type, e);
                            filteringInProgress.put(type, false);
                        }
                    }, filterExecutor);
                    
                    futures.add(future);
                }
                
                // Wait for all types to complete (with timeout to prevent indefinite blocking)
                try {
                    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                        .get(2, TimeUnit.MINUTES); // 2 minute timeout for all types
                    LOGGER.info("Completed retroactive fetching of all Pokemon by type");
                } catch (TimeoutException e) {
                    LOGGER.warn("Retroactive fetching timed out after 2 minutes, some types may still be processing", e);

                    // Cancel any futures that are still running to avoid unnecessary background work
                    int cancelledCount = 0;
                    for (CompletableFuture<Void> future : futures) {
                        if (!future.isDone()) {
                            boolean cancelled = future.cancel(true);
                            if (cancelled) {
                                cancelledCount++;
                            }
                        }
                    }
                    if (cancelledCount > 0) {
                        LOGGER.info("Cancelled {} incomplete retroactive fetch tasks after timeout", cancelledCount);
                    } else {
                        LOGGER.debug("No incomplete retroactive fetch tasks to cancel after timeout");
                    }
                } catch (Exception e) {
                    LOGGER.error("Error waiting for retroactive fetching to complete", e);
                }
                
            } catch (Exception e) {
                LOGGER.error("Error during retroactive fetching", e);
            }
        });
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
                    }
                    else {
                        speciesData = pokemonService.getPokemonSpeciesData(pokemon.id().toString());
                        if (speciesData != null && speciesData.getColor() != null) {
                            LOGGER.debug("speciesData.color: {}", speciesData.getColor().name());
                            color = speciesData.getColor().name();
                        }
                    }
                }
                catch (Exception e) {
                    LOGGER.error("No speciesData found using {} and service species call", pkmn);
                    LOGGER.info("Trying direct call with species: {}, url: {}", pokemon.species().name(), pokemon.species().url());
                    try {
                        String responseBody = pokemonService.callUrl(pokemon.species().url()).body();
                        speciesData = objectMapper.readValue(responseBody, PokemonSpecies.class);
                        LOGGER.info("Successfully retrieved speciesData for pokemon id: {}", pokemon.id());
                    }
                    catch (Exception ex) {
                        LOGGER.error("No speciesData found using {} and direct call. Empty speciesData created.", pokemon.id());
                        speciesData = new PokemonSpecies();
                    }
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
                        "flavorTexts", speciesData.getFlavorTextEntries() != null ? speciesData.getFlavorTextEntries() : new ArrayList<>(),
                        "descriptions", speciesData.getFlavorTextEntries() != null ? speciesData.getFlavorTextEntries() : new ArrayList<>(),
                        "description", speciesData.getFlavorTextEntries() != null
                                ? speciesData.getFlavorTextEntries().getFirst().getFlavorText()
                                : "No description available."
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