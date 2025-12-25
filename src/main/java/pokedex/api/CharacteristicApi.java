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
import tools.jackson.databind.json.JsonMapper;

import java.net.http.HttpResponse;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/characteristic")
public class CharacteristicApi extends BaseController
{
    /* Logging instance */
    private static final Logger LOGGER = LogManager.getLogger(CharacteristicApi.class);

    @Autowired
    CharacteristicApi(PokemonService pokemonService, PokeApiClient pokeApiClient)
    {
        super(pokemonService, pokeApiClient);
    }

    // Characteristics
    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity<?> getCharacteristics(@RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
                                         @RequestParam(value = "offset", required = false, defaultValue = "0") int offset)
    {
        LOGGER.info("getCharacteristics");
        HttpResponse<String> characteristics;
        try {
            characteristics = pokemonService.callUrl(pokeApiBaseUrl + "characteristic?limit=" + limit + "&offset=" + offset);
        } catch (Exception e) {
            LOGGER.error("Error retrieving response because {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
        return switch (characteristics.statusCode()) {
            case 200 -> ResponseEntity.ok(characteristics.body());
            case 400 -> ResponseEntity.badRequest().body("Could not access CharacteristicApi endpoint");
            default -> ResponseEntity.internalServerError().body("Could not access CharacteristicApi endpoint");
        };
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity<?> getCharacteristic(@PathVariable String id)
    {
        LOGGER.info("getCharacteristic {}", id);
        try {
            HttpResponse<String> characteristic = pokemonService.callUrl(pokeApiBaseUrl + "characteristic/" + id);
            if (null != characteristic) return ResponseEntity.ok(characteristic.body());
            else return ResponseEntity.badRequest().body("Could not find an characteristic with " + id);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
