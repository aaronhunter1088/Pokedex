package pokedex.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pokedex.controllers.BaseController;
import pokedex.service.PokemonService;
import skaro.pokeapi.client.PokeApiClient;
import skaro.pokeapi.query.PageQuery;
import skaro.pokeapi.resource.NamedApiResourceList;
import skaro.pokeapi.resource.generation.Generation;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/generation")
public class GenerationApi extends BaseController
{
    /* Logging instance */
    private static final Logger LOGGER = LogManager.getLogger(GenerationApi.class);

    @Autowired
    GenerationApi(PokemonService pokemonService, PokeApiClient pokeApiClient)
    {
        super(pokemonService, pokeApiClient);
    }

    @GetMapping(value = "")
    @ResponseBody
    ResponseEntity<?> getGenerations(@RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
                                     @RequestParam(value = "offset", required = false, defaultValue = "0") int offset)
    {
        LOGGER.info("getGenerations");
        NamedApiResourceList<Generation> generations = pokeApiClient.getResource(Generation.class, new PageQuery(limit, offset)).block();
        if (null != generations) return ResponseEntity.ok(generations);
        else return ResponseEntity.badRequest().body("Could not access Generation endpoint");
    }

    @GetMapping(value = "/{id}")
    @ResponseBody
    ResponseEntity<?> getGeneration(@PathVariable int id)
    {
        LOGGER.info("getGeneration {}", id);
        Generation generation = pokeApiClient.getResource(Generation.class, String.valueOf(id)).block();
        if (null != generation) return ResponseEntity.ok(generation);
        else return ResponseEntity.badRequest().body("Could not access Generation endpoint");
    }
}
