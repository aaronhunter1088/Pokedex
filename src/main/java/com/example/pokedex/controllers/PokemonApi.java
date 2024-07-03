package com.example.pokedex.controllers;

import com.example.pokedex.service.PokemonService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.json.JSONParser;
import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import skaro.pokeapi.client.PokeApiClient;
import skaro.pokeapi.query.PageQuery;
import skaro.pokeapi.resource.FlavorText;
import skaro.pokeapi.resource.NamedApiResource;
import skaro.pokeapi.resource.NamedApiResourceList;
import skaro.pokeapi.resource.pokemon.Pokemon;
import skaro.pokeapi.resource.pokemonspecies.PokemonSpecies;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/pokemon")
public class PokemonApi extends BaseController {

    private static final Logger logger = LogManager.getLogger(PokemonApi.class);
    @Autowired
    private PokeApiClient pokeApiClient;
    @Value("${skaro.pokeapi.baseUri}")
    private String pokeApiBaseUrl;

    @Autowired
    public PokemonApi(PokemonService pokemonService) {
        super(pokemonService);
    }

    @RequestMapping(value = "/list", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getAllPokemon(@RequestParam(value="limit", required=false, defaultValue="10") int limit,
                                                @RequestParam(value="offset", required=false, defaultValue="0") int offset) {
        logger.info("getAllPokemon limit:{} offset:{}", limit, offset);
        NamedApiResourceList<Pokemon> allPokemon;
        try {
            //"https://pokeapi.co/api/v2/pokemon/?limit=10&offset=0"
            allPokemon = pokeApiClient.getResource(Pokemon.class, new PageQuery(limit, offset))
                    .block();
            return ResponseEntity.ok(allPokemon);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Could not fetch all pokemon because " + e.getMessage());
        }
    }

    @RequestMapping(value = "/{nameOrId}", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getPokemon(@PathVariable("nameOrId") String nameOrId)
    {
        logger.info("getPokemon: {}", nameOrId);
        Pokemon pokemon = retrievePokemon(nameOrId);
        if (null != pokemon) {
            pokemon = pokeApiClient.getResource(Pokemon.class, nameOrId).block();
            return ResponseEntity.ok(pokemon);
        } else {
            logger.warn("pokemon was not found!");
            return ResponseEntity.badRequest().body(nameOrId + " was not found!");
        }
    }

    @RequestMapping(value="/{nameOrId}/description", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getPokemonDescription(@PathVariable("nameOrId") String nameOrId)
    {
        logger.info("getPkmnDescription: {}", nameOrId);
        List<FlavorText> pokemonDescriptions;
        try {
            pokemonDescriptions =  pokeApiClient.getResource(PokemonSpecies.class, nameOrId).blockOptional().get()
                    .getFlavorTextEntries().stream().filter(entry -> entry.getLanguage().getName().equals("en"))
                    .toList();
            int randomEntry = new Random().nextInt(pokemonDescriptions.size());
            String description = pokemonDescriptions.get(randomEntry).getFlavorText().replace("\n", " ");
            logger.info("description: {}", description);
            return ResponseEntity.ok(description);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(nameOrId + " text was not found!");
        }
    }

    @RequestMapping(value = "/{nameOrId}/color", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getDesignatedColor(@PathVariable("nameOrId") String nameOrId)
    {
        PokemonSpecies speciesInfo;
        try {
            speciesInfo = pokeApiClient.getResource(PokemonSpecies.class, nameOrId).block();
            if (speciesInfo != null) {
                String colorOfPokemon = speciesInfo.getColor().getName();
                logger.info("color: {}", colorOfPokemon);
                return ResponseEntity.ok(colorOfPokemon);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(nameOrId + " doesn't have a species!");
        }
        return ResponseEntity.notFound().build();
    }

    @RequestMapping(value="/{nameOrId}/validateNameOrId", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Boolean> validateNameOrId(@PathVariable("nameOrId") String nameOrId)
    {
        try {
            Pokemon pokemon = retrievePokemon(nameOrId);
            if (null != pokemon) {
                logger.info("valid nameOrId: {}", nameOrId);
                return ResponseEntity.ok().body(true);
            } else {
                logger.warn("invalid nameOrId: {}", nameOrId);
                return ResponseEntity.badRequest().body(false);
            }
        } catch (Exception e) {
            logger.warn("There was an error fetching the Pokemon '{}' because {}", nameOrId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @RequestMapping(value="/{nameOrId}/locations", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getLocations(@PathVariable("nameOrId") String nameOrId)
    {
        logger.info("getLocations for: {}", nameOrId);
        Pokemon pokemon = retrievePokemon(nameOrId);
        if (null == pokemon) {
            return ResponseEntity.badRequest()
                    .body("Could not find pokemon with value:" + nameOrId);
        }
        String encountersString = pokemon.getLocationAreaEncounters();
        HttpResponse<String> response;
        List<String> namesOfAreas = new ArrayList<>();
        JSONParser jsonParser;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(encountersString))
                    .GET()
                    .build();
            response = HttpClient.newBuilder()
                    .build()
                    .send(request,  HttpResponse.BodyHandlers.ofString());
            logger.debug("response: " + response.body());
            jsonParser = new JSONParser(response.body());
            List<LinkedHashMap<String, String>> map = (List<LinkedHashMap<String, String>>) jsonParser.parse();
            for(Map m : map) {
                LinkedHashMap<String, String> area = (LinkedHashMap<String, String>) m.get("location_area");
                namesOfAreas.add(area.get("name"));
            }
            namesOfAreas.forEach(area -> logger.debug("area: {}", area));
        } catch (Exception e) {
            logger.error("Error retrieving response because {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
        JSONArray array = new JSONArray();
        array.addAll(namesOfAreas);
        return new ResponseEntity<>(array.toJSONString(), HttpStatus.OK);
    }

    @RequestMapping(value="/{nameOrId}/species")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getSpeciesData(@PathVariable("nameOrId") String nameOrId)
    {
        logger.info("getSpeciesData: {}", nameOrId);
        Pokemon pokemonResource = (Pokemon) getPokemon(nameOrId).getBody();
        NamedApiResource<PokemonSpecies> speciesResource = pokemonResource.getSpecies();
        if (null == speciesResource) {
            return ResponseEntity.noContent().build();
        }
        String speciesUrl = speciesResource.getUrl();
        HttpResponse<String> response;
        JSONParser jsonParser;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(speciesUrl))
                    .GET()
                    .build();
            response = HttpClient.newBuilder()
                    .build()
                    .send(request,  HttpResponse.BodyHandlers.ofString());
            logger.info("response: {}", response.body());
            jsonParser = new JSONParser(response.body());
            Map<String, Object> results = (Map<String, Object>) jsonParser.parse();
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            logger.error("Error retrieving response because {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @RequestMapping(value= "/{nameOrId}/evolutionChain", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getEvolutionChain(@PathVariable("nameOrId") String nameOrId)
    {
        Map<String, Object> speciesData = getSpeciesData(nameOrId).getBody();
        try {
            Map<String, Object> chainData = (LinkedHashMap<String, Object>) speciesData.get("evolution_chain");
            String chainUrl = (String) chainData.get("url");
            logger.info("chainUrl: " + chainUrl);
            HttpResponse<String> response;
            JSONParser jsonParser;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(chainUrl))
                    .GET()
                    .build();
            response = HttpClient.newBuilder()
                    .build()
                    .send(request,  HttpResponse.BodyHandlers.ofString());
            logger.info("response: {}", response.body());
            jsonParser = new JSONParser(response.body());
            Map<String, Object> results = (Map<String, Object>) jsonParser.parse();
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            logger.error("Error parsing species data because {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Fetch the pokemon resource
     * @param nameOrId String the name or id of a Pokemon
     * @return the Pokemon or null
     */
    public Pokemon retrievePokemon(String nameOrId) {
        logger.info("retrievePokemon");
        try {
            return pokeApiClient.getResource(Pokemon.class, nameOrId).block();
        } catch (Exception e) {
            logger.error("Could not find pokemon with value: {}", nameOrId);
            return null;
        }
    }
}
