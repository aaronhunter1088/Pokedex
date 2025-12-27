package pokedex.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.service.registry.ImportHttpServices;
import pokedex.service.PokemonLocationEncounterService;

@Configuration(proxyBeanMethods = false)
@ImportHttpServices({PokemonLocationEncounterService.class})
public class PokemonExchangeConfig
{
}
