<!DOCTYPE html>
<html lang="en">
<head>
    <title>Search</title>
    <jsp:include page="headCommon.jsp"/>
    <style>
    </style>
</head>
<body style="justify-content:space-evenly;text-align:center;"
      class="${isDarkMode?'darkmode':'lightmode'}">
    <h1 id="searchImgReloadPage" style="vertical-align:middle;">
        <a href="${pageContext.request.contextPath}/search?darkmode=${isDarkMode}" style="cursor:zoom-in;" title="Search">
            <span class="center">
            <img alt="pokedex" src="${pageContext.request.contextPath}/images/pokedex.jpg" style="width:100%;"></span>
        </a>
    </h1>

    <h4 style="vertical-align: middle;">
        <a href="${pageContext.request.contextPath}/?darkmode=${isDarkMode}" title="Go back to list"><i class="fas fa-arrow-left" style="${isDarkMode?'color:white':'color:#000000'}"></i></a>
        <input id="search" name="nameOrId" placeholder="Pokemon Name/ID" type="text" style="color:${isDarkMode?'white':'black'} !important;" class="${isDarkMode?'darkmode':'lightmode'}"/>
        <button type="submit" style="background:none;border:none;padding:0;cursor:zoom-in;" title="Search">
            <img alt="pokéball" src="${pageContext.request.contextPath}/images/pokeball1.jpg"
                 class="button cursor" title="Find Pokemon" style="height:30px;width:30px;"
                 onclick="searchForPkmn(${isDarkMode})">
        </button>
    </h4>

    <div id="pokedex" class="${isDarkMode?'darkmode':'lightmode'}"></div>

</body>

