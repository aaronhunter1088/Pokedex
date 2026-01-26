package pokedex;

import org.jspecify.annotations.NonNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;
import pokedexapi.config.MyPokeApiReactorCachingConfiguration;

@Import(MyPokeApiReactorCachingConfiguration.class)
@EnableConfigurationProperties
@SpringBootApplication(scanBasePackages = {"pokedex", "pokedexapi"})
public class PokedexApplication extends SpringBootServletInitializer
{

    public static void main(String[] args)
    {
        SpringApplication.run(PokedexApplication.class, args);
    }

    @NonNull
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder)
    {
        return builder.sources(PokedexApplication.class);
    }

}