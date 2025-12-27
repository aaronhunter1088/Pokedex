package pokedex.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import pokedexapi.service.PokemonApiService;
import pokedexapi.service.PokemonLocationEncounterService;
import pokedexapi.service.PokemonService;
import skaro.pokeapi.client.PokeApiClient;
import skaro.pokeapi.resource.NamedApiResource;
import skaro.pokeapi.resource.locationarea.LocationArea;
import skaro.pokeapi.resource.locationarea.PokemonEncounter;
import skaro.pokeapi.resource.pokemon.LocationEncounterArea;
import skaro.pokeapi.resource.pokemon.Pokemon;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

/**
 * Pokedex SpringBoot Service used in this Pokédex application.
 */
@Service(value = "PokemonSpringBootService")
public class PokemonSpringBootService extends PokemonApiService
{
    /* Logging instance */
    public static final Logger LOGGER = LogManager.getLogger(PokemonSpringBootService.class);

    public PokemonSpringBootService(PokeApiClient pokeApiClient,
                                    PokemonLocationEncounterService pokemonLocationEncounterService,
                                    JsonMapper jsonMapper)
    {
        super(pokeApiClient, jsonMapper);
    }

    public Pokemon getPokemonByName(String pokemonIDName)
    {
        Pokemon pokemon = null;
        try {
            pokemon = pokeApiClient.getResource(Pokemon.class, pokemonIDName).block();
            if (null != pokemon) LOGGER.info("pokemon.id: {}", pokemon.getId());
        } catch (Exception e) {
            LOGGER.error("Pokemon not found using {}. Exception: {}", pokemonIDName, e.getMessage());
        }
        return pokemon;
    }

    public List<String> getPokemonLocationEncounters(String url)
    {
        HttpResponse<String> response;
        JSONParser jsonParser;
        List<String> areas = new ArrayList<>();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();
            response = HttpClient.newBuilder()
                    .build()
                    .send(request,  HttpResponse.BodyHandlers.ofString());
            LOGGER.info("response: {}", response);
            //jsonParser = new JSONParser(response.body());
            List<LocationEncounterArea> listOfLeas = jsonMapper.readValue(response.body(), new TypeReference<>(){});
            for(LocationEncounterArea lea : listOfLeas) {
                String area = lea.getLocationArea().name();
                areas.add(area);
            }
        } catch (URISyntaxException use) {
            use.printStackTrace();
            LOGGER.error("The url is malformed... {}", use.getMessage());
        } catch (IOException | InterruptedException ioe) {
            ioe.printStackTrace();
            LOGGER.error("There was an error sending the request");
        }
//        catch (ParseException pe) {
//            pe.printStackTrace();
//            LOGGER.error("There was an error parsing the response: {}", pe.getMessage());
//        }
        if (!areas.isEmpty()) {
            areas = areas.stream().sorted().toList();
        }
        return areas;
    }

    public Map<String, Object> getPokemonChainData(String pokemonChainId)
    {
        String chainUrl = "https://pokeapi.co/api/v2/evolution-chain/"+pokemonChainId+'/';
        HttpResponse<String> response;
        JSONParser jsonParser;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(chainUrl))
                    .GET()
                    .build();
            response = HttpClient.newBuilder()
                    .build()
                    .send(request,  HttpResponse.BodyHandlers.ofString());
            LOGGER.info("response: {}", response);
            jsonParser = new JSONParser(response.body());
            return (Map<String, Object>) jsonParser.parse();
        } catch (Exception e) {
            LOGGER.error("Internal Server Error: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }

//    public <T> HttpResponse<T> callUrl(String url)
//    {
//        HttpResponse<T> response = null;
//        try {
//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(new URI(url))
//                    .GET()
//                    .build();
//            response = HttpClient.newBuilder()
//                    .build()
//                    .send(request, (HttpResponse.BodyHandler<T>) HttpResponse.BodyHandlers.ofString());
//            logger.debug("response: {}", response.body());
//            logger.info("callUrl: {} status: {}", url, response.statusCode());
//        } catch (Exception e) {
//            logger.error("Failed to call endpoint: {}", url);
//        }
//        return response;
//    }

//    public List<String> getAllTypes() throws Exception
//    {
//        List<String> types = new ArrayList<>();
//        HttpResponse<String> typeResults = callUrl(pokeApiBaseUrl+"type");
//        if (typeResults.statusCode() == 200) {
//            JSONParser parser = new JSONParser(typeResults.body());
//            LinkedHashMap<String,Object> results = null;
//            try {
//                results = (LinkedHashMap<String,Object>) parser.parse();
//                List<String> finalTypes = types;
//                ((List) results.get("results")).stream()
//                        .forEach(map -> {
//                            LinkedHashMap<String,Object> result = (LinkedHashMap<String,Object>) map;
//                            finalTypes.add((String)result.get("name"));
//                        });
//                types = finalTypes.stream().sorted().toList();
//            } catch (ParseException e) {
//                logger.error("getAllTypes results failed bc {}", e.getMessage());
//            }
//            return types;
//        }
//        else if (typeResults.statusCode() == 400) return new ArrayList<>();
//        else return new ArrayList<>();
//    }

    // TODO: Look at.
    public List<String> getPokemonNamesThatEvolveFromTrading() throws Exception
    {
        String triggerUrl = pokeApiBaseUrl+"evolution-trigger/2/";
        HttpResponse<String> triggerResponse = callUrl(triggerUrl);
        JSONParser parser = new JSONParser(triggerResponse.body());
        LinkedHashMap<String,Object> triggerMap = null;
        List<String> pkmnNames = new ArrayList<>();
        try {
            triggerMap = (LinkedHashMap<String,Object>) parser.parse();
            pkmnNames = ((List)triggerMap.get("pokemon_species")).stream()
                    .map(m -> ((LinkedHashMap<String,Object>)m).get("name").toString())
                    .toList();
        } catch (ParseException e) {
            LOGGER.error("getPokemonNamesThatEvolveFromTrading results failed bc {}", e.getMessage());
        }
        return pkmnNames;
    }

}
