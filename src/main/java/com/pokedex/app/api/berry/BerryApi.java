package com.pokedex.app.api.berry;

import com.pokedex.app.controllers.BaseController;
import com.pokedex.app.service.PokemonService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import skaro.pokeapi.client.PokeApiClient;
import skaro.pokeapi.resource.NamedApiResourceList;
import skaro.pokeapi.resource.berry.Berry;
import skaro.pokeapi.resource.berryfirmness.BerryFirmness;
import skaro.pokeapi.resource.berryflavor.BerryFlavor;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/berry")
public class BerryApi extends BaseController {

    private static final Logger logger = LogManager.getLogger(BerryApi.class);
    private final PokeApiClient pokeApiClient;

    @Autowired
    public BerryApi(PokemonService pokemonService, PokeApiClient client) {
        super(pokemonService);
        pokeApiClient = client;
    }

    @GetMapping(value="/list")
    @ResponseBody
    public ResponseEntity<?> getAllBerries() {
        logger.info("getAllBerries");
        try {
            NamedApiResourceList<Berry> berries = pokeApiClient.getResource(Berry.class).block();
            if (null != berries) return ResponseEntity.ok(berries);
            else return ResponseEntity.badRequest().body("Could not access Berry endpoint");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping(value="/{id}")
    public ResponseEntity<?> getBerry(@PathVariable(value="id") String id) {
        logger.info("getBerry {}", id);
        try {
            Berry berry = pokeApiClient.getResource(Berry.class, id).block();
            if (null != berry) return ResponseEntity.ok(berry);
            else return ResponseEntity.badRequest().body("Could not find a berry with " + id);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping(value="/berry-firmness/{id}")
    public ResponseEntity<?> getBerryFirmness(@PathVariable(value="id") String id) {
        logger.info("getBerryFirmness: {}", id);
        try {
            BerryFirmness berryFirmness = pokeApiClient.getResource(BerryFirmness.class, id).block();
            if (null != berryFirmness) return ResponseEntity.ok(berryFirmness);
            else return ResponseEntity.badRequest().body("Could not find berry-firmness with " + id);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping(value="/berry-flavor/{id}")
    public ResponseEntity<?> getBerryFlavor(@PathVariable(value="id") String id) {
        logger.info("getBerryFlavor: {}", id);
        try {
            BerryFlavor berryFlavor = pokeApiClient.getResource(BerryFlavor.class, id).block();
            if (null != berryFlavor) return ResponseEntity.ok(berryFlavor);
            return ResponseEntity.badRequest().body("Could not find berry-flavor with " + id);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}
