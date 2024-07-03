package com.example.pokedex.service;

import com.fasterxml.jackson.annotation.JacksonInject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import skaro.pokeapi.client.PokeApiClient;
import skaro.pokeapi.query.PageQuery;
import skaro.pokeapi.resource.*;
import skaro.pokeapi.resource.egggroup.EggGroup;
import skaro.pokeapi.resource.evolutionchain.EvolutionChain;
import skaro.pokeapi.resource.generation.Generation;
import skaro.pokeapi.resource.growthrate.GrowthRate;
import skaro.pokeapi.resource.location.Location;
import skaro.pokeapi.resource.locationarea.LocationArea;
import skaro.pokeapi.resource.pokedex.Pokedex;
import skaro.pokeapi.resource.pokemon.Pokemon;
import skaro.pokeapi.resource.pokemoncolor.PokemonColor;
import skaro.pokeapi.resource.pokemonhabitat.PokemonHabitat;
import skaro.pokeapi.resource.pokemonshape.PokemonShape;
import skaro.pokeapi.resource.pokemonspecies.Genus;
import skaro.pokeapi.resource.pokemonspecies.PokemonSpecies;
import skaro.pokeapi.resource.pokemonspecies.PokemonSpeciesDexEntry;
import skaro.pokeapi.resource.pokemonspecies.PokemonSpeciesVariety;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

@Service(value="PokemonService")
public class PokemonService {

    private static final Logger logger = LogManager.getLogger(PokemonService.class);
    private final PokeApiClient pokeApiClient;
    private int savedPageNumber = 1;
    private int pokemonID = 0;
    private int pkmnPerPage = 10; // itemsPerPage

    @Autowired
    private PokemonService(PokeApiClient pokeApiClient) {
        this.pokeApiClient = pokeApiClient;
    }

    public NamedApiResourceList<Pokemon> getPokemonList(int _limit, int offset) {
//    const interval = {
//                limit: _limit,
//                offset: _offset
//    }
        //return this.Pokedex.getPokemonsList(interval);
        if (_limit > 0) {
            PageQuery query = new PageQuery(_limit, offset);
            NamedApiResourceList<Pokemon> results = pokeApiClient.getResource(Pokemon.class, query).block();
            return results;
        } else {
            return pokeApiClient.getResource(Pokemon.class).block();
        }
    }

    public Pokemon getPokemonByName(String pokemonIDName) {
        Pokemon pokemon = null;
        try {
            pokemon = pokeApiClient.getResource(Pokemon.class, pokemonIDName).block();
            if (null != pokemon) logger.info("pokemon.id: {}", pokemon.getId());
        } catch (Exception e) {
            logger.error("Pokemon not found using {}. Exception: {}", pokemonIDName, e.getMessage());
        }
        return pokemon;
    }

    public Pokemon getPokemonSpecificData(String pokemonName) {
        return getPokemonByName(pokemonName);
    }

    public PokemonSpecies getPokemonSpeciesData(String id) throws Exception {
        return pokeApiClient.getResource(PokemonSpecies.class, id).block();
    }

