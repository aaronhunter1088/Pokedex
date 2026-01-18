package pokedex.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import pokedexapi.service.PokemonApiService;
import skaro.pokeapi.client.PokeApiClient;

@Controller
public class SearchController extends BaseController
{
    /* Logging instance */
    private static final Logger LOGGER = LogManager.getLogger(SearchController.class);

    @Autowired
    public SearchController(PokemonApiService pokemonService,
                            PokeApiClient pokeApiClient)
    {
        super(pokemonService, pokeApiClient, null);
    }

    @GetMapping("/search")
    public ModelAndView searchPage(ModelAndView mav,
                                   @RequestParam(name = "darkmode", required = true, defaultValue = "false") String darkmode)
    {
        mav.addObject("pokemonId", 0);
        mav.addObject("isDarkMode", isDarkMode = darkmode.equals("true"));
        mav.setViewName("search");
        return mav;
    }
}
