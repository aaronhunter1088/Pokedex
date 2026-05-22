# AGENTS Guide - Pokédex

## Project Snapshot

- Spring Boot MVC app with JSP views (`war` packaging) that serves the web UI layer.
- Core Pokemon/domain data access is delegated to `PokedexApi` (`com.skvllprodvctions:PokedexApi` with classifier `code` in `pom.xml`).
- Entry point is `src/main/java/pokedex/PokedexApplication.java`, which imports `MyPokeApiReactorCachingConfiguration` and scans both `pokedex` and `pokedexapi` packages.

## Build, Test, Run

- Build: `./mvnw clean package`
- Test: `./mvnw test`
- Full verification before commit: `./mvnw clean verify`
- Run: `./mvnw spring-boot:run`
- Profile note:
  - `src/main/resources/application.properties` defaults to `production` and `server.servlet.context-path=/`.
  - `src/main/resources/application-local.properties` sets `server.port=4201` and `server.servlet.context-path=/springboot`.

## Architecture and Data Flow

- Controllers in `src/main/java/pokedex/controllers/` extend `BaseController` for shared Pokemon enrichment and pagination/filter state.
- `BaseController#createPokemon(...)` adds UI-specific fields (for example `defaultImage`, `officialImage`, `gifImage`, `description`, `type`, `locations`, `moveNames`) that JSP pages consume directly.
- Species data retrieval uses a two-step fallback pattern in multiple controllers:
  1. `pokemonService.getPokemonSpeciesData(...)`
  2. fallback HTTP call to the species URL + `ObjectMapper` mapping
- Type-filter caching and background prefetch logic lives in `BaseController` (`filteredPokemonByType`, `filteringInProgress`, `startRetroactiveFetchingByType`). Keep this behavior intact when changing filter/pagination flows.

## UI and Routing Conventions

- JSP files live in `src/main/webapp/WEB-INF/jsp/`; view names returned from controllers match these filenames (for example `index`, `pokedex`, `search`, `evolutions`, `evolves-how`).
- Main mapped endpoints are in:
  - `PokemonListController` (`/`, `/page`, `/pkmnPerPage`, `/getPokemonByType`, `/toggleGifs`, `/toggleDarkmode`)
  - `PokedexController` (`/pokedex/{nameOrId}`)
  - `EvolutionsController` (`/evolutions/{pokemonId}`)
  - `SearchController` (`/search`)
- `pokedex.jsp` loads evolution sections via AJAX and expects server-rendered HTML fragments; preserve this server-side rendering pattern when modifying those flows.

## Coding Patterns to Follow

- Use constructor injection as established in controllers/services.
- Use Log4j2 (`LogManager.getLogger(...)`) as in existing classes.
- Keep changes minimal and consistent with the existing mixed Java + JSP + jQuery approach in `src/main/webapp/WEB-INF/jsp/`.
- There are currently no Java tests in `src/test/java`; if you add behavior, prefer adding focused tests alongside the change and still run `./mvnw clean verify`.