    public List<String> getPokemonLocationEncounters(String url) {
        //return pokeApiClient.getResource(Location.class, id).block();
        HttpResponse<String> response;
        JSONParser jsonParser;
        List<String> areas = new ArrayList<>();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();
            response = HttpClient.newBuilder()
                    .build()
                    .send(request,  HttpResponse.BodyHandlers.ofString());
            logger.info("response: {}", response);
            jsonParser = new JSONParser(response.body());
            List<LinkedHashMap<String, String>> map = (List<LinkedHashMap<String, String>>) jsonParser.parse();
            for(Map m : map) {
                LinkedHashMap<String, String> area = (LinkedHashMap<String, String>) m.get("location_area");
                areas.add(area.get("name"));
            }
        } catch (URISyntaxException use) {
            use.printStackTrace();
            logger.error("The url is malformed... {}", use.getMessage());
        } catch (IOException | InterruptedException ioe) {
            ioe.printStackTrace();
            logger.error("There was an error sending the request");
        } catch (ParseException pe) {
            pe.printStackTrace();
            logger.error("There was an error parsing the response: {}", pe.getMessage());
        }
        if (!areas.isEmpty()) {
            areas = areas.stream().sorted().toList();
        }
        return areas;
    }

    public Map<String, Object> getPokemonChainData(String pokemonChainId) {
        //PokemonSpecies speciesData = getPokemonSpeciesData(pokemonId);
        String chainUrl = "https://pokeapi.co/api/v2/evolution-chain/"+pokemonChainId+'/';
        HttpResponse<String> response;
        JSONParser jsonParser;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(chainUrl))
                    .GET()
                    .build();
            response = HttpClient.newBuilder()
                    .build()
                    .send(request,  HttpResponse.BodyHandlers.ofString());
            logger.info("response: {}", response);
            jsonParser = new JSONParser(response.body());
            return (Map<String, Object>) jsonParser.parse();
        } catch (Exception e) {
            logger.error("Internal Server Error: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }

    public int getTotalPokemon(String pokedex) {
        return pokeApiClient.getResource(Pokedex.class, Objects.requireNonNullElse(pokedex, "1")).block().getPokemonEntries().size();
    }

    public void saveCurrentPage(int page) {
        logger.info("saving page: {}", page);
        this.savedPageNumber = page;
    }

    public int getSavedPage() {
        return this.savedPageNumber;
    }

    public void savePokemonID(int pokemonID) {
        logger.info("saving pokemonID: {}", pokemonID);
        this.pokemonID = pokemonID;
    }

    public void saveNumberOfPokemonPerPage(int pkmnPerPage) {
        logger.info("saving number of pokemon visible to {}", pkmnPerPage);
        this.pkmnPerPage = pkmnPerPage;
    }

    public int getNumberOfPokemonPerPage() {
        return this.pkmnPerPage;
    }

    public Map<Integer, List<List<Integer>>> getEvolutionsMap() {
        return new TreeMap<>() {{
            put(1, List.of(List.of(1), List.of(2), List.of(3, 10033, 10195))); // bulbasaur, ivysaur, venusaur, venusaur-mega, venusaur-gmax
            put(2, List.of(List.of(4), List.of(5), List.of(6, 10034, 10035, 10196))); // squirtle, wartortle, blastoise, blastoise-mega, blastoise-gmax
            put(3, List.of(List.of(7), List.of(8), List.of(9, 10036, 10197))); // charmander, charmeleon, charizard, charizard-mega-x, charizard-mega-y charizard-gmax
            put(4, List.of(List.of(10), List.of(11), List.of(12, 10198))); // caterpie, metapod, butterfree, butterfree-gmax
            put(5, List.of(List.of(13), List.of(14), List.of(15, 10090))); // weedle, kakuna, beedrill, beedrill-mega
            put(6, List.of(List.of(16), List.of(17), List.of(18, 10073))); // pidgey, pideotto, pideot, pideot-mega
            put(7, List.of(List.of(19, 10091), List.of(20, 10092, 10093))); // rattata, rattata-alola, raticate, raticate-alola, raticate-totem-alola
            put(8, List.of(List.of(21), List.of(22))); // spearow, fearow
            put(9, List.of(List.of(23), List.of(24))); // ekans, arbok
            put(10, List.of(List.of(172), List.of(25, 10199), List.of(26, 10100))); // pichu, pikachu, pikachu-gmax, raichu, raichu-alola
            // pikachu also has ID: 10080-10085,10094-10099,10148,10158,10160
            put(11, List.of(List.of(27, 10101), List.of(28, 10102))); //sandrew, sandrew-alola, sandslash, sandslash-alola
            put(12, List.of(List.of(29), List.of(30), List.of(31))); // nidoran-f, nidorina, nidoqueen
            put(13, List.of(List.of(32), List.of(33), List.of(34))); // nidoran-m, nidorino, nidoking
            put(14, List.of(List.of(173), List.of(35), List.of(36))); // cleffa, clefairy, clefable
            put(15, List.of(List.of(37, 10103), List.of(38, 10104))); // vulpix, vulpix-alola, ninetales, ninetales-alola
            put(16, List.of(List.of(174), List.of(39), List.of(40))); // igglybuff, jigglypuff, wigglytuff
            put(17, List.of(List.of(41), List.of(42), List.of(169))); // zubat, golbat, crobat
            put(18, List.of(List.of(43), List.of(44), List.of(45, 182))); // oddish, gloom, vileplume, bellossom
            put(19, List.of(List.of(46), List.of(47))); // paras, parasect
            put(20, List.of(List.of(48), List.of(49))); // venonat, venomoth
            put(21, List.of(List.of(50, 10105), List.of(51, 10106))); // diglett, diglett-alola, dugtrio, dugtrio-alola
            put(22, List.of(List.of(52, 10107, 10200, 10161), List.of(863, 53, 10108))); // meowth, meowth-gmax, perrserker, meowth-galar, persian, persian-alola
            put(23, List.of(List.of(54), List.of(55))); // psyduck, golduck
            put(24, List.of(List.of(56), List.of(57))); // mankey, primeape
            put(25, List.of(List.of(58, 10229), List.of(59, 10230))); // growlithe, growlithe-hisui, arcanine, arcanine-hisui
            put(26, List.of(List.of(60), List.of(61), List.of(62, 186))); // poliwag, poliwhirl, poliwrath, politoed
            put(27, List.of(List.of(63), List.of(64), List.of(65, 10037))); // abra, kadabra, alakazam, alakazam-mega
            put(28, List.of(List.of(66), List.of(67), List.of(68, 10201))); // machop, machoke, machamp, machamp-gmax
            put(29, List.of(List.of(69), List.of(70), List.of(71))); // bellsprout, weepinbell, victreebel
            put(30, List.of(List.of(72), List.of(73))); // tentacool, tentacruel
            put(31, List.of(List.of(74, 10109), List.of(75, 10110), List.of(76, 10111))); // geodude, geodude-alola, graveler, graveler-alola, golem, golem-alola
            put(32, List.of(List.of(77, 10162), List.of(78, 10163))); // ponyta, ponyta-galar, rapidash, rapidash-galar
            put(33, List.of(List.of(79, 10164), List.of(80, 10165, 10071), List.of(199, 10172))); // slowpoke, slowpoke-galar, slowbro, slowbro-galar, slowbro-mega, slowking, slowking-galar
            put(34, List.of(List.of(81), List.of(82), List.of(462))); // magnemite, magneton, magnezone
            put(35, List.of(List.of(83, 10166), List.of(865))); // farfetchd, farfetchd-galar, Sirfetchd
            put(36, List.of(List.of(84), List.of(85))); // doduo, dodrio
            put(37, List.of(List.of(86), List.of(87))); // seel, dewgong
            put(38, List.of(List.of(88, 10112), List.of(89, 10113))); // grimer, grimer-alola, muk, muk-alola
            put(39, List.of(List.of(90), List.of(91))); // shellder, cloyster
            put(40, List.of(List.of(92), List.of(93), List.of(94, 10038, 10202))); // gastly, haunter, gengar, gengar-mega, gengar-gmax
            put(41, List.of(List.of(95), List.of(208, 10072))); // onix, steelix, steelix-mega
            put(42, List.of(List.of(96), List.of(97))); // drowzee, hypno
            put(43, List.of(List.of(98), List.of(99, 10203))); // krabby, kingler, kingler-gmax
            put(44, List.of(List.of(100, 10231), List.of(101, 10232))); // voltorb, voltorb-hisui, electrode, electrode-hisui
            put(45, List.of(List.of(102), List.of(103, 10114))); // exeggcute, exeggutor,exeggutor-alola
            put(46, List.of(List.of(104), List.of(105, 10115, 10149))); // cubone, marowak, marowak-alola, marowak-totem
            put(47, List.of(List.of(236), List.of(106, 107, 237))); // tyroge, hitmonlee, hitmonchan, hitmontop
            put(48, List.of(List.of(108), List.of(463))); // lickitung, lickilicky
            put(49, List.of(List.of(109), List.of(110, 10167))); // koffing, weezing, weezing-galar
            put(50, List.of(List.of(111), List.of(112), List.of(464))); // rhyhorn, rhydon, rhyperior
            put(51, List.of(List.of(440), List.of(113), List.of(242))); // happiny, chansey, blissey
            put(52, List.of(List.of(114), List.of(465))); // tangela, tangrowth
            put(53, List.of(List.of(115, 10039))); // kangaskhan, kangaskhan-mega
            put(54, List.of(List.of(116), List.of(117), List.of(230))); // horsea, seadra, kingdra
            put(55, List.of(List.of(118), List.of(119))); // goldeen, seaking
            put(56, List.of(List.of(120), List.of(121))); // staryu, starmie
            put(57, List.of(List.of(439), List.of(122, 10168), List.of(866))); // mime-jr, mr-mime, mr-mime-galar, mr-rime
            put(58, List.of(List.of(123), List.of(212, 10046, 900))); // scyther, scizor(trade with metal coat), scizor-mega, kleavor(use black augurite)
            put(59, List.of(List.of(238), List.of(124))); // smoochum, jynx
            put(60, List.of(List.of(239), List.of(125), List.of(466))); // elekid, electabuzz, electivire
            put(61, List.of(List.of(240), List.of(126), List.of(467))); // magby, magmar, magmortar
            put(62, List.of(List.of(127, 10040))); // pinsir, pinsir-mega
            put(63, List.of(List.of(128))); // tauros
            put(64, List.of(List.of(129), List.of(130, 10041))); // magikarp, gyarados, gyarados-mega
            put(65, List.of(List.of(131, 10204))); // lapras, lapras-gmax
            put(66, List.of(List.of(132))); // ditto
            put(67, List.of(List.of(133, 10205), List.of(134, 135, 136, 196, 197, 470, 471, 700)));
            // eevee, eevee-gmax, vaporeon, jolteon, flareon, espeon, umbreon(197)
            // eevee also ID: 10159
            put(68, List.of(List.of(137), List.of(233), List.of(474))); // porygon, porygon2, porygon-z
            put(69, List.of(List.of(138), List.of(139))); // omanyte, omastar
            put(70, List.of(List.of(140), List.of(141))); // kabuto, kabutops
            put(71, List.of(List.of(142, 10042))); // aerodactyle, aerodactyl-mega
            put(72, List.of(List.of(446), List.of(143, 10206))); // munchlax, snorlax, snorlax-gmax
            put(73, List.of(List.of(144, 10169))); // articuno, articuno-galar
            put(74, List.of(List.of(145, 10170))); // zapdos, zapdos-galar
            put(75, List.of(List.of(146, 10171))); // moltres, moltres-galar
            put(76, List.of(List.of(147), List.of(148), List.of(149))); // dratini, dragonair, dragonite
            put(77, List.of(List.of(150, 10043, 10044))); // mewtwo, mewtwo-mega-x, mewtwo-mega-y
            put(78, List.of(List.of(151))); // mew
            // /* End of Generation 1 */
            put(79, List.of(List.of(152), List.of(153), List.of(154))); // chikorita, bayleef, meganium
            put(80, List.of(List.of(155), List.of(156), List.of(157, 10233))); // cyndaquil, quilava, typhlosion, typhlosion-hisui
            put(81, List.of(List.of(158), List.of(159), List.of(160))); // totodile, croconaw, feraligatr
            put(82, List.of(List.of(161), List.of(162))); // sentret, furret
            put(83, List.of(List.of(163), List.of(164))); // hoothoot, noctowl
            put(84, List.of(List.of(165), List.of(166))); // ledyba, ledian
            put(85, List.of(List.of(167), List.of(168))); // spinarak, ariados
            put(86, List.of(List.of(170), List.of(171))); // chinchou, lanturn
            put(87, List.of(List.of(175), List.of(176), List.of(468))); // togepi, togetic, togekiss
            put(88, List.of(List.of(177), List.of(178))); // natu, xatu
            put(89, List.of(List.of(179), List.of(180), List.of(181, 10045))); // mareep, flaaffy, ampharos, ampharos-mega
            put(90, List.of(List.of(298), List.of(183), List.of(184))); // azurill, marill, azumarill
            put(91, List.of(List.of(438), List.of(185))); // bonsly, sudowoodo
            put(92, List.of(List.of(187), List.of(188), List.of(189))); // hoppip, skiploom, jumpluff
            put(93, List.of(List.of(190), List.of(424))); // aipom, ambipom
            put(94, List.of(List.of(191), List.of(192))); // sunkern, sunflora
            put(95, List.of(List.of(193), List.of(469))); // yanma, yanmega
            put(96, List.of(List.of(194), List.of(195))); // wooper, quagsire
            put(97, List.of(List.of(198), List.of(430))); // murkrow, honchkrow
            put(98, List.of(List.of(200), List.of(429))); // misdreavus, mismagius
            put(99, List.of(List.of(201))); // unown
            put(100, List.of(List.of(360), List.of(202))); // wynaut, wobbuffet
            put(101, List.of(List.of(203))); // girafarig
            put(102, List.of(List.of(204), List.of(205))); // pineco, forretress
            put(103, List.of(List.of(206))); // dunsparce
            put(104, List.of(List.of(207), List.of(472))); // gligar, gliscor
            put(105, List.of(List.of(209), List.of(210))); // snubbull, granbull
            put(106, List.of(List.of(211, 10234), List.of(904))); // qwilfish, qwilfish-hisui, overqwil
            put(107, List.of(List.of(213))); // shuckle
            put(108, List.of(List.of(214, 10047))); // heracross, heracross-mega
            put(109, List.of(List.of(215, 10235), List.of(461, 903))); // sneasel, sneasel-hisui, weavile (daytime and with Razor Claw) OR sneasler (nighttime with Razor Claw)
            put(110, List.of(List.of(216), List.of(217), List.of(901))); // teddiursa, ursaring, ursaluna(peat block under full moon)
            put(111, List.of(List.of(218), List.of(219))); // slugma, magcargo
            put(112, List.of(List.of(220), List.of(221), List.of(473))); // swinub, piloswine, mamoswine
            put(113, List.of(List.of(222, 10173), List.of(864))); // corsola, corsola-galar, cursola
            put(114, List.of(List.of(223), List.of(224))); // remoraid, octillery
            put(115, List.of(List.of(225))); // delibird
            put(116, List.of(List.of(458), List.of(226))); // mantyke, mantine
            put(117, List.of(List.of(227))); // skarmory
            put(118, List.of(List.of(228), List.of(229, 10048))); // houndour, houndoom, houndoom-mega
            put(119, List.of(List.of(231), List.of(232))); // phanpy, donphan
            put(120, List.of(List.of(234))); // stanler
            put(121, List.of(List.of(235))); // smeargle
            put(122, List.of(List.of(241))); // miltank
            put(123, List.of(List.of(243))); // raikou
            put(124, List.of(List.of(244))); // entei
            put(125, List.of(List.of(245))); // suicune
            put(126, List.of(List.of(246), List.of(247), List.of(248, 10049))); // larvitar, pupitar, tyranitar, tyranitar-mega
            put(127, List.of(List.of(249))); // lugia
            put(128, List.of(List.of(250))); // ho-oh
            put(129, List.of(List.of(251))); // celebi Page 26
            // /* End of Generation 2 */
            put(130, List.of(List.of(252), List.of(253), List.of(254, 10065))); // treeco, grovyle, sceptile, sceptile-mega
            put(131, List.of(List.of(255), List.of(256), List.of(257, 10050))); // torchic, combusken, blaziken, blaziken-mega
            put(132, List.of(List.of(258), List.of(259), List.of(260, 10064))); // mudkip, marshtomp, swampert, swampert-mega
            put(133, List.of(List.of(261), List.of(262))); // poochyena, mightyena
            put(134, List.of(List.of(263, 10174), List.of(264, 10175), List.of(862))); // zigzagoon, zigzagoon-galar, linoone, linoone-galar, obstagoon
            put(135, List.of(List.of(265), List.of(266, 268), List.of(267, 269))); // wurmple, silcoon, cascoon, beautifly, dustox
            put(136, List.of(List.of(270), List.of(271), List.of(272))); // lotad, lombre, ludicolo
            put(137, List.of(List.of(273), List.of(274), List.of(275))); // seedot, nuzleaf, shiftry
            put(138, List.of(List.of(276), List.of(277))); // taillow, swellow
            put(139, List.of(List.of(278), List.of(279))); // wingull, pelipper
            put(140, List.of(List.of(280), List.of(281), List.of(282, 10051, 475, 10068))); // ralts, kirlia, gardevoir(30), gardevoir-mega gallade(dawn stone & male), gallade-mega
            put(141, List.of(List.of(283), List.of(284))); // surskit, masquerain
            put(142, List.of(List.of(285), List.of(286))); // shroomish, breloom
            put(143, List.of(List.of(287), List.of(288), List.of(289))); // slakoth, vigoroth, slaking
            put(144, List.of(List.of(290), List.of(291), List.of(292))); // nincada, ninjask, shedinja
            put(145, List.of(List.of(293), List.of(294), List.of(295))); // whismur, loudred, exploud
            put(146, List.of(List.of(296), List.of(297))); // makuhita, harlyama
            put(147, List.of(List.of(299), List.of(476))); // nosepass, probopass(level up in magnetic field)
            put(148, List.of(List.of(300), List.of(301))); // skitty, delcatty
            put(149, List.of(List.of(302, 10066))); // sableye, sableye-mega
            put(150, List.of(List.of(303, 10052))); // mawile, mawile-mega
            put(151, List.of(List.of(304), List.of(305), List.of(306, 10053))); // aron, lairon, aggron, aggron-mega
            put(152, List.of(List.of(307), List.of(308, 10054))); // meditite, medicham, medicham-mega
            put(153, List.of(List.of(309), List.of(310, 10055))); // electrike, manectric, manectric-mega
            put(154, List.of(List.of(311))); // plusle
            put(155, List.of(List.of(312))); // minun
            put(156, List.of(List.of(313))); // volbeat
            put(157, List.of(List.of(314))); // illumise
            put(158, List.of(List.of(406), List.of(315), List.of(407))); // budew(high friendship), roselia, roserade(shiny stone)
            put(159, List.of(List.of(316), List.of(317))); // gulpin, swalot
            put(160, List.of(List.of(318), List.of(319, 10070))); // carvanha, sharpedo, sharpedo-mega
            put(161, List.of(List.of(320), List.of(321))); // wailmer, wailord
            put(162, List.of(List.of(322), List.of(323, 10087))); //numel, camerupt, camerupt-mega
            put(163, List.of(List.of(324))); // torkoal
            put(164, List.of(List.of(325), List.of(326))); // spoink, grumpig
            put(165, List.of(List.of(327))); // spinda
            put(166, List.of(List.of(328), List.of(329), List.of(330))); // trapinch, vibrava, flygon
            put(167, List.of(List.of(331), List.of(332))); // cacnea, cacturne
            put(168, List.of(List.of(333), List.of(334, 10067))); // swablu, altaria, altaria-mega
            put(169, List.of(List.of(335))); // zangoose
            put(170, List.of(List.of(336))); // seviper
            put(171, List.of(List.of(337))); // lunatone
            put(172, List.of(List.of(338))); // solrock
            put(173, List.of(List.of(339), List.of(340))); // barboach, whiscash
            put(174, List.of(List.of(341), List.of(342))); // corphish, crawdaunt
            put(175, List.of(List.of(343), List.of(344))); // baltoy, claydol
            put(176, List.of(List.of(345), List.of(346))); // lileep, cradily
            put(177, List.of(List.of(347), List.of(348))); // anorith, armaldo
            put(178, List.of(List.of(349), List.of(350))); // feebas, milotic
            put(179, List.of(List.of(351, 10013, 10014, 10015))); // castform OTHER IMAGES with ID: 10013-10015
            put(180, List.of(List.of(352))); // kecleon
            put(181, List.of(List.of(353), List.of(354, 10056))); // shuppet, banette, banette-mega
            put(182, List.of(List.of(355), List.of(356), List.of(477))); // duskull, dusclops, dusknoir(trade with reaper cloth)
            put(183, List.of(List.of(357))); // tropius
            put(184, List.of(List.of(433), List.of(358))); // chingling, chimecho
            put(185, List.of(List.of(359, 10057))); // absol, absol-mega
            put(186, List.of(List.of(361), List.of(362, 10074, 478))); // snorunt, glalie(level up), glalie-mega, froslass(dawn stone & female)
            put(187, List.of(List.of(363), List.of(364), List.of(365))); // spheal, sealeo, walrein
            put(188, List.of(List.of(366), List.of(367), List.of(368))); // clamperl, huntail, gorebyss
            put(189, List.of(List.of(369))); // relicanth
            put(190, List.of(List.of(370))); // luvdisc
            put(191, List.of(List.of(371), List.of(372), List.of(373, 10089))); // bagon, shelgon, salamence, salamence-mega
            put(192, List.of(List.of(374), List.of(375), List.of(376, 10076))); // beldum, metang, metagross, metagross-mega
            put(193, List.of(List.of(377))); // regirock
            put(194, List.of(List.of(378))); // regice
            put(195, List.of(List.of(379))); // registeel
            put(196, List.of(List.of(380, 10062))); // latias, latias-mega
            put(197, List.of(List.of(381, 10063))); // latios, latios-mega
            put(198, List.of(List.of(382, 10077))); // kyogre, kyogre-primal
            put(199, List.of(List.of(383, 10078))); // groudon, groudon-primal
            put(200, List.of(List.of(384, 10079))); // rayquaza, rayquaza-primal
            put(201, List.of(List.of(385))); // jirachi
            put(202, List.of(List.of(386, 10001, 10002, 10003))); // deoxys(normal) OTHER IMAGES with ID: 10001, 10002, 10003
            /* End of Generation 3 */
            put(203, List.of(List.of(387), List.of(388), List.of(389))); // turtwig, grotle, torterra
            put(204, List.of(List.of(390), List.of(391), List.of(392))); // chimchar, monferno, infernape
            put(205, List.of(List.of(393), List.of(394), List.of(395))); // piplup, prinplup, empoleon
            put(206, List.of(List.of(396), List.of(397), List.of(398))); // starly, staravia, staraptor
            put(207, List.of(List.of(399), List.of(400))); // bidoof, bibarel
            put(208, List.of(List.of(401), List.of(402))); // kricketot, kricketune
            put(209, List.of(List.of(403), List.of(404), List.of(405))); // shinx, luxio, luxray
                /* 210 does not exist in evolution-chains call */
            put(211, List.of(List.of(408), List.of(409))); // cranidos, rampardos
            put(212, List.of(List.of(410), List.of(411))); // shieldon, bastiodon
            put(213, List.of(List.of(412, 10004, 10005), List.of(413), List.of(414))); // burmy, wormadam-plant, mothim OTHER IMAGES for 412 with ID: 10004, 10005
            put(214, List.of(List.of(415), List.of(416))); // combee, vespiquen
            put(215, List.of(List.of(417))); // pachirisu
            put(216, List.of(List.of(418), List.of(419))); // buizel, floatzel
            put(217, List.of(List.of(420), List.of(421))); // cherubi, cherrim
            put(218, List.of(List.of(422), List.of(423))); // shellos, gastrodon
            put(219, List.of(List.of(425), List.of(426))); // drifloon, drifblim
            put(220, List.of(List.of(427), List.of(428, 10088))); // buneary, lopunny, lopunny-mega
            put(221, List.of(List.of(431), List.of(432))); // glameow, purugly
            put(223, List.of(List.of(434), List.of(435))); // stunky, skuntank
                /* 222 does not exist in evolution-chains call */
            put(224, List.of(List.of(436), List.of(437))); // bronzor, bronzong
            put(228, List.of(List.of(441))); // chatot
            /* 225, 226, 227 does not exist in evolution-chains call */
            put(229, List.of(List.of(442))); // spiritomb
            put(230, List.of(List.of(443), List.of(444), List.of(445, 10058))); // gible, gabite, garchomp, garchomp-mega
            /* 231 does not exist in evolution-chains call */
            put(232, List.of(List.of(447), List.of(448, 10059))); // riolu, lucario, lucario-mega
            put(233, List.of(List.of(449), List.of(450))); // hippopotas, hippowdon
            put(234, List.of(List.of(451), List.of(452))); // skorupi, drapion
            put(235, List.of(List.of(453), List.of(454))); // croagunk, toxicroak
            put(236, List.of(List.of(455))); // carnivine
            put(237, List.of(List.of(456), List.of(457))); // finneon, lumineon
            /* 238 does not exist in the evolution-chains call */
            put(239, List.of(List.of(459), List.of(460, 10060))); // snover, abomasnow, abomasnow-mega
            put(240, List.of(List.of(479, 10008, 10009, 10010, 10011, 10012))); // rotom, OTHER IMAGES with ID: 10008-10012
            put(241, List.of(List.of(480))); // uxie
            put(242, List.of(List.of(481))); // mesprit
            put(243, List.of(List.of(482))); // azelf
            put(244, List.of(List.of(483, 10245))); // dialga, dialga-origin
            put(245, List.of(List.of(484, 10246))); // palkia, palkia-origin
            put(246, List.of(List.of(485))); // heatran
            put(247, List.of(List.of(486))); // regigigas
            put(248, List.of(List.of(487, 10007))); // giratina,-altered OTHER IMAGES with ID: 10007
            put(249, List.of(List.of(488))); // cresselia
            put(250, List.of(List.of(489), List.of(490))); // phione, manaphy
            /* 251 does not exist in the evolution-chains call */
            put(252, List.of(List.of(491))); // darkrai
            put(253, List.of(List.of(492, 10006))); // shaymin-lang, shaymin-sky
            put(254, List.of(List.of(493))); // arceus
            put(255, List.of(List.of(494))); // victini
            /* End of Generation 4 */
            put(256, List.of(List.of(495), List.of(496), List.of(497))); // snivy, servine, serperior
            put(257, List.of(List.of(498), List.of(499), List.of(500))); // tepig, pignite, emboar
            put(258, List.of(List.of(501), List.of(502), List.of(503, 10236))); // oshawott, dewott, samurott, samurott-hisui
            put(259, List.of(List.of(504), List.of(505))); // patrat, watchog
            put(260, List.of(List.of(506), List.of(507), List.of(508))); // lillipup, herdier, stoutland
            put(261, List.of(List.of(509), List.of(510))); // purrloin, liepard
            put(262, List.of(List.of(511), List.of(512))); // pansage, simisage
            put(263, List.of(List.of(513), List.of(514))); // pansear, simisear
            put(264, List.of(List.of(515), List.of(516))); // panpour, simipour
            put(265, List.of(List.of(517), List.of(518))); // munna, musharna
            put(266, List.of(List.of(519), List.of(520), List.of(521))); // pidove, tranquill, unfezant
            put(267, List.of(List.of(522), List.of(523))); // blitzle, zebstrika
            put(268, List.of(List.of(524), List.of(525), List.of(526))); // roggenrola, boldore, gigalith
            put(269, List.of(List.of(527), List.of(528))); // woobat, swoobat
            put(270, List.of(List.of(529), List.of(530))); // drilbur, excadrill
            put(271, List.of(List.of(531, 10069))); // audino, audino-mega
            put(272, List.of(List.of(532), List.of(533), List.of(534))); // timburr, gurdurr, conkeldurr
            put(273, List.of(List.of(535), List.of(536), List.of(537))); // tympole, palpitoad, seismitoad
            put(274, List.of(List.of(538))); // throh
            put(275, List.of(List.of(539))); // sawk
            put(276, List.of(List.of(540), List.of(541), List.of(542))); // sewaddle, swadloon, leavanny
            put(277, List.of(List.of(543), List.of(544), List.of(545))); // venipede, whirlipede, scolipide
            put(278, List.of(List.of(546), List.of(547))); // cottonee, whimsicott
            put(279, List.of(List.of(548), List.of(549, 10237))); // petilil, lilligant, lilligant
            put(280, List.of(List.of(550, 100016, 10247), List.of(902, 10248))); // basculin-red-striped, basculin-blue-striped, basculin-white-striped, basculegion-male, basculegion-female
            put(281, List.of(List.of(551), List.of(552), List.of(553))); // sandile, krokorok, krookodile
            put(282, List.of(List.of(554, 10017, 10176, 10178), List.of(555, 10177))); // darumaka, darumaka-galar, darmanitan-zen, darmanitan-zen-galar, darmanitan-standard, darmanitan-galar-standard
            put(283, List.of(List.of(556))); // maractus
            put(284, List.of(List.of(557), List.of(558))); // dwebble, crustle
            put(285, List.of(List.of(559), List.of(560))); // scraggy, scrafty
            put(286, List.of(List.of(561))); // sigilyph
            put(287, List.of(List.of(562, 10179), List.of(563, 867))); // yamask(type:ghost) => cofagrigus(levelup:34), OR  yamask => runerigus(near dusty bowl), yamask-galar
            put(288, List.of(List.of(564), List.of(565))); // tirtouga, carracosta
            put(289, List.of(List.of(566), List.of(567))); // archen, archeops
            put(290, List.of(List.of(568), List.of(569, 10207))); // trubbish, garbodor, garbodor-gmax
            put(291, List.of(List.of(570, 10238), List.of(571, 10239))); // zorua,zorua-hisui, zoroark, zoroark-hisui
            put(292, List.of(List.of(572), List.of(573))); // minccino, cinccino
            put(293, List.of(List.of(574), List.of(575), List.of(576))); // gothita, gothorita, gothitelle
            put(294, List.of(List.of(577), List.of(578), List.of(579))); // solosis, duosion, reuniclus
            put(295, List.of(List.of(580), List.of(581))); // ducklett, swanna
            put(296, List.of(List.of(582), List.of(583), List.of(584))); // vanillite, vanillish, vanilluxe
            put(297, List.of(List.of(585), List.of(586))); // deerling, sawsbuck
            put(298, List.of(List.of(587))); // emolga
            put(299, List.of(List.of(588), List.of(589))); // karrablast, escavalier
            put(300, List.of(List.of(590), List.of(591))); // foongus, amoonguss
            put(301, List.of(List.of(592), List.of(593))); // frillish, jellicent
            put(302, List.of(List.of(594))); // alomomola
            put(303, List.of(List.of(595), List.of(596))); // joltik, galvantula
            put(304, List.of(List.of(597), List.of(598))); // ferroseed, ferrothorn
            put(305, List.of(List.of(599), List.of(600), List.of(601))); // klink, klang, klinklang
            put(306, List.of(List.of(602), List.of(603), List.of(604))); // tynamo, eelektrik, eelektross
            put(307, List.of(List.of(605), List.of(606))); // elgyem, beheeyem
            put(308, List.of(List.of(607), List.of(608), List.of(609))); // litwick, lampent, chandelure
            put(309, List.of(List.of(610), List.of(611), List.of(612))); // axew, fraxure, haxorus
            put(310, List.of(List.of(613), List.of(614))); // cubchoo, beatric
            put(311, List.of(List.of(615))); // cryogonal
            put(312, List.of(List.of(616), List.of(617))); // shelmet, accelgor
            put(313, List.of(List.of(618, 10180))); // stunfisk, stunfisk-galar
            put(314, List.of(List.of(619), List.of(620))); // mienfoo, mienshao
            put(315, List.of(List.of(621))); // druddigon
            put(316, List.of(List.of(622), List.of(623))); // golett, golurk
            put(317, List.of(List.of(624), List.of(625))); // pawniard, bisharp
            put(318, List.of(List.of(626))); // bouffalant
            put(319, List.of(List.of(627), List.of(628, 10240))); // rufflet, braviary, braviary-hisui
            put(320, List.of(List.of(629), List.of(630))); // vullaby, mandibuzz
            put(321, List.of(List.of(631))); // heatmor
            put(322, List.of(List.of(632))); // durant
            put(323, List.of(List.of(633), List.of(634), List.of(635))); // deino, zweilous, hydreigon
            put(324, List.of(List.of(636), List.of(637))); // larvesta, volcarona
            put(325, List.of(List.of(638))); // cobalion
            put(326, List.of(List.of(639))); // terrakion
            put(327, List.of(List.of(640))); // virizion
            put(328, List.of(List.of(641, 10019))); // tornadus-incarnate, tornadus-therian
            put(329, List.of(List.of(642, 10020))); // thundurus-incarnate, thundurus-therian
            put(330, List.of(List.of(643, 10021))); // reshiram, thundurus-therian
            put(331, List.of(List.of(644))); // zekrom
            put(332, List.of(List.of(645))); // landorus-incarnate
            put(333, List.of(List.of(646, 10022, 10023))); // kyurem, kyurem-black, kyurem-white
            put(334, List.of(List.of(647, 10024))); // keldeo-ordinary, keldeo-resolute
            put(335, List.of(List.of(648, 10018))); // meloetta-aria, meloetta-pirouette
            put(336, List.of(List.of(649))); // genesect
            // /* End of Generation 5 */
            put(337, List.of(List.of(650), List.of(651), List.of(652))); // chespin, quilladin, chesnaught
            put(338, List.of(List.of(653), List.of(654), List.of(655))); // fennekin, braixen, delphox
            put(339, List.of(List.of(656), List.of(657), List.of(658, 10116, 10117))); // froakie, frogadier, greninja, greninja-battle-bond, greninja-ash
            put(340, List.of(List.of(659), List.of(660))); // bunnelby, diggersby
            put(341, List.of(List.of(661), List.of(662), List.of(663))); // fletchling, fletchinder, talonflame
            put(342, List.of(List.of(664), List.of(665), List.of(666))); // scatterbug, spewpa, vivillon
            put(343, List.of(List.of(667), List.of(668))); // litleo, pyroar
            put(344, List.of(List.of(669), List.of(670, 10061), List.of(671))); // flabebe, floette, floette-eternal, florges
            put(345, List.of(List.of(672), List.of(673))); // skiddo, gogoat
            put(346, List.of(List.of(674), List.of(675))); // pancham, pangoro
            put(347, List.of(List.of(676))); // furfrou,
            put(348, List.of(List.of(677), List.of(678, 10025))); // espurr, meowstic-male, meowstic-female
            put(349, List.of(List.of(679), List.of(680), List.of(681, 10026))); // honedge, doublade, aegislash-shield, aegislash-blade
            put(350, List.of(List.of(682), List.of(683))); // spritizee, aromatisse
            put(351, List.of(List.of(684), List.of(685))); // swirlix, slurpuff
            put(352, List.of(List.of(686), List.of(687))); // inkay, malamar
            put(353, List.of(List.of(688), List.of(689))); // binacle, barbaracle
            put(354, List.of(List.of(690), List.of(691))); // skrelp, dragalge
            put(355, List.of(List.of(692), List.of(693))); // clauncher, clawitzer
            put(356, List.of(List.of(694), List.of(695))); // helioptile, heliolisk
            put(357, List.of(List.of(696), List.of(697))); // tyrunt, tyrantrum
            put(358, List.of(List.of(698), List.of(699))); // amaura, aurorus
            put(359, List.of(List.of(701))); // hawlucha
            put(360, List.of(List.of(702))); // dedenne
            put(361, List.of(List.of(703))); // carbink
            put(362, List.of(List.of(704), List.of(705, 10241), List.of(706, 10242))); // goomy, sliggoo, sloggoo-hisui, goodra, goodra-hisui
            put(363, List.of(List.of(707))); // klefki
            put(364, List.of(List.of(708), List.of(709))); // phantump, trevenant
            put(365, List.of(List.of(710), List.of(711))); // pumpkaboo-average, gourgeist-average
            put(366, List.of(List.of(712), List.of(713, 10243))); // bergmite, avalugg, avalugg-hisui
            put(367, List.of(List.of(714), List.of(715))); // noibat, noivern
            put(368, List.of(List.of(716))); // xerneas
            put(369, List.of(List.of(717))); // yveltal
            put(370, List.of(List.of(718, 10118, 10119, 10181, 10120))); // zygarde-50, zygarde-10-power-construct, zygarde-50-power-construct, zygarde-10, zygarde-complete
            put(371, List.of(List.of(719, 10075))); // diancie, diancie-mega
            put(372, List.of(List.of(720, 10086))); // hoopa, hoopa-unbound
            put(373, List.of(List.of(721))); // volcanion
            // /* End of Generation 6 */
            put(374, List.of(List.of(722), List.of(723), List.of(724, 10244))); // rowlet, dartrix, decidueye, decidueye-hisui
            put(375, List.of(List.of(725), List.of(726), List.of(727))); // litten, torracat, incineroar
            put(376, List.of(List.of(728), List.of(729), List.of(730))); // popplio, brionne, primarina
            put(377, List.of(List.of(731), List.of(732), List.of(733))); // pikipek, trumbeak, toucannon
            put(378, List.of(List.of(734), List.of(735, 10121))); // yungoos, gumshoos, gumshoos-totem
            put(379, List.of(List.of(736), List.of(737), List.of(738, 10122))); // grubbin, charjabug, vikavolt, vikavolt-totem
            put(380, List.of(List.of(739), List.of(740))); // crabrawler, crabominable
            put(381, List.of(List.of(741, 10123, 10124, 10125))); // oricorio-baile, oricorio-pom-pom, oricorio-pau, oricorio-sensu
            put(382, List.of(List.of(742), List.of(743, 10150))); // cutiefly, ribombee, ribombee-totem
            put(383, List.of(List.of(744, 10151), List.of(745, 10126, 10152))); // rockruff, rockruff-own-tempo lycanroc-midday, lycanroc-midnight, lycanroc-dusk
            put(384, List.of(List.of(746, 10127))); // wishiwashi-solo, wishiwashi-school
            put(385, List.of(List.of(747), List.of(748))); // mareanie, toxapex
            put(386, List.of(List.of(749), List.of(750))); // mudbray, mudsdale
            put(387, List.of(List.of(751), List.of(752, 10153))); // dewpider, araquanid, araquanid-totem
            put(388, List.of(List.of(753), List.of(754, 10128))); // fomantis, lurantis, lurantis-totem
            put(389, List.of(List.of(755), List.of(756))); // morelull, shiinotic
            put(390, List.of(List.of(757), List.of(758, 10129))); // salandit, salazzle, salazzle-totem
            put(391, List.of(List.of(759), List.of(760))); // stufful, bewear
            put(392, List.of(List.of(761), List.of(762), List.of(763))); // bounsweet, steenee, tsareena
            put(393, List.of(List.of(764))); // comfey
            put(394, List.of(List.of(765))); // oranguru
            put(395, List.of(List.of(766))); // passimian
            put(396, List.of(List.of(767), List.of(768))); // wimpod, golisopod
            put(397, List.of(List.of(769), List.of(770))); // sandygast, palossand
            put(398, List.of(List.of(771))); // pyukumuku
            put(399, List.of(List.of(772), List.of(773))); // type-null, silvally
            put(400, List.of(List.of(774, 10130, 10131, 10132, 10133, 10134, 10135, 10136, 10137, 10138, 10139, 10140, 10141, 10142)));
            // minior-red-meteor, orange, yellow, green, blue, indio, violet; minior-red, orange, yellow, green, blue, indigo, violet
            put(401, List.of(List.of(775))); // komala
            put(402, List.of(List.of(776))); // turtonator
            put(403, List.of(List.of(777, 10154))); // togedemaru, togedemaru-totem
            put(404, List.of(List.of(778, 10143, 10144, 10145))); // mimikyu-disguised, mimikyu-busted, mimikyu-totem-disguised, mimikyu-totem-busted
            put(405, List.of(List.of(779))); // bruxish
            put(406, List.of(List.of(780))); // drampa
            put(407, List.of(List.of(781))); // dhelmise
            put(408, List.of(List.of(782), List.of(783), List.of(784, 10146))); // jangmo-o, hakamo-o, kommo-o, kommo-o-totem
            put(409, List.of(List.of(785))); // tapu-koko
            put(410, List.of(List.of(786))); // tapu-lele
            put(411, List.of(List.of(787))); // tapu-bulu
            put(412, List.of(List.of(788))); // tapu-fini
            put(413, List.of(List.of(789), List.of(790), List.of(791), List.of(792))); // cosmog, cosmoem, solgaleo, lunala
            put(414, List.of(List.of(793))); // nihilego
            put(415, List.of(List.of(794))); // buzzwole
            put(416, List.of(List.of(795))); // pheromosa
            put(417, List.of(List.of(796))); // xurkitree
            put(418, List.of(List.of(797))); // celesteela
            put(419, List.of(List.of(798))); // kartana
            put(420, List.of(List.of(799))); // guzzlord
            put(421, List.of(List.of(800, 10155, 10156, 10157))); // necrozma, necrozma-dusk, necrozma-dawn, necrozma-ultra
            put(422, List.of(List.of(801, 10147))); // magearna, magearna-original
            put(423, List.of(List.of(802))); // marshadow
            put(424, List.of(List.of(803), List.of(804))); // poipole, naganadel
            put(425, List.of(List.of(805))); // stakataka
            put(426, List.of(List.of(806))); // blacephalon
            put(427, List.of(List.of(807))); // zeraora
            put(428, List.of(List.of(808), List.of(809, 10208))); // meltan, melmetal, melmetal-gmax
            //[429, [[809] ]], // melmetal
            // /* End of Generation 7 */
            put(430, List.of(List.of(810), List.of(811), List.of(812, 10209))); // grookey, thwackey, rillaboom, rillaboom-gmax
            put(431, List.of(List.of(813), List.of(814), List.of(815, 10210))); // scorbunny, raboot, cinderace, cinderace-gmax
            put(432, List.of(List.of(816), List.of(817), List.of(818, 10211))); // sobble, drizzile, inteleon, inteleon-gmax
            put(433, List.of(List.of(819), List.of(820))); // skwovet, greedent
            put(434, List.of(List.of(821), List.of(822), List.of(823, 10212))); // rookidee, corvisquire, corviknight, corviknight-gmax
            put(435, List.of(List.of(824), List.of(825), List.of(826, 10213))); // blipbug, dottler, orbeetle, orbeetle-gmax
            put(436, List.of(List.of(827), List.of(828))); // nickit, thievul
            put(437, List.of(List.of(829), List.of(830))); // gossifleur, eldegoss
            put(438, List.of(List.of(831), List.of(832))); // wooloo, dubwool
            put(439, List.of(List.of(833), List.of(834, 10214))); // chewtle, drednaw, drednaw-gmax
            put(440, List.of(List.of(835), List.of(836))); // yamper, boltund
            put(441, List.of(List.of(837), List.of(838), List.of(839, 10215))); // rolycoly, carkol, coalossal, coalossal-gmax
            put(442, List.of(List.of(840), List.of(841, 10216), List.of(842, 10217))); // applin, flapple, flapple-gmax, appletun, appletun-gmax
            put(443, List.of(List.of(843), List.of(844, 10218))); // silicobra, sandaconda, sandaconda-gmax
            put(444, List.of(List.of(845, 10182, 10183))); // cramorant, cramorant-gulping, gramorant-gorging
            put(445, List.of(List.of(846), List.of(847))); // arrokuda, barraskewda
            put(446, List.of(List.of(848), List.of(849, 10219, 10184, 10228))); // toxel, toxtricity-amped, toxtricity-amped-gmax, toxtricity-low-key, toxtricity-low-key-gmax
            put(447, List.of(List.of(850), List.of(851, 10220))); // sizzlipede, centiskorch, centiskorch-gmax
            put(448, List.of(List.of(852), List.of(853))); // clobbopus, grapploct
            put(449, List.of(List.of(854), List.of(855))); // sinistea, polteageist
            put(450, List.of(List.of(856), List.of(857), List.of(858, 10221))); // hatenna, hattrem, hatterene, hatterene-gmax
            put(451, List.of(List.of(859), List.of(860), List.of(861, 10222))); // impidimp, morgrem, grimmsnarl, grimmsnarl-gmax
            put(452, List.of(List.of(868), List.of(869, 10223))); // milcery, alcremie, alcremie-gmax
            put(453, List.of(List.of(870))); // falinks
            put(454, List.of(List.of(871))); // pincurchin
            put(455, List.of(List.of(872), List.of(873))); //snom, frosmoth
            put(456, List.of(List.of(874))); // stonjourner
            put(457, List.of(List.of(875, 10185))); // eiscue-ice, eiscue-noice
            put(458, List.of(List.of(876, 10186))); // indeedee-male, indeedee-female
            put(459, List.of(List.of(877, 10187))); // morpeko-full-belly, morpeko-hangry
            put(460, List.of(List.of(878), List.of(879, 10224))); // cufant, copperajah, copperajah-gmax
            put(461, List.of(List.of(880))); // dracozolt
            put(462, List.of(List.of(881))); // arctozolt
            put(463, List.of(List.of(882))); // dracovish
            put(464, List.of(List.of(883))); // arctovish
            put(465, List.of(List.of(884, 10225))); // duraludon, duraludon-gmax
            put(466, List.of(List.of(885), List.of(886), List.of(887))); // dreepy, drakloak, dragapult
            put(467, List.of(List.of(888, 10188))); // zacian, zacian-crowned
            put(468, List.of(List.of(889, 10189))); // zamazenta, zamazenta-crowned
            put(469, List.of(List.of(890, 10190))); // eternatus, eternatus-eternamax
            put(470, List.of(List.of(891), List.of(892, 10191, 10226, 10227))); // kubfu, urshifu-single-strike, urshifu-rapid-strike, single-strike-gmax, rapid-strike-gmax
            put(471, List.of(List.of(893, 10192))); // zarude, zarude-dada
            put(472, List.of(List.of(894))); // regieleki
            put(473, List.of(List.of(895))); // regidrago
            put(474, List.of(List.of(896))); // glastrier
            put(475, List.of(List.of(897))); // spectrier
            put(476, List.of(List.of(898, 10193, 10194))); // calyrex, calyrex-ice, calyrex-shadow
            put(477, List.of(List.of(899))); // wyrdeer
            put(478, List.of(List.of(905, 10249))); // enamorus, enamorus-therian
            put(479, List.of(List.of(10027, 10028, 10029), List.of(10030, 10031, 10032))); // pumpkaboo-small, large, super, gorgeist-small, large, super
            /* End of generation 8 */
            put(480, List.of(List.of(906), List.of(907), List.of(908)));
        }};
    }

    public HttpResponse<String> callUrl(String url) {
        HttpResponse<String> response = null;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();
            response = HttpClient.newBuilder()
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString());
            logger.info("response: {}", response.body());
        } catch (Exception e) {
            logger.error("Failed to call endpoint: {}", url);
        }
        return response;
    }

    public PokemonSpecies setFromResults(Map<String,Object> results) {
        PokemonSpecies speciesData = new PokemonSpecies();
        speciesData.setId(((BigInteger) results.get("id")).intValue());
        speciesData.setName((String) results.get("name"));
        speciesData.setOrder(((BigInteger) results.get("order")).intValue());
        speciesData.setGenderRate(((BigInteger) results.get("gender_rate")).intValue());
        speciesData.setCaptureRate(((BigInteger) results.get("capture_rate")).intValue());
        speciesData.setBaseHappiness(((BigInteger) results.get("base_happiness")).intValue());
        speciesData.setIsBaby((Boolean) results.get("is_baby"));
        speciesData.setIsLegendary((Boolean) results.get("is_legendary"));
        speciesData.setIsMythical((Boolean) results.get("is_mythical"));
        speciesData.setHatchCounter(((BigInteger) results.get("hatch_counter")).intValue());
        speciesData.setHasGenderDifferences((Boolean) results.get("has_gender_differences"));
        speciesData.setFormsSwitchable((Boolean) results.get("forms_switchable"));
        speciesData.setGrowthRate((NamedApiResource<GrowthRate>) results.get("growth_rate"));
        speciesData.setPokedexNumbers((List<PokemonSpeciesDexEntry>) results.get("pokedex_numbers"));
        speciesData.setEggGroups((List<NamedApiResource<EggGroup>>) results.get("egg_groups"));
        speciesData.setColor((NamedApiResource<PokemonColor>) results.get("color"));

//        private NamedApiResource<PokemonShape> shape;
//        private NamedApiResource<PokemonSpecies> evolvesFromSpecies;
//        private ApiResource<EvolutionChain> evolutionChain;
//        private NamedApiResource<PokemonHabitat> habitat;
//        private NamedApiResource<Generation> generation;
//        private List<Name> names;
//        private List<FlavorText> flavorTextEntries;
//        private List<Description> formDescriptions;
//        private List<Genus> genera;
//        private List<PokemonSpeciesVariety> varieties;

        return speciesData;
    }
}
