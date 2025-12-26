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
//import skaro.pokeapi.resource.stat.Stat;
//
//import java.net.http.HttpResponse;
//
//@RestController
//@CrossOrigin(origins = "*")
//@RequestMapping("/stat")
//public class StatApi extends BaseController
//{
//    /* Logging instance */
//    private static final Logger LOGGER = LogManager.getLogger(StatApi.class);
//
//    @Autowired
//    StatApi(PokemonService pokemonService, PokeApiClient client)
//    {
//        super(pokemonService, client);
//    }
//
//    // Pokemon Stats
//    @RequestMapping(value = "", method = RequestMethod.GET)
//    @ResponseBody
//    ResponseEntity<?> getStats(@RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
//                               @RequestParam(value = "offset", required = false, defaultValue = "0") int offset)
//    {
//        LOGGER.info("getPokemonStats limit:{} offset:{}", limit, offset);
//        HttpResponse<String> stats;
//        try {
//            stats = pokemonService.callUrl(pokeApiBaseUrl + "stat?limit=" + limit + "&offset=" + offset);
//        } catch (Exception e) {
//            LOGGER.error("Error retrieving response because {}", e.getMessage());
//            return ResponseEntity.internalServerError().build();
//        }
//        return switch (stats.statusCode()) {
//            case 200 -> ResponseEntity.ok(stats.body());
//            case 400 -> ResponseEntity.badRequest().body("Could not access Stats endpoint");
//            default -> ResponseEntity.internalServerError().body("Could not access Stats endpoint");
//        };
//    }
//
//    @RequestMapping(value = "/{nameOrId}", method = RequestMethod.GET)
//    @ResponseBody
//    ResponseEntity<?> getStat(@PathVariable("nameOrId") String nameOrId)
//    {
//        LOGGER.info("getPokemonStat {}", nameOrId);
//        try {
//            Stat stat = pokeApiClient.getResource(Stat.class, nameOrId).block();
//            if (null != stat) return ResponseEntity.ok(stat);
//            else return ResponseEntity.badRequest().body("Could not access Stat endpoint");
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().body(e.getMessage());
//        }
//    }
//}
