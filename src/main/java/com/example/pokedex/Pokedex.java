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

    @RequestMapping(value = "/pokemon/{nameOfPokemon}", method=RequestMethod.GET)
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
    public ResponseEntity<Object> getPokemonDescription(@PathVariable("nameOfPokemon") String nameOfPokemon)
    {
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
            return ResponseEntity.badRequest().body(nameOfPokemon + " text was not found!");
        }
    }

    @RequestMapping(value = "/{nameOfPokemon}/color", method=RequestMethod.GET)
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
                }
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(nameOfPokemon + " doesn't have a species!");
            }
        }
        return ResponseEntity.notFound().build();
    }

    @RequestMapping(value="/{nameOfPokemon}/validateName", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Boolean> validateName(@PathVariable("nameOfPokemon") String nameOfPokemon)
    {
        try {
            Pokemon pokemon = pokeApiClient.getResource(Pokemon.class, nameOfPokemon).block();
            System.out.println("valid name: " + nameOfPokemon);
            return ResponseEntity.ok().body(true);
        } catch (Exception e) {
            System.out.println("invalid name: " + nameOfPokemon);
            return ResponseEntity.badRequest().body(false);
        }
    }
}