<script src="https://cdn.jsdelivr.net/npm/popper.js@1.12.9/dist/umd/popper.min.js" integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/js/bootstrap.min.js" integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl" crossorigin="anonymous"></script>
<script type="text/javascript">
    $(function(){
        $('#pokedex').hide();
        //checks whether the pressed key is "Enter"
        $('#nameOrId').on('keypress', function(e) {
            if ($('#nameOrId').val() === '') return;
            if (e.code === 'Enter' || e.code === 'Return') {
                //getPokemonInfo();
                //getPokemonFromSearch($('#pokemonName').val());
            }
        });
    });

    function searchForPkmn(isDarkMode) {
        let nameOrId = $("#search").val().trim();
        if (nameOrId === '') {
            nameOrId = $("#searchMobile").val().trim();
        }
        console.log('nameOrId: ' + nameOrId);

        $.ajax({
            type: "GET",
            url: "/springboot/pokemon/" + nameOrId + "/validateNameOrId",
            async: false,
            dataType: "application/json",
            crossDomain: true,
            statusCode: {
                200: function(data) {
                    console.log('200 validatePkmn');
                    console.log(JSON.parse(JSON.stringify(data.responseText)));
                    let url = '';
                    if (nameOrId) {
                        url = 'pokedex/' + nameOrId + '?darkmode=' + isDarkMode;
                    }
                    console.log('Navigating to: ' + url);
                    window.location.href = url; // navigates like a normal link
                },
                404: function(data) {
                    console.log(JSON.parse(JSON.stringify(data.responseText)));
                    alert("Pok\u00e9mon not found. Please check the Name or ID and try again.");
                },
                500: function() {
                    alert('Pok\u00e9mon search failed. Please try again later.');
                }
            }
        });
    }

    function loadPokedexPageAjax(url) {
        $.ajax({
            type: "GET",
            url: url,
            async: false,
            dataType: "application/json",
            statusCode: {
                200: function(data) {
                    $("#pokedex").html(data.responseText);
                    $("#pokedex").show();
                    $("#pokedexSearchImgSearchLink").hide();
                },
                404: function() {
                    console.log('Failed');
                    $("#evolutionsDiv").html('Request failed');
                },
                500: function() {
                    console.log('Server Error');
                }
            }
        });
    }

    let pokemon = {}, sprites = {};
    let locations = [], moves = [];
    let desc = "", color = "";
    // Actions performed when you click "Get Info"
    function getPokemonInfo() {
        let name = $('#nameOrId').val();
        name = name.toLowerCase().trim();
        if (!isNameValid(name)) { alert("Not a valid name or ID"); }
        else {
            pokemon = getPokemon(name);
            //console.log("pokemon set: " + JSON.stringify(pokemon));
            sprites = JSON.parse(pokemon)["sprites"];
            //console.log("sprites set: " + JSON.stringify(sprites));
            // // desc = getPokemonDescriptionText(name);
            // // locations = getLocations(name);
            // // moves = getMoves(name);
            // // color = setBackgroundAndGetColor(name);
            // // setupPokedex();
            // // showImage('default');
            // // $('#pokedex').show();
            // // setButtonWeightDefaults();
            // // setTabDivDefaults();
        }
    }
    // Ajax to get Pokemon
    function getPokemon(name) {
        pokemon = $.ajax({
            type: "GET",
            url: "/pokemon/"+name,
            async: false,
            dataType: "application/json",
            success: function(data) {
                pokemon = JSON.parse(data.responseText);
            }
        });
        return pokemon.responseText;
    }
    // Ajax to get Moves
    function getMoves() {
        moves = JSON.parse(pokemon)["moves"];
        //console.log(moves.length + " before moves text adding")
        let moveNames = [];
        moves.forEach(m => {
            let move = m['move'].name;
            //console.log(move);
            move = move[0].toUpperCase() + move.substring(1);
            moveNames.push(move);
        });
        moves = moveNames.sort();
        return moves;
    }
    // Ajax to get Locations
    function getLocations(name) {
        $.ajax({
            type: "GET",
            url: "/pokemon/locations/"+name,
            async: false,
            crossDomain: false,
            success: function(data) {
                locations = JSON.parse(data);
                //console.log("length: " + locations.length + " locations: " + locations);
                let locationText = [];
                for(let i=0; i<locations.length; i++) {
                    let theName = locations[i];
                    let newName = "";
                    let names = theName.split("-");
                    names.forEach(name => {
                        name = name[0].toUpperCase() + name.substring(1);
                        newName += name + " ";
                    });
                    locationText.push(newName.toString());
                }
                locations = locationText;
                //console.log("after: " + locations);
            }
        });
        return locations.sort();
    }
    // Ajax to get Description text
    function getPokemonDescriptionText(name) {
        desc = $.ajax({
            headers: {
                accept: "application/json",
                contentType: "application/json"
            },
            type: "GET",
            url: "pokemon/"+name+"/description",
            async: false,
            dataType: "application/json",
            crossDomain: false,
            'success': function(data) {
                desc = JSON.parse(data.responseText);
            }
        });
        return desc.responseText;
    }
    // Flips between regular and shiny images
    function showImage(type) {
        let photoEle = $('#imageOfPokemon');
        let image;
        switch (type) {
            case 'default' : {
                image = sprites["frontDefault"];
                if (image === null) {
                    image = "/images/pokeball1.jpg";
                }
                $('#pokemonDefaultImgBtn').css('font-weight', 'bold');
                $('#pokemonShinyImgBtn').css('font-weight', 'normal');
                break;
            }
            case 'shiny' : {
                image = sprites["frontShiny"];
                if (image === null) {
                    image = "/images/pokeball1.jpg";
                }
                $('#pokemonDefaultImgBtn').css('font-weight', 'normal');
                $('#pokemonShinyImgBtn').css('font-weight', 'bold');
                break;
            }
            default : {
                image = "/images/pokeball1.jpg";
                break;
            }
        }
        photoEle.prop("src", image);
        photoEle.show();
    }
    // Ajax to get the color of the Pokemon
    function setBackgroundAndGetColor(name) {
        $.ajax({
            type: "GET",
            url: "/pokemon/"+name+"/color",
            async: false,
            crossDomain: false,
            'success': function(data) {
                let colorRes = JSON.stringify(data).replaceAll("\"", "");
                //console.log("color before: " + colorRes);
                if (colorRes === "red") { document.body.style.backgroundColor = "#FA8072"; }
                else if (colorRes === "yellow") { document.body.style.backgroundColor  = "#FFC300"; }
                else if (colorRes === "green") { document.body.style.backgroundColor  = "#AFE1AF"; }
                else if (colorRes === "blue") { document.body.style.backgroundColor  = "#ADD8E6"; }
                else if (colorRes === "purple") { document.body.style.backgroundColor  = "#CBC3E3"; }
                else if (colorRes === "brown") { document.body.style.backgroundColor  = "#D27D2D"; }
                else if (colorRes === "white") { document.body.style.backgroundColor  = "#d3cbcb"; }
                else if (colorRes === "pink") { document.body.style.backgroundColor = colorRes; }
                else if (colorRes === "black") { document.body.style.backgroundColor = "#8f8b8b"}
                else if (colorRes === "gray" || colorRes === "grey") { document.body.style.backgroundColor = "#8f8b8b"}
                //console.log("colorRes: " + colorRes);
                color = colorRes;
            },
            'fail': function(data) {
                console.log('fail /pokemon/'+name+'/color: ' + data);
            }
        });
        //console.log("color after: " + color);
        return color;
    }
    // Sets the text fields
    function setupPokedex() {
        pokemon = JSON.parse(pokemon);
        console.log("pkmn: " + pokemon);
        $('#idOfPokemon').text(pokemon.id);
        $('#nameOfPokemon').text(pokemon.name.charAt(0).toUpperCase() + pokemon.name.slice(1));
        $('#heightOfPokemon').text(pokemon.height);
        $('#weightOfPokemon').text(pokemon.weight);
        $('#colorOfPokemon').text(color[0].toUpperCase()+color.slice(1));
        let pokemonTypes = pokemon["types"];
        if (pokemonTypes.length > 1) {
            $('#typeOfPokemon').text(pokemonTypes[0].type.name[0].toUpperCase()+pokemonTypes[0].type.name.slice(1) +
                " and " +
                pokemonTypes[1].type.name[0].toUpperCase()+pokemonTypes[1].type.name.slice(1));
        }
        else {
            $('#typeOfPokemon').text(pokemonTypes[0].type.name[0].toUpperCase()+pokemonTypes[0].type.name.slice(1));
        }
        $('#description').text(desc);

        $('#locationsDiv').html("");
        if (locations.length === 0) {
            $('#locationsDiv').append("<h3 id=\"location\">No known locations!</h3>");
        }
        else {
            for(let i=0; i<locations.length; i++) {
                $('#locationsDiv').append("<h3 id=\"location"+i+"\">"+locations[i]+"</h3>");
            }
        }

        $('#movesDiv').html("");
        for(let i=0; i<moves.length; i++) {
            $('#movesDiv').append("<h3 id=\"moves"+i+"\">"+moves[i]+"</h3>");
        }
    }
    // Determines if the input is valid or not
    function isNameValid(name) {
        let result = false;
        $.ajax({
            type: "GET",
            url: "pokemon/"+name+"/validateName",
            async: false,
            dataType: "application/json",
            crossDomain: true,
            statusCode: {
                200: function() {
                    result = true;
                },
                400: function() {
                    result = false;
                }
            }
        });
        return result;
    }
    // Shows the div based on the tab click
    function showDivAndBoldText(div) {
        if (div === "descriptionDiv") {
            $('#descriptionDiv').show();
            $('#locationsDiv').hide();
            $('#movesDiv').hide();
            $('#berriesDiv').hide();
            $('#descTabBtn').css('font-weight', 'bold');
            $('#locTabBtn').css('font-weight', 'normal')
            $('#movesTabBtn').css('font-weight', 'normal')
            $('#berriesTabBtn').css('font-weight', 'normal')
        }
        else if (div === "locationsDiv") {
            $('#descriptionDiv').hide();
            $('#locationsDiv').show();
            $('#movesDiv').hide();
            $('#berriesDiv').hide();
            $('#descTabBtn').css('font-weight', 'normal');
            $('#locTabBtn').css('font-weight', 'bold')
            $('#movesTabBtn').css('font-weight', 'normal')
            $('#berriesTabBtn').css('font-weight', 'normal')
        }
        else if (div === "movesDiv") {
            $('#descriptionDiv').hide();
            $('#locationsDiv').hide();
            $('#movesDiv').show();
            $('#berriesDiv').hide();
            $('#descTabBtn').css('font-weight', 'normal');
            $('#locTabBtn').css('font-weight', 'normal')
            $('#movesTabBtn').css('font-weight', 'bold')
            $('#berriesTabBtn').css('font-weight', 'normal')
        }
        // else if (div === "berriesDiv") {
        //     $('#descriptionDiv').hide();
        //     $('#locationsDiv').hide();
        //     $('#movesDiv').hide();
        //     $('#berriesDiv').show();
        //     $('#descTabBtn').css('font-weight', 'normal');
        //     $('#locTabBtn').css('font-weight', 'normal')
        //     $('#movesTabBtn').css('font-weight', 'normal')
        //     $('#berriesTabBtn').css('font-weight', 'bold')
        // }
    }
    // Default setting for button weights
    function setButtonWeightDefaults()  {
        $('#descTabBtn').css('font-weight', 'bold');
        $('#locTabBtn').css('font-weight', 'normal');
        $('#movesTabBtn').css('font-weight', 'normal');
        //$('#berriesTabBtn').css('font-weight', 'normal');
    }
    // Default setting for tab visuals
    function setTabDivDefaults() {
        $('#descriptionDiv').show();
        $('#locationsDiv').hide();
        $('#movesDiv').hide();
        //$('#berriesDiv').hide();
    }
</script>

</html>