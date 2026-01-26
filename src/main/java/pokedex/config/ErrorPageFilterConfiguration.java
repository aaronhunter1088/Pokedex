package pokedex.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.error.ErrorPageFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration to prevent ErrorPageFilter registration conflicts when deploying to external Tomcat.
 * 
 * When deploying Spring Boot applications as WAR files to external servlet containers like Tomcat,
 * the ErrorPageFilter can conflict with the container's error handling. This configuration disables
 * the automatic registration of the ErrorPageFilter.
 */
@Configuration
public class ErrorPageFilterConfiguration {

    /**
     * Disables the ErrorPageFilter registration to prevent conflicts with external Tomcat.
     * 
     * @return FilterRegistrationBean with enabled=false
     */
    @Bean
    public FilterRegistrationBean<ErrorPageFilter> disableSpringBootErrorFilter(ErrorPageFilter filter) {
        FilterRegistrationBean<ErrorPageFilter> filterRegistrationBean = new FilterRegistrationBean<>(filter);
        filterRegistrationBean.setEnabled(false);
        return filterRegistrationBean;
    }
}
