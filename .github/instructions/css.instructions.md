---
applyTo: 'src/main/webapp/resources/css/*.css'
description: 'Cascading Style Sheets Instructions'
---
Apply these instructions when editing CSS files.

- Follow existing desktop-first structure with mobile overrides inside `@media screen and (max-width: 650px)`.
- Preserve shared layout patterns already used across files:
  - grid containers (`display: grid`, `grid-gap`, `grid-template-columns`)
  - card/tile styling (`.box` shadow, fixed/min heights)
  - mobile slide-out behavior (`.mobile-menu`, `.mobile-menu-overlay`, `.active` state)
- Keep dark mode compatibility by scoping overrides with `.darkmode ...` selectors where needed.
- Maintain compatibility with existing JSP/JS hooks by reusing current selectors and IDs/classes (`.list-grid`, `.pokedex-grid`, `#evolutions`, `#nameAndID`, `.desktop-controls`, `.mobile-header`, `.mobile-menu-item`).
- Use `!important` only when required for responsive overrides or theme conflicts (as currently done in mobile and dark mode rules).
- Keep existing vendor-prefixed properties when present (`-webkit-transition`, `-webkit-transform`, `-webkit-overflow-scrolling`) and pair them with standard properties.
- Preserve scrollbar customization patterns (`::-webkit-scrollbar`, `::-webkit-scrollbar-thumb`) when editing scrollable sections.
- Keep comments concise and practical, especially around responsive behavior and UI constraints.
