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
import skaro.pokeapi.resource.encountercondition.EncounterCondition;
import skaro.pokeapi.resource.encounterconditionvalue.EncounterConditionValue;
import skaro.pokeapi.resource.encountermethod.EncounterMethod;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/encounter")
public class EncounterApi extends BaseController {

    private static final Logger logger = LogManager.getLogger(EncounterApi.class);
    private final PokeApiClient pokeApiClient;
    @Value("${skaro.pokeapi.baseUri}")
    private String pokeApiBaseUrl;

    // Encounter Method
    @Autowired
    public EncounterApi(PokemonService pokemonService, PokeApiClient client) {
        super(pokemonService);
        pokeApiClient = client;
    }

    @GetMapping(value="/list-encounter-method")
    @ResponseBody
    public ResponseEntity<?> getEncounterMethods() {
        logger.info("getEncounterMethods");
        try {
            NamedApiResourceList<EncounterMethod> encounters = pokeApiClient.getResource(skaro.pokeapi.resource.encountermethod.EncounterMethod.class).block();
            if (null != encounters) return ResponseEntity.ok(encounters);
            else return ResponseEntity.badRequest().body("Could not access EncounterMethod endpoint");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping(value="/encounter-method/{id}")
    public ResponseEntity<?> getEncounterMethod(@PathVariable(value="id") String id) {
        logger.info("getEncounterMethod {}", id);
        try {
            EncounterMethod encounterMethod = pokeApiClient.getResource(EncounterMethod.class, id).block();
            if (null != encounterMethod) return ResponseEntity.ok(encounterMethod);
            else return ResponseEntity.badRequest().body("Could not find an encounterMethod with " + id);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    // Conditions
    @GetMapping(value="/list-condition")
    @ResponseBody
    public ResponseEntity<?> getConditions() {
        logger.info("getConditions");
        try {
            NamedApiResourceList<EncounterCondition> conditions = pokeApiClient.getResource(skaro.pokeapi.resource.encountercondition.EncounterCondition.class).block();
            if (null != conditions) return ResponseEntity.ok(conditions);
            else return ResponseEntity.badRequest().body("Could not access EncounterCondition endpoint");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping(value="/condition/{id}")
    public ResponseEntity<?> getCondition(@PathVariable(value="id") String id) {
        logger.info("getCondition {}", id);
        try {
            EncounterCondition encounterCondition = pokeApiClient.getResource(EncounterCondition.class, id).block();
            if (null != encounterCondition) return ResponseEntity.ok(encounterCondition);
            else return ResponseEntity.badRequest().body("Could not find an encounterCondition with " + id);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    // Condition Values
    @GetMapping(value="/list-condition-value")
    @ResponseBody
    public ResponseEntity<?> getConditionValues() {
        logger.info("getConditionValues");
        try {
            NamedApiResourceList<EncounterConditionValue> conditionValues = pokeApiClient.getResource(skaro.pokeapi.resource.encounterconditionvalue.EncounterConditionValue.class).block();
            if (null != conditionValues) return ResponseEntity.ok(conditionValues);
            else return ResponseEntity.badRequest().body("Could not access EncounterConditionValue endpoint");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping(value="/condition-value/{id}")
    public ResponseEntity<?> getConditionValue(@PathVariable(value="id") String id) {
        logger.info("getConditionValue {}", id);
        try {
            EncounterConditionValue conditionValue = pokeApiClient.getResource(EncounterConditionValue.class, id).block();
            if (null != conditionValue) return ResponseEntity.ok(conditionValue);
            else return ResponseEntity.badRequest().body("Could not find an conditionValue with " + id);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
