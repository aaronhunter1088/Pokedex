//package pokedex.api;
//
//import pokedex.controllers.BaseController;
//import pokedex.service.PokemonService;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import skaro.pokeapi.client.PokeApiClient;
//
//import java.net.http.HttpResponse;
//
//@RestController
//@CrossOrigin(origins = "*")
//@RequestMapping("/machine")
//public class MachineApi extends BaseController
//{
//    /* Logging instance */
//    private static final Logger logger = LogManager.getLogger(MachineApi.class);
//
//    @Autowired
//    public MachineApi(PokemonService pokemonService, PokeApiClient pokeApiClient)
//    {
//        super(pokemonService, pokeApiClient);
//    }
//
//    // Machines
//    @GetMapping(value="")
//    @ResponseBody
//    public ResponseEntity<?> getMachines()
//    {
//        logger.info("getMachines");
//        HttpResponse<String> response = pokemonService.callUrl(pokeApiBaseUrl+"/machine/");
//        if (response.statusCode() == 200) {
//            return ResponseEntity.ok(response.body());
//        } else if (response.statusCode() == 400) {
//            return ResponseEntity.badRequest().build();
//        } else {
//            return ResponseEntity.internalServerError().build();
//        }
//    }
//
//    @GetMapping(value="/{id}")
//    public ResponseEntity<?> getMachine(@PathVariable String id)
//    {
//        logger.info("getLocation {}", id);
//        HttpResponse<String> response = pokemonService.callUrl(pokeApiBaseUrl+"/machine/"+id);
//        if (response.statusCode() == 200) {
//            return ResponseEntity.ok(response.body());
//        } else if (response.statusCode() == 400) {
//            return ResponseEntity.badRequest().build();
//        } else {
//            return ResponseEntity.internalServerError().build();
//        }
//    }
//}
