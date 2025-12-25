package pokedex.api;

import pokedex.controllers.BaseController;
import pokedex.service.PokemonService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import skaro.pokeapi.client.PokeApiClient;
import skaro.pokeapi.query.PageQuery;
import skaro.pokeapi.resource.NamedApiResourceList;
import skaro.pokeapi.resource.berry.Berry;
import skaro.pokeapi.resource.berryfirmness.BerryFirmness;
import skaro.pokeapi.resource.berryflavor.BerryFlavor;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/berry")
public class BerryApi extends BaseController
{
    /* Logging instance */
    private static final Logger LOGGER = LogManager.getLogger(BerryApi.class);

    @Autowired
    BerryApi(PokemonService pokemonService, PokeApiClient pokeApiClient)
    {
        super(pokemonService, pokeApiClient);
    }

    @GetMapping(value="")
    @ResponseBody
    public ResponseEntity<?> getAllBerries()
    {
        LOGGER.info("getAllBerries");
        try {
            NamedApiResourceList<Berry> berries = pokeApiClient.getResource(Berry.class).block();
            if (null != berries) return ResponseEntity.ok(berries);
            else return ResponseEntity.badRequest().body("Could not access Berry endpoint");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping(value="/{id}")
    public ResponseEntity<?> getBerry(@PathVariable String id)
    {
        LOGGER.info("getBerry {}", id);
        try {
            Berry berry = pokeApiClient.getResource(Berry.class, id).block();
            if (null != berry) return ResponseEntity.ok(berry);
            else return ResponseEntity.badRequest().body("Could not find a berry with " + id);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/berry-firmness")
    @ResponseBody
    ResponseEntity<?> getBerryFirmness(@RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
                                       @RequestParam(value = "offset", required = false, defaultValue = "0") int offset)
    {
        LOGGER.info("getBerryFirmness");
        try {
            NamedApiResourceList<BerryFirmness> berryFirmnesses = pokeApiClient.getResource(BerryFirmness.class, new PageQuery(limit, offset)).block();
            if (null != berryFirmnesses) return ResponseEntity.ok(berryFirmnesses);
            else return ResponseEntity.badRequest().body("Could not access berry-firmness endpoint");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping(value = "/berry-firmness/{id}")
    ResponseEntity<?> getBerryFirmness(@PathVariable String id)
    {
        LOGGER.info("getBerryFirmness: {}", id);
        try {
            BerryFirmness berryFirmness = pokeApiClient.getResource(BerryFirmness.class, id).block();
            if (null != berryFirmness) return ResponseEntity.ok(berryFirmness);
            else return ResponseEntity.badRequest().body("Could not find berry-firmness with " + id);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/berry-flavor")
    @ResponseBody
    ResponseEntity<?> getBerryFlavors(@RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
                                      @RequestParam(value = "offset", required = false, defaultValue = "0") int offset)
    {
        LOGGER.info("getBerryFlavors");
        try {
            NamedApiResourceList<BerryFlavor> berryFlavors = pokeApiClient.getResource(BerryFlavor.class, new PageQuery(limit, offset)).block();
            if (null != berryFlavors) return ResponseEntity.ok(berryFlavors);
            else return ResponseEntity.badRequest().body("Could not access berry-flavor endpoint");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping(value = "/berry-flavor/{id}")
    ResponseEntity<?> getBerryFlavor(@PathVariable String id)
    {
        LOGGER.info("getBerryFlavor: {}", id);
        try {
            BerryFlavor berryFlavor = pokeApiClient.getResource(BerryFlavor.class, id).block();
            if (null != berryFlavor) return ResponseEntity.ok(berryFlavor);
            return ResponseEntity.badRequest().body("Could not find berry-flavor with " + id);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}
