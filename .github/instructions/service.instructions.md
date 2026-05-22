---
applyTo: 'src/main/java/pokedex/services/*.java'
description: 'Service Instructions'
---
Apply these instructions when adding or editing service classes in the project.

- Keep services annotated with `@Service` and use a private static logger via `LogManager.getLogger(...)`.
- Follow the existing state-holder pattern used by `DarkmodeService` and `GifService`: a single boolean field, a setter, a getter, and a toggle method when needed.
- Keep service methods small and explicit; avoid adding controller responsibilities or view logic.
- Preserve the current application-wide singleton behavior when storing mutable state, and be careful when introducing new shared state.
- Use clear method names aligned with the stored flag (`setDarkmode` / `isDarkmode` / `toggleDarkMode`, `setShowGifs` / `isShowGifs` / `toggleShowGifs`).
- Keep Javadoc concise and match existing comment style when documenting simple accessors or toggles.
- Maintain existing package naming and file organization conventions in the `pokedex.services` code.
