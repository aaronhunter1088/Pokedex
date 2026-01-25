<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Pok&#233;mon Info</title>
    <jsp:include page="headCommon.jsp"/>
    <style>
        /* Style the tab */
        .tab {
            overflow: hidden;
            border: 1px solid #ccc;
            background-color: #f1f1f1;
        }
        /* Style the buttons that are used to open the tab content */
        .tab button {
            background-color: inherit;
            float: left;
            border: none;
            outline: none;
            cursor: pointer;
            padding: 14px 16px;
            transition: 0.3s;
        }
        .parent-div {
            display: flex;
            justify-content: center;
            align-items: center;
            width: 100%;
            position: relative;
        }
        .left-div {
            padding-left: 15px;
            position: absolute;
            left: 0;
        }
        .center-div {
            text-align: center;
        }
    </style>
</head>
<body style="justify-content:space-evenly;text-align:center;"
      class="${isDarkMode?'darkmode':'lightmode'}">
    <h1 id="pokedexSearchImgSearchLink" style="vertical-align:middle;">
        <a href="${pageContext.request.contextPath}/search?darkmode=${isDarkMode}" style="cursor:zoom-in;" title="Search">
            <span class="center">
            <img alt="pokedex" src="${pageContext.request.contextPath}/images/pokedex.jpg" style="width:100%;"></span>
        </a>
    </h1>
    <div id="pokedex">
        <div class="pokedex-grid">
            <div id="pokemonInfo" class="box" style="padding-top:10px;background-color:${pokemon.color};">
                <!-- Back arrow, Name, and ID -->
                <div id="backAndName" class="parent-div">
                    <div id="backArrow" class="left-div">
                        <a href="${pageContext.request.contextPath}/?darkmode=${isDarkMode}"><i class="fas fa-arrow-left fa-2x" style="color:#000000;"></i></a>
                    </div>
                    <div id="nameAndID" class="center-div" style="display:inline-flex;gap:20px;">
                        <h3 id="name">Name: ${pokemon.name}</h3>
                        <h3 id="idOfPokemon">ID: ${pokemon.id}</h3>
                    </div>
                </div>
                <br>
                
                <!-- Pokemon Image and Basic Info -->
                <div id="imageAndBasicInfo" style="display:flex;justify-content:center;align-items:center;gap:20px;flex-wrap:wrap;">
                    <div id="imageOfPokemon" style="justify-content:center;display:block;">
                        <img id="pkmnImage" src="${defaultImage}" alt="image" style="width:250px;height:250px;">
                    </div>
                    <div id="basicInfo" style="text-align:left;">
                        <h3 id="heightOfPokemon">Height: ${pokemon.heightInInches} in</h3>
                        <h3 id="weightOfPokemon">Weight: ${pokemon.weightInPounds} lbs</h3>
                        <h3 id="colorOfPokemon">Color: ${pokemon.capitalizedColor}</h3>
                        <h3 id="typeOfPokemon">Type: ${pokemon.type}</h3>
                    </div>
                </div>
                <br>
                
                <!-- Action Buttons - Two Rows -->
                <div id="actionButtons" style="justify-content:center;text-align:center;">
                    <div id="infoButtonsDiv" style="display:flex;justify-content:center;gap:5px;flex-wrap:wrap;margin-bottom:10px;">
                        <button id="descriptionBtn" class="tab" onclick="showDiv('description');">Description</button>
                        <button id="locationsBtn" class="tab" onclick="showDiv('locations');">Locations</button>
                        <button id="movesBtn" class="tab" onclick="showDiv('moves');">Moves</button>
                        <button id="evolvesHowBtn" class="tab" onclick="showDiv('evolvesHow');">How ${pokemon.name} Evolves</button>
                    </div>
                    <div id="photoButtonsDiv" style="display:flex;justify-content:center;gap:5px;flex-wrap:wrap;">
                        <button id="defaultImgBtn" class="tab" onclick="showImage('default');">Show Default</button>
                        <button id="officialImgBtn" class="tab" onclick="showImage('official');">Show Official</button>
                        <button id="shinyImgBtn" class="tab" onclick="showImage('shiny');">Show Shiny</button>
                        <button id="gifImgBtn" class="tab" onclick="showImage('gif');">Show Gif</button>
                    </div>
                </div>
                <br>
                
                <!-- Info Display Divs -->
                <div id="descriptionDiv" style="display:inline-flex;width:300px;justify-content:center;">
                    <h3 id="description">${pokemon.description}</h3>
                </div>
                <div id="locationsDiv" style="overflow-y:scroll;overflow-x:hidden;height:200px;">
                    <c:choose>
                        <c:when test="${pokemon.locations.size() == 0}">
                            <h3 id="locations" class="listStyle">No known locations</h3>
                        </c:when>
                        <c:when test="${pokemon.locations.size() > 0}">
                            <c:forEach var="location" items="${pokemon.locations}">
                                <h3 id="locations" class="listStyle">${location}</h3>
                            </c:forEach>
                        </c:when>
                    </c:choose>
                </div>
                <div id="movesDiv" style="overflow-y:scroll;overflow-x:hidden;height:200px;">
                <c:forEach var="moveName" items="${pokemon.pokemonMoveNames}">
                    <h3 id="moves" class="listStyle">${moveName}</h3>
                </c:forEach>
                </div>
                <div id="evolvesHowDiv" style="overflow-y:scroll;overflow-x:hidden;height:200px;">
                    <jsp:include page="evolves-how.jsp" />
                </div>
            </div>
        </div>
    </div>

    <div id="evolutions"></div>

