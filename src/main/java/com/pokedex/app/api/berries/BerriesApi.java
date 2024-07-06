package com.pokedex.app.api.berries;

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
public class BerriesApi {

    private static final Logger logger = LogManager.getLogger(BerriesApi.class);
    @Autowired
    private PokeApiClient pokeApiClient;

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

    @GetMapping(value="/{nameOrId}")
    public ResponseEntity<?> getBerry(@PathVariable(value="nameOrId") String nameOrId) {
        logger.info("getBerry {}", nameOrId);
        try {
            Berry berry = pokeApiClient.getResource(Berry.class, nameOrId).block();
            if (null != berry) return ResponseEntity.ok(berry);
            else return ResponseEntity.badRequest().body("Could not find a berry with " + nameOrId);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping(value="/berry-firmness/{nameOrId}")
    public ResponseEntity<?> getBerryFirmness(@PathVariable(value="nameOrId") String nameOrId) {
        logger.info("getBerryFirmness: {}", nameOrId);
        try {
            BerryFirmness berryFirmness = pokeApiClient.getResource(BerryFirmness.class, nameOrId).block();
            if (null != berryFirmness) return ResponseEntity.ok(berryFirmness);
            else return ResponseEntity.badRequest().body("Could not find berry-firmness with " + nameOrId);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping(value="/berry-flavor/{nameOrId}")
    public ResponseEntity<?> getBerryFlavor(@PathVariable(value="nameOrId") String nameOrId) {
        logger.info("getBerryFlavor: {}", nameOrId);
        try {
            BerryFlavor berryFlavor = pokeApiClient.getResource(BerryFlavor.class, nameOrId).block();
            if (null != berryFlavor) return ResponseEntity.ok(berryFlavor);
            return ResponseEntity.badRequest().body("Could not find berry-flavor with " + nameOrId);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}
