package pokedex.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pokedex.controllers.BaseController;
import pokedex.service.PokemonService;
import skaro.pokeapi.client.PokeApiClient;
import skaro.pokeapi.query.PageQuery;
import skaro.pokeapi.resource.NamedApiResourceList;
import skaro.pokeapi.resource.nature.Nature;
import tools.jackson.databind.json.JsonMapper;

import java.util.Arrays;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/nature")
public class NatureApi extends BaseController
{
    /* Logging instance */
    private static final Logger LOGGER = LogManager.getLogger(NatureApi.class);

    @Autowired
    NatureApi(PokemonService pokemonService, PokeApiClient pokeApiClient)
    {
        super(pokemonService, pokeApiClient);
    }

    // Natures
    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity<?> getNatures(@RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
                                 @RequestParam(value = "offset", required = false, defaultValue = "0") int offset)
    {
        LOGGER.info("getNatures");
        try {
            NamedApiResourceList<Nature> natures = pokeApiClient.getResource(Nature.class, new PageQuery(limit, offset)).block();
            if (null != natures) return ResponseEntity.ok(natures);
            else return ResponseEntity.badRequest().body("Could not access Nature endpoint");
        } catch (Exception e) {
            Arrays.stream(e.getStackTrace()).forEach(LOGGER::error);
            return ResponseEntity.badRequest().body("Could not fetch all Nature because " + e.getMessage());
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity<?> getNature(@PathVariable(value = "id") String id)
    {
        LOGGER.info("getNature {}", id);
        try {
            Nature nature = pokeApiClient.getResource(Nature.class, id).block();
            if (null != nature) return ResponseEntity.ok(nature);
            else return ResponseEntity.badRequest().body("Could not access Nature endpoint");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
