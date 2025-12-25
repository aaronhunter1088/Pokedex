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
import skaro.pokeapi.resource.growthrate.GrowthRate;
import tools.jackson.databind.json.JsonMapper;

import java.util.Arrays;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/growth-rate")
public class GrowthRateApi extends BaseController
{
    /* Logging instance */
    private static final Logger LOGGER = LogManager.getLogger(GrowthRateApi.class);

    @Autowired
    GrowthRateApi(PokemonService pokemonService, PokeApiClient client)
    {
        super(pokemonService, client);
    }

    // Growth Rate
    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity<?> getGrowthRates(@RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
                                     @RequestParam(value = "offset", required = false, defaultValue = "0") int offset)
    {
        LOGGER.info("getGrowthRates");
        try {
            NamedApiResourceList<GrowthRate> growthRates = pokeApiClient.getResource(GrowthRate.class, new PageQuery(limit, offset)).block();
            if (null != growthRates) return ResponseEntity.ok(growthRates);
            else return ResponseEntity.badRequest().body("Could not access GrowthRate endpoint");
        } catch (Exception e) {
            Arrays.stream(e.getStackTrace()).forEach(LOGGER::error);
            return ResponseEntity.badRequest().body("Could not fetch all GrowthRate because " + e.getMessage());
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity<?> getGrowthRate(@PathVariable String id)
    {
        LOGGER.info("getGrowthRate {}", id);
        try {
            GrowthRate growthRate = pokeApiClient.getResource(GrowthRate.class, id).block();
            if (null != growthRate) return ResponseEntity.ok(growthRate);
            else return ResponseEntity.badRequest().body("Could not access GrowthRate endpoint");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
