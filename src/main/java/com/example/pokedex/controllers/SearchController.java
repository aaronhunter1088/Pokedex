package com.example.pokedex.controllers;

import com.example.pokedex.service.PokemonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SearchController extends BaseController {

    @Autowired
    public SearchController(PokemonService pokemonService) {
        super(pokemonService);
    }

    @GetMapping("/search")
    public ModelAndView searchPage(ModelAndView mav) {
        mav.addObject("pokemonId", 0);
        mav.setViewName("search");
        return mav;
    }
}
