package com.example.pokedex;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import skaro.pokeapi.client.PokeApiClient;
import skaro.pokeapi.resource.FlavorText;
import skaro.pokeapi.resource.pokemon.Pokemon;
import skaro.pokeapi.resource.pokemonspecies.PokemonSpecies;

import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/pokedex")
public class Pokedex {

    @Autowired
    private PokeApiClient pokeApiClient;

    public Pokedex(PokeApiClient pokeApiClient) {
        this.pokeApiClient = pokeApiClient;
    }

    @RequestMapping(value = "/{nameOfPokemon}", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getPokemon(@PathVariable("nameOfPokemon") String nameOfPokemon)
    {
        System.out.println("pokemonName: " + nameOfPokemon);
        Pokemon pokemon;
        try {
            pokemon =  pokeApiClient.getResource(Pokemon.class, nameOfPokemon).block();
            return ResponseEntity.ok(pokemon);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(nameOfPokemon + " was not found!");
        }
    }

    @RequestMapping(value="/{nameOfPokemon}/description", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getPokemonInfoText(@PathVariable("nameOfPokemon") String nameOfPokemon)
    {
        System.out.println("pokemonName: " + nameOfPokemon);
        List<FlavorText> pokemonDescriptions;
        try {
            pokemonDescriptions =  pokeApiClient.getResource(PokemonSpecies.class, nameOfPokemon).blockOptional().get()
                    .getFlavorTextEntries().stream().filter(entry -> entry.getLanguage().getName().equals("en"))
                    .toList();
            int randomEntry = new Random().nextInt(pokemonDescriptions.size());
            String description = pokemonDescriptions.get(randomEntry).getFlavorText().replace("\n", " ");
            System.out.println("description: " + description);
            return ResponseEntity.ok(description);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(nameOfPokemon + " text was not found!");
        }
    }

    @RequestMapping(value = "/{nameOfPokemon}/color", method= RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getDesignatedColor(@PathVariable("nameOfPokemon") String nameOfPokemon)
    {
        PokemonSpecies speciesInfo;
        for(int i=1; i!=-1; i++) {
            try {
                speciesInfo = pokeApiClient.getResource(PokemonSpecies.class, nameOfPokemon).block();
                if (speciesInfo != null) {
                    String colorOfPokemon = speciesInfo.getColor().getName();
                    System.out.println("color: " + colorOfPokemon);
                    return ResponseEntity.ok(colorOfPokemon);
                } else {
                    return ResponseEntity.notFound().build();
                }
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("e: " + e.getMessage());
            }
        }

        return ResponseEntity.noContent().build();
    }
}
