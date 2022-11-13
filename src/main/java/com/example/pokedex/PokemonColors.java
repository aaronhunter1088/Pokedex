package com.example.pokedex;

import org.springframework.cache.support.CompositeCacheManager;
import reactor.netty.http.client.HttpClient;
import skaro.pokeapi.PokeApiConfigurationProperties;
import skaro.pokeapi.PokeApiReactorBaseConfiguration;
import skaro.pokeapi.PokeApiReactorCachingConfiguration;
import skaro.pokeapi.PokeApiReactorEndpointConfiguration;
import skaro.pokeapi.client.PokeApiClient;
import skaro.pokeapi.client.PokeApiEntityFactory;
import skaro.pokeapi.resource.pokemoncolor.PokemonColor;

import java.net.URI;

public class PokemonColors {

    private static HttpClient http = new MyPokeApiReactorCachingConfiguration().httpClient();
    private static PokeApiReactorCachingConfiguration cacheConfig = new PokeApiReactorCachingConfiguration();
    private static PokeApiReactorBaseConfiguration baseConfig = new PokeApiReactorBaseConfiguration();
    private static PokeApiConfigurationProperties properties = new PokeApiConfigurationProperties();
    private static PokeApiReactorEndpointConfiguration endpointConfiguration = new PokeApiReactorEndpointConfiguration();

    private static PokeApiEntityFactory factory;
    private static PokeApiClient client;

    static {
        properties.setBaseUri(URI.create("https://pokeapi.co/api/v2/"));
        factory = baseConfig.pokeApiEntityFactory(baseConfig.webClient(http, baseConfig.jsonEncoder(), baseConfig.jsonDecoder(), properties),
                endpointConfiguration.endpointRegistry());

        client = cacheConfig.pokeApiClient(factory, cacheConfig.cacheFacade(new CompositeCacheManager()));
    }

    public static void main(String[] args) {
        PokemonColor pkmnColor;
        for(int i=1; i!=-1; i++) {
            try {
                pkmnColor = client.getResource(PokemonColor.class, String.valueOf(i)).block();
                if (pkmnColor != null) System.out.println(pkmnColor.getName());
            } catch (Exception e) {
                i = -2;
            }
        }
    }
}