</body>

<c:set var="pokeballImg" value="/images/pokeball1.jpg"/>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.12.9/dist/umd/popper.min.js" integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/js/bootstrap.min.js" integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl" crossorigin="anonymous"></script>
<script type="text/javascript">
    $(function(){
        $("#defaultImgBtn").css('font-weight', 'bold');

        let pokemonInfoDiv = document.getElementById("pokemonInfo");
        pokemonInfoDiv.style.backgroundColor = changeColor("${pokemon.color}");

        defaultInfoDivs();
        $("#descriptionDiv").show();
        $("#descriptionBtn").css('font-weight', 'bold');
        if (Number.parseInt('${pokemon.id}') !== 0) {
            setEvolutionsDiv();
            updateEvolutionsDiv();
        }
    });

    function showImage(type) {
        let photoEle = $("#pkmnImage");
        let image;
        switch (type) {
            case 'default' : {
                image = "${defaultImage}";
                if (image === '') {
                    image = "${pokeballImg}";
                }
                $("#defaultImgBtn").css('font-weight', 'bold');
                $("#officialImgBtn").css('font-weight', 'normal');
                $("#shinyImgBtn").css('font-weight', 'normal');
                $("#gifImgBtn").css('font-weight', 'normal');
                break;
            }
            case 'official' : {
                image = "${officialImage}";
                if (image === '') {
                    image = "${pokeballImg}";
                }
                $("#defaultImgBtn").css('font-weight', 'normal');
                $("#officialImgBtn").css('font-weight', 'bold');
                $("#shinyImgBtn").css('font-weight', 'normal');
                $("#gifImgBtn").css('font-weight', 'normal');
                break;
            }
            case 'shiny' : {
                image = "${shinyImage}";
                if (image === '') {
                    image = "${pokeballImg}";
                }
                $("#defaultImgBtn").css('font-weight', 'normal');
                $("#officialImgBtn").css('font-weight', 'normal');
                $("#shinyImgBtn").css('font-weight', 'bold');
                $("#gifImgBtn").css('font-weight', 'normal');
                break;
            }
            case 'gif' : {
                image = "${gifImage}";
                console.log('image: ' + image);
                if (image === '') {
                    image = "${pokeballImg}";
                }
                $("#defaultImgBtn").css('font-weight', 'normal');
                $("#officialImgBtn").css('font-weight', 'normal');
                $("#shinyImgBtn").css('font-weight', 'normal');
                $("#gifImgBtn").css('font-weight', 'bold');
                break;
            }
            default : {
                image = "${pokeballImg}";
                break;
            }
        }
        photoEle.prop("src", image);
        photoEle.show();
    }

    function changeColor(pokemonColor) {
        console.log('changeColor: ' + pokemonColor);
        if (pokemonColor === "red") { return "#FA8072"; }
        else if (pokemonColor === "yellow") { return "#ffeb18"; }
        else if (pokemonColor === "green") { return "#AFE1AF"; }
        else if (pokemonColor === "blue") { return "#ADD8E6"; }
        else if (pokemonColor === "purple") { return "#CBC3E3"; }
        else if (pokemonColor === "brown") { return "#D27D2D"; }
        else if (pokemonColor === "white") { return "#d2cbd3"; }
        else if (pokemonColor === "pink") { return "#ef6bb6ff"; }
        else if (pokemonColor === "black") { return "#8f8b8b"}
        else if (pokemonColor === "gray" || pokemonColor === "grey") { return "#8f8b8b"}
        else return "#ffffff";
    }

    function defaultInfoDivs() {
        $("#descriptionDiv").hide();
        $("#descriptionBtn").css('font-weight', 'normal');
        $("#locationsDiv").hide();
        $("#locationsBtn").css('font-weight', 'normal');
        $("#movesDiv").hide();
        $("#movesBtn").css('font-weight', 'normal');
        $("#evolvesHowDiv").hide();
        $("#evolvesHowBtn").css('font-weight', 'normal');
    }

    function showDiv(divName) {
        defaultInfoDivs();
        switch (divName) {
            case 'description' : {
                $("#descriptionDiv").show();
                $("#descriptionDiv").css('display', 'inline-flex');
                $("#descriptionBtn").css('font-weight', 'bold');
                break;
            }
            case 'locations' : {
                $("#locationsDiv").show();
                $("#locationsBtn").css('font-weight', 'bold');
                break;
            }
            case 'moves' : {
                $("#movesDiv").show();
                $("#movesBtn").css('font-weight', 'bold');
                break;
            }
            case 'evolvesHow' : {
                $.ajax({
                    type: "GET",
                    url: "${pageContext.request.contextPath}/evolves-how",
                    async: false,
                    data: {
                        pokemonId: Number.parseInt('${pokemonId}')
                    },
                    dataType: "application/json",
                    crossDomain: true,
                    statusCode: {
                        200: function (data) {
                            $("#evolvesHowDiv").html(data.responseText);
                        },
                        404: function () {
                            console.log('Failed');
                            $("#evolutionsDiv").html('Request failed');
                        },
                        500: function () {
                            console.log('Server Error');
                        }
                    }
                });
                $("#evolvesHowDiv").show();
                $("#evolvesHowBtn").css('font-weight', 'bold');
                break;
            }
            default : {
                console.log('Should never reach here!');
                $("descriptionDiv").show();
            }
        }
    }

    function setEvolutionsDiv() {
        let response = $.ajax({
            type: "GET",
            url: "${pageContext.request.contextPath}/evolutions/" + "${pokemonId}?darkmode=${isDarkMode}",
            async: false,
            dataType: "json",
            crossDomain: true,
            success: function (data) {
                return data.responseText;
            },
            error: function (jqXHR, textStatus, errorThrown) {
                if (jqXHR.status === 404) {
                    console.log('Failed');
                    $("#evolutions").html('Request failed');
                } else if (jqXHR.status === 500) {
                    console.log('Server Error');
                }
            }
        });
        if (response.responseText !== undefined) {
            $("#evolutions").html(response.responseText);
        }
    }

    function updateEvolutionsDiv() {
        let pokemonDivs = $("#evolutions div div a div.box");
        pokemonDivs.each((index, div) => {
            div.style.backgroundColor = changeColor(div.style.backgroundColor);
            console.log('updated color of ' + div.id);
        });
    }

</script>

</html>
