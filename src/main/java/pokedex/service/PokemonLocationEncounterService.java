package pokedex.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import static pokedexapi.utilities.Constants.POKEAPI_BASE_API_URL;

@HttpExchange(url = POKEAPI_BASE_API_URL, accept = "application/json")
public interface PokemonLocationEncounterService
{
    @GetExchange(value = "pokemon/{id}/encounters")
    @ResponseBody ResponseEntity<String> getPokemonLocationEncounters(@PathVariable Integer id);
}
