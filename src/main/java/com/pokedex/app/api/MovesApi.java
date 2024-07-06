package com.pokedex.app.api;

import com.pokedex.app.controllers.BaseController;
import com.pokedex.app.service.PokemonService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import skaro.pokeapi.client.PokeApiClient;
import skaro.pokeapi.resource.NamedApiResourceList;
import skaro.pokeapi.resource.move.Move;
import skaro.pokeapi.resource.moveailment.MoveAilment;
import skaro.pokeapi.resource.movebattlestyle.MoveBattleStyle;
import skaro.pokeapi.resource.movecategory.MoveCategory;
import skaro.pokeapi.resource.movedamageclass.MoveDamageClass;
import skaro.pokeapi.resource.movelearnmethod.MoveLearnMethod;
import skaro.pokeapi.resource.movetarget.MoveTarget;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/move")
public class MovesApi extends BaseController {

    private static final Logger logger = LogManager.getLogger(MovesApi.class);
    private final PokeApiClient pokeApiClient;
    @Value("${skaro.pokeapi.baseUri}")
    private String pokeApiBaseUrl;

    @Autowired
    public MovesApi(PokemonService pokemonService, PokeApiClient client) {
        super(pokemonService);
        pokeApiClient = client;
    }

    // Moves
    @GetMapping(value="/list")
    @ResponseBody
    public ResponseEntity<?> getMoves() {
        logger.info("getMoves");
        try {
            NamedApiResourceList<Move> moves = pokeApiClient.getResource(Move.class).block();
            if (null != moves) return ResponseEntity.ok(moves);
            else return ResponseEntity.badRequest().body("Could not access Move endpoint");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping(value="/move/{id}")
    public ResponseEntity<?> getMove(@PathVariable(value="id") String id) {
        logger.info("getMove {}", id);
        try {
            Move move = pokeApiClient.getResource(Move.class, id).block();
            if (null != move) return ResponseEntity.ok(move);
            else return ResponseEntity.badRequest().body("Could not find a move with " + id);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    // Move Ailment
    @GetMapping(value="/list-move-ailment")
    @ResponseBody
    public ResponseEntity<?> getMoveAilments() {
        logger.info("getMoveAilments");
        try {
            NamedApiResourceList<MoveAilment> moveAilments = pokeApiClient.getResource(MoveAilment.class).block();
            if (null != moveAilments) return ResponseEntity.ok(moveAilments);
            else return ResponseEntity.badRequest().body("Could not access MoveAilment endpoint");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping(value="/move-ailment/{id}")
    public ResponseEntity<?> getMoveAilment(@PathVariable(value="id") String id) {
        logger.info("getMoveAilment {}", id);
        try {
            MoveAilment moveAilment = pokeApiClient.getResource(MoveAilment.class, id).block();
            if (null != moveAilment) return ResponseEntity.ok(moveAilment);
            else return ResponseEntity.badRequest().body("Could not find a moveAilment with " + id);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    // Battle Styles
    @GetMapping(value="/list-move-battle-style")
    @ResponseBody
    public ResponseEntity<?> getMoveBattleStyles() {
        logger.info("getMoveMoveBattleStyles");
        try {
            NamedApiResourceList<MoveBattleStyle> moveAilments = pokeApiClient.getResource(MoveBattleStyle.class).block();
            if (null != moveAilments) return ResponseEntity.ok(moveAilments);
            else return ResponseEntity.badRequest().body("Could not access MoveBattleStyle endpoint");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping(value="/move-battle-style/{id}")
    public ResponseEntity<?> getMoveBattleStyle(@PathVariable(value="id") String id) {
        logger.info("getMoveBattleStyle {}", id);
        try {
            MoveBattleStyle battleStyle = pokeApiClient.getResource(MoveBattleStyle.class, id).block();
            if (null != battleStyle) return ResponseEntity.ok(battleStyle);
            else return ResponseEntity.badRequest().body("Could not find a battleStyle with " + id);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    // Categories
    @GetMapping(value="/list-category")
    @ResponseBody
    public ResponseEntity<?> getCategories() {
        logger.info("getCategories");
        try {
            NamedApiResourceList<MoveCategory> moveCategories = pokeApiClient.getResource(MoveCategory.class).block();
            if (null != moveCategories) return ResponseEntity.ok(moveCategories);
            else return ResponseEntity.badRequest().body("Could not access MoveCategory endpoint");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping(value="/move-category/{id}")
    public ResponseEntity<?> getCategory(@PathVariable(value="id") String id) {
        logger.info("getMoveBattleStyle {}", id);
        try {
            MoveCategory moveCategory = pokeApiClient.getResource(MoveCategory.class, id).block();
            if (null != moveCategory) return ResponseEntity.ok(moveCategory);
            else return ResponseEntity.badRequest().body("Could not find a moveCategory with " + id);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    // Damage Classes
    @GetMapping(value="/list-damage-class")
    @ResponseBody
    public ResponseEntity<?> getDamageClasses() {
        logger.info("getDamageClasses");
        try {
            NamedApiResourceList<MoveDamageClass> moveDamages = pokeApiClient.getResource(MoveDamageClass.class).block();
            if (null != moveDamages) return ResponseEntity.ok(moveDamages);
            else return ResponseEntity.badRequest().body("Could not access MoveDamageClass endpoint");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping(value="/damage-class/{id}")
    public ResponseEntity<?> getDamageClass(@PathVariable(value="id") String id) {
        logger.info("getMoveDamageClass {}", id);
        try {
            MoveDamageClass moveDamageClass = pokeApiClient.getResource(MoveDamageClass.class, id).block();
            if (null != moveDamageClass) return ResponseEntity.ok(moveDamageClass);
            else return ResponseEntity.badRequest().body("Could not find a moveDamageClass with " + id);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    // Learn Moves
    @GetMapping(value="/list-learn-move")
    @ResponseBody
    public ResponseEntity<?> getLearnMoves() {
        logger.info("getLearnMoves");
        try {
            NamedApiResourceList<MoveLearnMethod> learnMoves = pokeApiClient.getResource(MoveLearnMethod.class).block();
            if (null != learnMoves) return ResponseEntity.ok(learnMoves);
            else return ResponseEntity.badRequest().body("Could not access MoveLearnMethod endpoint");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping(value="/learn-move/{id}")
    public ResponseEntity<?> getMoveLearnMethod(@PathVariable(value="id") String id) {
        logger.info("getMoveLearnMethod {}", id);
        try {
            MoveLearnMethod moveLearnMethod = pokeApiClient.getResource(MoveLearnMethod.class, id).block();
            if (null != moveLearnMethod) return ResponseEntity.ok(moveLearnMethod);
            else return ResponseEntity.badRequest().body("Could not find a moveLearnMethod with " + id);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    // Targets
    @GetMapping(value="/list-move-target")
    @ResponseBody
    public ResponseEntity<?> getMoveTargets() {
        logger.info("getTargets");
        try {
            NamedApiResourceList<MoveTarget> learnMoves = pokeApiClient.getResource(MoveTarget.class).block();
            if (null != learnMoves) return ResponseEntity.ok(learnMoves);
            else return ResponseEntity.badRequest().body("Could not access MoveTarget endpoint");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping(value="/move-target/{id}")
    public ResponseEntity<?> getMoveTarget(@PathVariable(value="id") String id) {
        logger.info("getMoveTarget {}", id);
        try {
            MoveTarget moveTarget = pokeApiClient.getResource(MoveTarget.class, id).block();
            if (null != moveTarget) return ResponseEntity.ok(moveTarget);
            else return ResponseEntity.badRequest().body("Could not find a moveTarget with " + id);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}
