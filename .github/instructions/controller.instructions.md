---
applyTo: 'src/main/java/pokedex/controllers/*.java'
description: 'Controller Instructions'
---
Apply these instructions when adding or editing controller classes in the project.

- Keep controllers annotated with `@Controller` and use constructor injection with `@Autowired` on the constructor, matching the existing inheritance from `BaseController`.
- Prefer extending `BaseController` for shared behavior such as Pokemon enrichment, dark mode, GIF toggles, pagination state, and fallback API access.
- Use a private static logger via `LogManager.getLogger(...)` in each controller.
- Keep controller methods small and endpoint-focused; delegate enrichment and mapping work to `BaseController` helpers like `createPokemon(...)`, `retrievePokemon(...)`, and `setupPokedex(...)`.
- Preserve the existing endpoint layout and response style:
  - `PokemonListController`: `/`, `/page`, `/pkmnPerPage`, `/getPokemonByType`, `/toggleGifs`, `/toggleDarkmode`
  - `PokedexController`: `/pokedex/{nameOrId}`
  - `EvolutionsController`: `/evolutions/{pokemonId}`
  - `EvolvesHowController`: `pokemonId` request parameter and `evolves-how` fragment rendering
  - `SearchController`: `/search`
- Use `ModelAndView` for server-rendered views and `ResponseEntity` / `@ResponseBody` only for simple JSON or text responses.
- Preserve the two-step species lookup pattern when needed: call `pokemonService.getPokemonSpeciesData(...)` first, then fall back to `pokemonService.callUrl(...)` plus `ObjectMapper` mapping.
- Keep session and UI state changes consistent with the existing services (`DarkmodeService`, `GifService`) and avoid duplicating state logic outside the shared base controller unless necessary.
- Maintain server-rendered AJAX fragment behavior for `pokedex.jsp` and related views; do not switch these flows to client-side rendering unless the whole flow is being redesigned.
- Keep request parameter and path variable names aligned with existing controller and JSP usage (`darkmode`, `pageNumber`, `pkmnPerPage`, `chosenType`, `pokemonId`, `nameOrId`).

