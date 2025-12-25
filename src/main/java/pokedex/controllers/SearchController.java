package pokedex.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pokedex.service.PokemonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import skaro.pokeapi.client.PokeApiClient;

@Controller
public class SearchController extends BaseController
{
    /* Logging instance */
    private static final Logger LOGGER = LogManager.getLogger(SearchController.class);
    @Autowired
    public SearchController(PokemonService pokemonService, PokeApiClient pokeApiClient)
    {
        super(pokemonService, pokeApiClient);
    }

    @GetMapping("/search")
    public ModelAndView searchPage(ModelAndView mav)
    {
        mav.addObject("pokemonId", 0);
        mav.setViewName("search");
        return mav;
    }
}
