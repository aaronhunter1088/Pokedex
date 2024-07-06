package com.pokedex.app.api.item;

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
import skaro.pokeapi.resource.berry.Berry;
import skaro.pokeapi.resource.item.Item;

import java.net.http.HttpResponse;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/item")
public class ItemApi extends BaseController {

    private static final Logger logger = LogManager.getLogger(ItemApi.class);
    private final PokeApiClient pokeApiClient;
    @Value("${skaro.pokeapi.baseUri}")
    private String pokeApiBaseUrl;

    @Autowired
    public ItemApi(PokemonService pokemonService, PokeApiClient client) {
        super(pokemonService);
        pokeApiClient = client;
    }

    @GetMapping(value="/list")
    @ResponseBody
    public ResponseEntity<?> getItems() {
        logger.info("getItems");
        try {
            NamedApiResourceList<Item> items = pokeApiClient.getResource(Item.class).block();
            if (null != items) return ResponseEntity.ok(items);
            else return ResponseEntity.badRequest().body("Could not access Item endpoint");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping(value="/{id}")
    public ResponseEntity<?> getItem(@PathVariable(value="id") String id) {
        logger.info("getItem {}", id);
        try {
            Item item = pokeApiClient.getResource(Item.class, id).block();
            if (null != item) return ResponseEntity.ok(item);
            else return ResponseEntity.badRequest().body("Could not find an item with " + id);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
