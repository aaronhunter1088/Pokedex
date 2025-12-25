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
import skaro.pokeapi.resource.egggroup.EggGroup;

import java.util.Arrays;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/egg-group")
public class EggGroupApi extends BaseController
{
    /* Logging instance */
    private static final Logger LOGGER = LogManager.getLogger(EggGroupApi.class);

    @Autowired
    EggGroupApi(PokemonService pokemonService, PokeApiClient pokeApiClient)
    {
        super(pokemonService, pokeApiClient);
    }

    // Egg Groups
    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity<?> getEggGroups(@RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
                                   @RequestParam(value = "offset", required = false, defaultValue = "0") int offset)
    {
        LOGGER.info("getEggGroups");
        try {
            NamedApiResourceList<EggGroup> eggGroups = pokeApiClient.getResource(EggGroup.class, new PageQuery(limit, offset)).block();
            if (null != eggGroups) return ResponseEntity.ok(eggGroups);
            else return ResponseEntity.badRequest().body("Could not access EggGroup endpoint");
        } catch (Exception e) {
            Arrays.stream(e.getStackTrace()).forEach(LOGGER::error);
            return ResponseEntity.badRequest().body("Could not fetch all eggGroups because " + e.getMessage());
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity<?> getEggGroup(@PathVariable String id)
    {
        LOGGER.info("getEggGroup {}", id);
        try {
            EggGroup eggGroup = pokeApiClient.getResource(EggGroup.class, id).block();
            if (null != eggGroup) return ResponseEntity.ok(eggGroup);
            else return ResponseEntity.badRequest().body("Could not access EggGroup endpoint");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
