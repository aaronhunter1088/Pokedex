# Pokedex - A Spring Boot App

<img width="1582" height="1036" alt="Homepage" src="https://github.com/user-attachments/assets/3288bcc8-bb7c-479b-9669-ccffa563785a" />

# v1.3.0
This version simply updates the application version to match the corresponding
API code's version. We are skipping v1.2.0.

# v1.1.2
This version fixes a bug when filtering Pokemon by type. While the first set
some particular type do show, the 'totalPokemon' value was not reflecting the
actual number of Pokemon of that type. Now, when a type is chosen, all Pokémon
of that type are fetched. A popup informs the user to wait while we gather all
the Pokémon to display. Once completed, Pokémon of just the selected type are
shown on the page. The results are cached for faster retrieval on subsequent
requests of the same type.

# v1.1.1
This version simply includes some previous jars that were never pushed up.

# v1.1.0
This version added the darkmode value passed in from the new Pokedex Landing
Page. This value is either true or false, darkmode or lightmode respectively.
This value is then passed to all other pages, and the CSS is adjusted
accordingly.

# v1.0.3
This version fixed some issues with description, locations, moves, colors,
and other minor issues found.

# v1.0.2
This version addressed a few minor bugs and improved the user interface
for better usability. Also created the bootable jar file for easier
deployment and confirmed its operability.

# v1.0.0 - v1.0.1
This version integrated with PokedexApi code, removing several duplicate
classes and simplifying the codebase. The PokedexApi library now handles
interactions with the PokeAPI, while the Pokedex application focuses on
the user interface and experience. This change enhances maintainability
and leverages the specialized functionality of the PokedexApi library all
while still maintaining its status of being a 100% Spring Boot application.

# v0.6

Upgraded several major dependencies. Update Parent version reference to 1.0.0,
updated Spring Boot from 3.3.1 to 4.0.0, and Jackson from 2.x to 3.x. Updated
file paths to simplify the structure.

# v0.5

This version introduced the usage of the Parent Pom. This pom manages
all the versions used within the project.

# v0.4

This Spring boot application has been upgraded from 2.7.5 to 3.3.1!

The app begins by listing the Pokemon out in a grid
pattern, defaulting to 10 pokemon per page. On the homepage, there will
be a Pokedex image which directs to the search page. Then there is a
pagination list for all pokemon, a GIF toggle, Jump to Page, and Pokemon
Per Page actions. Finally the list of Pokemon is displayed, followed by
one more pagination option.
If you toggle the GIF button, if a Pokemon has a GIF icon, then that icon
will be displayed instead of the default image. When the GIF toggle is not
enabled, if you hover over the default image, if a Pokemon has an official
icon, then that icon will be displayed instead of the default image.
If you enter a page number to jump to, then providing a value greater than
or equal to 1 and less than or equal to the total page count will load that
page of Pokemon, keeping in alignment with the number of pokemon to view per
page, and if the GIF toggle is on or not.
If you enter a count to display Pokemon Per Page, then as long as that number
is less than or equal to 50, that value will be used. If 51 or more is entered
then the value of 50 will be enforced.
Clicking on a Pokemon box will open more details about that selected Pokemon.
On that info page will list all images available on the Pokemon, various
properties such as height and weight, description, locations, moves, and how
that particular Pokemon evolves, if applicable. Finally, it lists all the
stages of that particular Pokemon. If there are variations of a Pokemon, it
is depicted in the same stage.
Finally, the search page lets you search for a particular Pokemon by its name
or id. If an invalid name or ID is given, then no response will be displayed.
Once a valid name or ID is given, the page will display that Pokemon info.

I have also created a Pull Request for the PokeApi-Reactor code to fix
an issue I encountered. This was previously identified and documented as an
issue on the Github account.
My PR: https://github.com/SirSkaro/pokeapi-reactor/pull/10
The Issue: https://github.com/SirSkaro/pokeapi-reactor/issues/8
Submitted on: June 28th, 2024
Merged in on: July 2nd, 2024

# v<=0.3

This Spring boot application allows you to enter the ID or name of a pokemon
and it will return two images, and details about the Pokemon. If there
is no image available, a pokeball will appear in place of the images.

If the name is mistyped or an ID is not a valid ID for a Pokemon,
an alert will appear.

I am making calls to https://pokeapi.co/docs/v2.
I am utilizing Java (Spring Boot) with auto caching: pokeapi-reactor
written by Benjamin Churchill to create a client.
