# pokedexSpringbootApp

This Spring boot application allows you to enter the ID or name of a pokemon
and it will return two images, and details about the Pokemon. If there
is no image available, a pokeball will appear in place of the images.

If the name is mistyped or an ID is not a valid ID for a Pokemon,
an alert will appear. 

I am making calls to https://pokeapi.co/docs/v2.
I am utilizing Java (Spring Boot) with auto caching: pokeapi-reactor 
written by Benjamin Churchill to create a client.
