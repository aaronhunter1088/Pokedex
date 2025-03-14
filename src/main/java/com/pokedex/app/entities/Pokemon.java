package com.pokedex.app.entities;

import skaro.pokeapi.resource.FlavorText;

import java.util.List;

public class Pokemon extends skaro.pokeapi.resource.pokemon.Pokemon {

    String type;
    String defaultImage;
    String officialImage;
    String gifImage;
    String shinyImage;
    String color;
    List<FlavorText> descriptions;
    String description;
    List<String> locations;
    List<String> moves;

    public Pokemon(skaro.pokeapi.resource.pokemon.Pokemon pokemonResource) {
        super();
        setId(pokemonResource.getId());
        setName(pokemonResource.getName().substring(0,1).toUpperCase()+pokemonResource.getName().substring(1));
        setBaseExperience(pokemonResource.getBaseExperience());
        setHeight(pokemonResource.getHeight());
        setDefault(pokemonResource.isDefault());
        setOrder(pokemonResource.getOrder());
        setWeight(pokemonResource.getWeight());
        setAbilities(pokemonResource.getAbilities());
        setForms(pokemonResource.getForms());
        setGameIndices(pokemonResource.getGameIndices());
        setHeldItems(pokemonResource.getHeldItems());
        setLocationAreaEncounters(pokemonResource.getLocationAreaEncounters());
        setMoves(pokemonResource.getMoves());
        setSprites(pokemonResource.getSprites());
        setSpecies(pokemonResource.getSpecies());
        setStats(pokemonResource.getStats());
        setTypes(pokemonResource.getTypes());
        setPastTypes(pokemonResource.getPastTypes());
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDefaultImage() { return defaultImage; }
    public void setDefaultImage(String defaultImage) { this.defaultImage = defaultImage; }

    public String getOfficialImage() { return officialImage; }
    public void setOfficialImage(String officialImage) { this.officialImage = officialImage; }

    public String getGifImage()  { return gifImage; }
    public void setGifImage(String gifImage) { this.gifImage = gifImage; }

    public String getShinyImage() { return shinyImage; }
    public void setShinyImage(String shinyImage) { this.shinyImage = shinyImage; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public List<FlavorText> getDescriptions() { return descriptions; }
    public void setDescriptions(List<FlavorText> descriptions) { this.descriptions = descriptions; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getLocations() { return locations; }
    public void setLocations(List<String> locations) { this.locations = locations; }

    public List<String> getPokemonMoves() { return moves; }
    public void setPokemonMoves(List<String> moves) { this.moves = moves; }

    public String getCapitalizedColor() {
        return this.getColor().substring(0,1).toUpperCase()
             + this.getColor().substring(1);
    }

    public String getHeightInInches() {
        return String.format("%.0f", this.getHeight() * 3.93701);
    }

    public String getWeightInPounds() {
        return String.format("%.0f", this.getWeight() * 0.220462);
    }

    @Override
    public String toString() {
        return "Pokemon{" +
                "id='" + this.getId() + '\'' +
                ", name='" + this.getName() + '\'' +
                ", baseExperience='" + this.getBaseExperience() + '\'' +
                ", height='" + this.getHeight() + '\'' +
                ", isDefault='" + this.isDefault() + '\'' +
                ", order='" + this.getOrder() + '\'' +
                ", weight='" + this.getWeight() + '\'' +
                ", abilities='" + this.getAbilities() + '\'' +
                ", forms='" + this.getForms() + '\'' +
                ", gameIndices='" + this.getGameIndices() + '\'' +
                ", heldItems='" + this.getHeldItems() + '\'' +
                ", locationAreaEncounters='" + this.getLocationAreaEncounters() + '\'' +
                ", moves='" + this.getPokemonMoves() + '\'' +
                ", sprites='" + this.getSprites() + '\'' +
                ", species='" + this.getSpecies() + '\'' +
                ", stats='" + this.getStats() + '\'' +
                ", types='" + this.getType() + '\'' +
                ", pastTypes='" + this.getPastTypes() + '\'' +
                ", type='" + type + '\'' +
                ", defaultImage='" + defaultImage + '\'' +
                ", officialImage='" + officialImage + '\'' +
                ", gifImage='" + gifImage + '\'' +
                ", color='" + color + '\'' +
                '}';
    }

}
