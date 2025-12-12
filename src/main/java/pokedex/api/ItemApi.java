package pokedex.api;

import pokedex.controllers.BaseController;
import pokedex.service.PokemonService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import skaro.pokeapi.client.PokeApiClient;
import skaro.pokeapi.resource.NamedApiResourceList;
import skaro.pokeapi.resource.item.Item;
import skaro.pokeapi.resource.itemattribute.ItemAttribute;
import skaro.pokeapi.resource.itemcategory.ItemCategory;
import skaro.pokeapi.resource.itemflingeffect.ItemFlingEffect;
import skaro.pokeapi.resource.itempocket.ItemPocket;

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

    @GetMapping(value="/item/{id}")
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

    // Attributes
    @GetMapping(value="/list-item-attribute")
    @ResponseBody
    public ResponseEntity<?> getItemAttributes() {
        logger.info("getItemAttributes");
        try {
            NamedApiResourceList<ItemAttribute> itemAttributes = pokeApiClient.getResource(ItemAttribute.class).block();
            if (null != itemAttributes) return ResponseEntity.ok(itemAttributes);
            else return ResponseEntity.badRequest().body("Could not access ItemAttribute endpoint");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping(value="/item-attribute/{id}")
    public ResponseEntity<?> getItemAttribute(@PathVariable(value="id") String id) {
        logger.info("getItemAttribute {}", id);
        try {
            ItemAttribute itemAttribute = pokeApiClient.getResource(ItemAttribute.class, id).block();
            if (null != itemAttribute) return ResponseEntity.ok(itemAttribute);
            else return ResponseEntity.badRequest().body("Could not find an itemAttribute with " + id);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    // Categories
    @GetMapping(value="/list-item-category")
    @ResponseBody
    public ResponseEntity<?> getItemCategories() {
        logger.info("getItemCategories");
        try {
            NamedApiResourceList<ItemCategory> itemCategories = pokeApiClient.getResource(ItemCategory.class).block();
            if (null != itemCategories) return ResponseEntity.ok(itemCategories);
            else return ResponseEntity.badRequest().body("Could not access ItemCategory endpoint");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping(value="/item-category/{id}")
    public ResponseEntity<?> getItemCategory(@PathVariable(value="id") String id) {
        logger.info("getItemCategory {}", id);
        try {
            ItemCategory itemCategory = pokeApiClient.getResource(ItemCategory.class, id).block();
            if (null != itemCategory) return ResponseEntity.ok(itemCategory);
            else return ResponseEntity.badRequest().body("Could not find an itemCategory with " + id);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    // Fling Effects
    @GetMapping(value="/list-item-fling-effect")
    @ResponseBody
    public ResponseEntity<?> getFlingEffects() {
        logger.info("getFlingEffects");
        try {
            NamedApiResourceList<ItemFlingEffect> flingEffects = pokeApiClient.getResource(ItemFlingEffect.class).block();
            if (null != flingEffects) return ResponseEntity.ok(flingEffects);
            else return ResponseEntity.badRequest().body("Could not access ItemFlingEffect endpoint");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping(value="/item-fling-effect/{id}")
    public ResponseEntity<?> getFlingEffect(@PathVariable(value="id") String id) {
        logger.info("getFlingEffect {}", id);
        try {
            ItemFlingEffect flingEffect = pokeApiClient.getResource(ItemFlingEffect.class, id).block();
            if (null != flingEffect) return ResponseEntity.ok(flingEffect);
            else return ResponseEntity.badRequest().body("Could not find a flingEffect with " + id);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    // Pockets
    @GetMapping(value="/list-item-pocket")
    @ResponseBody
    public ResponseEntity<?> getItemPockets() {
        logger.info("getItemPockets");
        try {
            NamedApiResourceList<ItemPocket> itemPocket = pokeApiClient.getResource(ItemPocket.class).block();
            if (null != itemPocket) return ResponseEntity.ok(itemPocket);
            else return ResponseEntity.badRequest().body("Could not access ItemPocket endpoint");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping(value="/item-pocket/{id}")
    public ResponseEntity<?> getItemPocket(@PathVariable(value="id") String id) {
        logger.info("getItemPocket {}", id);
        try {
            ItemPocket itemPocket = pokeApiClient.getResource(ItemPocket.class, id).block();
            if (null != itemPocket) return ResponseEntity.ok(itemPocket);
            else return ResponseEntity.badRequest().body("Could not find a itemPocket with " + id);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}
