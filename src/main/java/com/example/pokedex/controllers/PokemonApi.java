package com.example.pokedex.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.json.JSONParser;
import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import skaro.pokeapi.client.PokeApiClient;
import skaro.pokeapi.query.PageQuery;
import skaro.pokeapi.resource.FlavorText;
import skaro.pokeapi.resource.NamedApiResource;
import skaro.pokeapi.resource.NamedApiResourceList;
import skaro.pokeapi.resource.evolutionchain.EvolutionChain;
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
public class PokemonApi {

    private static final Logger logger = LogManager.getLogger(PokemonApi.class);
    @Autowired
    private PokeApiClient pokeApiClient;

    @RequestMapping(value = "/list", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getAllPokemon(@RequestParam(value="limit", required=false, defaultValue="10") int limit,
                                                @RequestParam(value="offset", required=false, defaultValue="0") int offset) {
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

    // Can be name or id
    @RequestMapping(value = "/{nameOfPokemon}", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getPokemon(@PathVariable("nameOfPokemon") String nameOfPokemon)
    {
        System.out.println("pokemonName: " + nameOfPokemon);
        Pokemon pokemon;
        try {
            pokemon = pokeApiClient.getResource(Pokemon.class, nameOfPokemon).block();
            return ResponseEntity.ok(pokemon);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(nameOfPokemon + " was not found!");
        }
    }

    @RequestMapping(value="/{nameOfPokemon}/description", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getPokemonDescription(@PathVariable("nameOfPokemon") String nameOfPokemon)
    {
        List<FlavorText> pokemonDescriptions;
        try {
            pokemonDescriptions =  pokeApiClient.getResource(PokemonSpecies.class, nameOfPokemon).blockOptional().get()
                    .getFlavorTextEntries().stream().filter(entry -> entry.getLanguage().getName().equals("en"))
                    .toList();
            int randomEntry = new Random().nextInt(pokemonDescriptions.size());
            String description = pokemonDescriptions.get(randomEntry).getFlavorText().replace("\n", " ");
            System.out.println("description: " + description);
            return ResponseEntity.ok(description);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(nameOfPokemon + " text was not found!");
        }
    }

    @RequestMapping(value = "/{nameOfPokemon}/color", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getDesignatedColor(@PathVariable("nameOfPokemon") String nameOfPokemon)
    {
        PokemonSpecies speciesInfo;
        for(int i=1; i!=-1; i++) {
            try {
                speciesInfo = pokeApiClient.getResource(PokemonSpecies.class, nameOfPokemon).block();
                if (speciesInfo != null) {
                    String colorOfPokemon = speciesInfo.getColor().getName();
                    System.out.println("color: " + colorOfPokemon);
                    return new ResponseEntity<>(colorOfPokemon, HttpStatus.OK);
                }
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(nameOfPokemon + " doesn't have a species!");
            }
        }
        return ResponseEntity.notFound().build();
    }

    @RequestMapping(value="/{nameOfPokemon}/validateName", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Boolean> validateName(@PathVariable("nameOfPokemon") String nameOfPokemon)
    {
        try {
            Pokemon pokemon = pokeApiClient.getResource(Pokemon.class, nameOfPokemon).block();
            System.out.println("valid name: " + nameOfPokemon);
            return ResponseEntity.ok().body(true);
        } catch (Exception e) {
            System.out.println("invalid name: " + nameOfPokemon);
            return ResponseEntity.badRequest().body(false);
        }
    }

    @RequestMapping(value="/locations/{nameOfPokemon}", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getLocations(@PathVariable("nameOfPokemon") String nameOfPokemon)
    {
        Pokemon pokemon = pokeApiClient.getResource(Pokemon.class, nameOfPokemon).block();
        if (pokemon == null) return null; //return ResponseEntity.badRequest().build();
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
                    //.send(request, HttpResponse.BodyHandlers.ofString());
                    .send(request,  HttpResponse.BodyHandlers.ofString());
            System.out.println("response: " + response.body());
            jsonParser = new JSONParser(response.body());
            List<LinkedHashMap<String, String>> map = (List<LinkedHashMap<String, String>>) jsonParser.parse();
            for(Map m : map) {
                LinkedHashMap<String, String> area = (LinkedHashMap<String, String>) m.get("location_area");
                namesOfAreas.add(area.get("name"));
            }
            namesOfAreas.forEach(System.out::println);
        } catch (Exception e) {
            e.printStackTrace();
            //return null;
            return ResponseEntity.badRequest().build();
        }

        //return namesOfAreas.toArray(new String[0]);
        JSONArray array = new JSONArray();
        array.addAll(namesOfAreas);
        return new ResponseEntity<>(array.toJSONString(), HttpStatus.OK);
    }

    @RequestMapping(value="/species/{pokemonId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getSpeciesData(@PathVariable("pokemonId") String pokemonId)
    {
        Pokemon pokemonResource = (Pokemon) getPokemon(pokemonId).getBody();
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

    @RequestMapping(value="/evolutionChain/{pokemonId}", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getEvolutionChain(@PathVariable("pokemonId") String pokemonId)
    {
        Map<String, Object> speciesData = getSpeciesData(pokemonId).getBody();
        //JSONParser jsonParser = new JSONParser(String.valueOf(speciesData));
        try {
            //Map<String, Object> species = (Map<String, Object>) jsonParser.parse();
            //jsonParser = new JSONParser(String.valueOf(species.get("evolutionChain")));
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
            logger.error("Error parsing species data");
            return ResponseEntity.internalServerError().build();
        }
    }
}
