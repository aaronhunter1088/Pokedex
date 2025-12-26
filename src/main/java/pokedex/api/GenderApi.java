//package pokedex.api;
//
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import pokedex.controllers.BaseController;
//import pokedex.service.PokemonService;
//import skaro.pokeapi.client.PokeApiClient;
//
//import java.net.http.HttpResponse;
//
//@RestController
//@CrossOrigin(origins = "*")
//@RequestMapping("/gender")
//public class GenderApi extends BaseController
//{
//    /* Logging instance */
//    private static final Logger LOGGER = LogManager.getLogger(GenderApi.class);
//
//    @Autowired
//    GenderApi(PokemonService pokemonService, PokeApiClient pokeApiClient)
//    {
//        super(pokemonService, pokeApiClient);
//    }
//
//    // Genders
//    @RequestMapping(value = "", method = RequestMethod.GET)
//    @ResponseBody
//    ResponseEntity<?> getGenders(@RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
//                                 @RequestParam(value = "offset", required = false, defaultValue = "0") int offset)
//    {
//        LOGGER.info("getGenders");
//        HttpResponse<String> genders;
//        try {
//            genders = pokemonService.callUrl(pokeApiBaseUrl + "gender?limit=" + limit + "&offset=" + offset);
//        } catch (Exception e) {
//            LOGGER.error("Error retrieving response because {}", e.getMessage());
//            return ResponseEntity.internalServerError().build();
//        }
//        return switch (genders.statusCode()) {
//            case 200 -> ResponseEntity.ok(genders.body());
//            case 400 -> ResponseEntity.badRequest().body("Could not access Gender endpoint");
//            default -> ResponseEntity.internalServerError().body("Could not access Gender endpoint");
//        };
//    }
//
//    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
//    @ResponseBody
//    ResponseEntity<?> getGender(@PathVariable String id)
//    {
//        LOGGER.info("getGender {}", id);
//        HttpResponse<String> gender;
//        try {
//            gender = pokemonService.callUrl(pokeApiBaseUrl + "gender/" + id);
//        } catch (Exception e) {
//            LOGGER.error("Error retrieving response because {}", e.getMessage());
//            return ResponseEntity.internalServerError().build();
//        }
//        return switch (gender.statusCode()) {
//            case 200 -> ResponseEntity.ok(gender.body());
//            case 400 -> ResponseEntity.badRequest().body("Could not access Gender endpoint");
//            default -> ResponseEntity.internalServerError().body("Could not access Gender endpoint");
//        };
//    }
//}
