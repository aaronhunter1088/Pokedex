# Copilot Instructions for Pokédex

This is a Spring Boot-based Pokédex web application that displays Pokémon information retrieved from the PokéAPI. The application uses JSP templates for the frontend and communicates with the PokeAPI via the pokeapi-reactor client library.

## Code Standards

### Required Before Each Commit
- Run `./mvnw clean verify` before committing any changes to ensure code compiles and tests pass
- Follow existing code formatting and style conventions in the codebase

### Development Flow
- Build: `./mvnw clean package`
- Test: `./mvnw test`
- Run locally: `./mvnw spring-boot:run` (runs on port 4201)
- Full CI check: `./mvnw clean verify`

## Repository Structure
- `src/main/java/pokedex/`: Main Java source code
  - `PokedexApplication.java`: Spring Boot application entry point
  - `controllers/`: Spring MVC controllers for handling web requests
- `src/main/resources/`: Configuration files
  - `application.properties`: Main application configuration
  - `application-*.properties`: Environment-specific configurations
  - `log4j2.properties`: Logging configuration
- `src/main/webapp/`: Web application resources
  - `WEB-INF/jsp/`: JSP template files
  - `images/`: Static image resources
  - `resources/`: Static web resources
- `pom.xml`: Maven project configuration

## Key Guidelines
1. Follow Java best practices and Spring Boot conventions
2. Maintain existing code structure and organization
3. Use Spring dependency injection patterns
4. Write unit tests for new functionality using JUnit 5
5. Document public APIs and complex logic with Javadoc
6. Use Log4j2 for logging (logger instances via `LogManager.getLogger()`)

## Technology Stack
- Java with Spring Boot 4.0.0
- Maven for build management (use Maven Wrapper: `./mvnw`)
- JSP (JavaServer Pages) for view templates
- JSTL (JSP Standard Tag Library) for template logic
- PokeAPI-Reactor for external API calls
- JUnit 5 for testing
- Log4j2 for logging

## External Dependencies
- The application retrieves Pokémon data from https://pokeapi.co/api/v2/
- Uses the `skaro.pokeapi:pokeapi-reactor` library for API communication
- Depends on a parent POM (`com.skvllprodvctions:parent:1.0.0`)

## Notes
- The application runs on port 4201 with context path `/springboot` (access at http://localhost:4201/springboot)
- WAR packaging is used for deployment
- Caching is enabled for API responses via `MyPokeApiReactorCachingConfiguration`
