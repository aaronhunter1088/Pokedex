# Claude Guide - Pokédex

## Update `AGENTS.md` if you update this file. These files should mostly remain identical however you can keep your agent-sepcific notes in the "Claude Specific Notes" section at the bottom of this file.


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

- **Java language level**: Use Java 25 features where appropriate (Streams over manual loops, final local variables, Optional over null returns).
- **Dependency injection**: Use constructor injection with `@Autowired` on the constructor (as in all controllers and services).
- **Logging**: Use Log4j2 (`LogManager.getLogger(...)` as a private static logger) in each component.
- **Controllers**: Extend `BaseController` for shared Pokemon enrichment, pagination, filtering, and UI state. Keep controller methods small; delegate enrichment to `BaseController#createPokemon(...)` and related helpers.
- **Services**: Follow the state-holder pattern used by `DarkmodeService` and `GifService` (single boolean field, setter/getter, toggle when needed).
- **JSP views**: Use JSTL tags (`<c:if>`, `<c:forEach>`, etc.) and Jakarta taglibs; avoid scriptlets. Use `${pageContext.request.contextPath}` for paths. Preserve server-rendered AJAX fragment patterns (e.g., `pokedex.jsp` loading evolution sections).
- **Frontend**: Keep changes consistent with the existing mixed jQuery + inline JavaScript approach. Use semantic `alt` text for images and match current ID conventions used by jQuery handlers.
- **Testing**: Currently no Java tests exist in `src/test/java`; if you add behavior, create focused JUnit 5 tests alongside and run `./mvnw clean verify`.

## Instruction Files by Topic

Refer to files in `.github/instructions/` for detailed guidance on specific aspects:
- **Controllers**: `controller.instructions.md` — when wiring controllers, extending BaseController, endpoint layout, AJAX fragments.
- **Services**: `service.instructions.md` — when adding stat-holder services or modifying existing state logic.
- **Java code**: `java.instructions.md` — language level, style conventions, method size guidance.
- **JSP templates**: `jsp.instructions.md` — taglib usage, path construction, dark mode/GIF toggle bindings, server rendering patterns.
- **Images**: `images.instructions.md` — storage location, naming conventions, current image purposes.
- **Build**: `build.instructions.md` — dependency management guidelines in `pom.xml`.
- **Testing**: `test.instructions.md` — JUnit 5 patterns, assertion styles, parameterized tests.
- **Git commits**: `.github/git-commit-instructions.md` — message format and conventions (e.g., `feat/add_new_feature`, `fix/bug_description`).

## Claude Specific Notes

None at the moment. Add as necessary.