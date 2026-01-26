package pokedex;

import org.jspecify.annotations.NonNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.error.ErrorPageFilter;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
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

    /**
     * Disables ErrorPageFilter to prevent registration conflicts when deploying to external Tomcat.
     * When Spring Boot applications are deployed as WAR files to external servlet containers,
     * the ErrorPageFilter can conflict with the container's error handling mechanism.
     */
    @Bean
    public FilterRegistrationBean<ErrorPageFilter> disableSpringBootErrorFilter(ErrorPageFilter filter)
    {
        FilterRegistrationBean<ErrorPageFilter> filterRegistrationBean = new FilterRegistrationBean<>(filter);
        filterRegistrationBean.setEnabled(false);
        return filterRegistrationBean;
    }

}