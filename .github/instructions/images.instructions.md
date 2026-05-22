---
applyTo: 'src/main/webapp/images/*.*'
description: 'Image Instructions'
---
Apply these instructions when adding or modifying images in the project.

- Read the jsp.instructions.md file for JSP specific instructions on how images should be displayed on a jsp page.
- Store all images in the `src/main/webapp/images/` directory to ensure they are correctly served by the web application.
- Use descriptive and consistent naming conventions for image files to improve maintainability (e.g., `pokeball-tab.png`
- instead of `pokeball1.png`). This will help ensure we know what each image is used for and avoid confusion when more
  images are added in the future.

The following are all images today and why they are used. This section should be maintained and updated whenever we
introduce new images or change the purpose of existing ones.

- Homeage.png: Used in the README.md file to show a screenshot of the homepage. It is not used in any of the JSP files.
- pokeball_search.png: Used as the search button and appears when no image is found for a particular Pokémon. It is used in the JSP files.
- pokeball_tabs.png: Used in the tabs of the web page. It is used in the JSP files.
- pokedex.png: Used as a logo, centered on the top of each page. It is used in the JSP files. You can click it as well to load the specific search-for-a-Pokémon web-page.