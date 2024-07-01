<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/images/pokeball.jpg"/>
    <title>Pokemon Info</title>
    <!--- jQuery -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.7.1/jquery.min.js" integrity="sha512-v2CJ7UaYy4JwqLDIrZUI/4hqeoQieOmAZNXBeQyjo21dadnwR+8ZaIJVT8EE2iyI61OV8e6M8PP2/4hpQINQ/g==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
    <link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
    <script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
    <!-- Bootstrap -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/css/bootstrap.min.css" integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">
    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">
    <!-- Optional theme -->
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap-theme.min.css">
    <!-- Latest compiled and minified JavaScript -->
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script>
    <!-- JavaScript Bundle with Popper -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.0/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-gH2yIJqKdNHPEq0n4Mqa/HGKIhSkIHeL5AyhkYV8i59U5AR6csBvApHHNl/vI1Bx" crossorigin="anonymous">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.2.0/css/all.css" integrity="sha384-hWVjflwFxL6sNzntih27bfxkr27PmbbK/iSvJ+a4+0owXq79v+lsFkW54bOGbiDQ" crossorigin="anonymous">

    <link href="${pageContext.request.contextPath}/resources/css/pokedex.css"
          type="text/css" rel="stylesheet">
    <style>
        .center {
            padding: 10px 0;
            text-align: center;
            vertical-align: middle;
        }
    </style>
</head>
<body>
<h1 id="title" class="center">
    <a href="${pageContext.request.contextPath}/search">
        <img alt="pokedex" src="${pageContext.request.contextPath}/images/pokedexImage.jpg" style="">
    </a>
</h1>
<div id="pokedex">
    <div class="pokedex-grid">
        <div id="nameAndImages" class="box" style="padding-top: 10px;background-color:${color};">
            <div id="backAndName">
                <div id="backArrow" style="float:left;">
                    <a href="${pageContext.request.contextPath}/" title="Click here to return to Homepage">
                        <i class="fas fa-arrow-left" style="color:#000000;width:50px; height:50px;"></i>
                    </a>
                </div>
                <div id="mainName" style="justify-content:center;padding-top:2%;padding-right:10%;">
                    <h1 id="infoTitle">${pkmnName}</h1>
                </div>
            </div>
            <div id="imageOfPokemon" style="justify-content:center;display:block;">
                <img id="pkmnImage" src="${defaultImage}" alt="defaultImage">
            </div>
            <div id="photoButtonsDiv" style="justify-content:center;text-align:center;display:inline-flex;">
                <div style="">
                    <button id="defaultImgBtn" class="tab" onclick="showImage('default');">Show Default</button>
                </div>
                <div style="">
                    <button id="officialImgBtn" class="tab" onclick="showImage('official');">Show Official</button>
                </div>
                <div style="">
                    <button id="shinyImgBtn" class="tab" onclick="showImage('shiny');">Show Shiny</button>
                </div>
                <div style="">
                    <button id="gifImgBtn" class="tab" onclick="showImage('gif');">Show Gif</button>
                </div>
            </div>
        </div>
        <div id="info" class="box" style="padding-top: 10px;background-color:${color};">
            <div id="nameAndID" style="display:inline-flex">
                <h3 id="name">Name: ${pkmnName}</h3> &nbsp;&nbsp;&nbsp;&nbsp;
                <h3 id="idOfPokemon">ID: ${pkmnId}</h3>
            </div>
            <br>
            <div id="hAndW" style="display:inline-flex;">
                <h3 id="heightOfPokemon">Height: ${height} m</h3> &nbsp;&nbsp;&nbsp;&nbsp;
                <h3 id="weightOfPokemon">Weight: ${weight} kg</h3>
            </div>
            <br>
            <div id="colorAndType" style="display:inline-flex;">
                <h3 id="colorOfPokemon">Color: ${color}</h3> &nbsp;&nbsp;&nbsp;&nbsp;
                <h3 id="typeOfPokemon">Type: ${pkmnType}</h3>
            </div>
            <div style="display: flow;">
                <button id="descriptionBtn" class="tab" onclick="showDiv('description');">Description</button>
                <button id="locationsBtn" class="tab" onclick="showDiv('locations');">Locations</button>
                <button id="movesBtn" class="tab" onclick="showDiv('moves');">Moves</button>
                <button id="evolutionsBtn" class="tab" onclick="showDiv('evolvesHow');">How ${pokemonName} Evolves</button>
            </div>
            <br>
            <div id="descriptionDiv" style="display:inline-flex; width:300px; justify-content:center;">
                <h3 id="description">${description}</h3>
            </div>
            <div id="locationsDiv" style="overflow-y: scroll; overflow-x: hidden; height:200px;">
                <c:choose>
                    <c:when test="${pkmnLocations.size() == 0}">
                        <h3 id="locations" class="listStyle">No known locations</h3>
                    </c:when>
                    <c:when test="${pkmnLocations.size() > 0}">
                        <c:forEach var="location" items="${pkmnLocations}">
                            <h3 id="locations" class="listStyle">${location}</h3>
                        </c:forEach>
                    </c:when>
                </c:choose>
            </div>
            <div id="movesDiv" style="overflow-y: scroll; overflow-x: hidden; height:200px;">
                <c:forEach var="move" items="${pkmnMoves}">
                    <h3 id="moves" class="listStyle"
                    >${move}</h3>
                </c:forEach>
            </div>
            <div id="evolvesHowDiv" style="overflow-y: scroll; overflow-x: hidden; height:200px;">
                <jsp:include page="evolves-how.jsp" />
            </div>
        </div>
    </div>
</div>

<div id="evolutions">

</div>
<%--<jsp:include page="evolutions.jsp">--%>
<%--    <jsp:param name="pokemonID" value="${pkmnId}" />--%>
<%--</jsp:include>--%>
</body>

<script src="https://cdn.jsdelivr.net/npm/popper.js@1.12.9/dist/umd/popper.min.js" integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/js/bootstrap.min.js" integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl" crossorigin="anonymous"></script>
<script type="text/javascript">
    $(function(){
        $("#defaultImgBtn").css('font-weight', 'bold');

        let nameAndImagesDiv = document.getElementById("nameAndImages");
        nameAndImagesDiv.style.backgroundColor = changeColor("${color}");
        let infoDiv = document.getElementById("info");
        infoDiv.style.backgroundColor = changeColor("${color}");

        defaultInfoDivs();
        $("#descriptionDiv").show();
        setEvolutionsDiv();
    });

    function showImage(type) {
        let photoEle = $("#pkmnImage");
        let image;
        switch (type) {
            case 'default' : {
                image = "${sprites.get("default")}";
                if (image === '') {
                    image = "/images/pokeball1.jpg";
                }
                $("#defaultImgBtn").css('font-weight', 'bold');
                $("#officialImgBtn").css('font-weight', 'normal');
                $("#shinyImgBtn").css('font-weight', 'normal');
                $("#gifImgBtn").css('font-weight', 'normal');
                break;
            }
            case 'official' : {
                image = "${sprites.get("official")}";
                if (image === '') {
                    image = "/images/pokeball1.jpg";
                }
                $("#defaultImgBtn").css('font-weight', 'normal');
                $("#officialImgBtn").css('font-weight', 'bold');
                $("#shinyImgBtn").css('font-weight', 'normal');
                $("#gifImgBtn").css('font-weight', 'normal');
                break;
            }
            case 'shiny' : {
                image = "${sprites.get("shiny")}";
                if (image === '') {
                    image = "/images/pokeball1.jpg";
                }
                $("#defaultImgBtn").css('font-weight', 'normal');
                $("#officialImgBtn").css('font-weight', 'normal');
                $("#shinyImgBtn").css('font-weight', 'bold');
                $("#gifImgBtn").css('font-weight', 'normal');
                break;
            }
            case 'gif' : {
                image = "${sprites.get("gif")}";
                if (image === '') {
                    image = "/images/pokeball1.jpg";
                }
                $("#defaultImgBtn").css('font-weight', 'normal');
                $("#officialImgBtn").css('font-weight', 'normal');
                $("#shinyImgBtn").css('font-weight', 'normal');
                $("#gifImgBtn").css('font-weight', 'bold');
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
        $("#locationsDiv").hide();
        $("#movesDiv").hide();
        $("#evolutionsDiv").hide();
    }

    function showDiv(divName) {
        defaultInfoDivs();
        switch (divName) {
            case 'description' : {
                $("#descriptionDiv").show();
                break;
            }
            case 'locations' : {
                $("#locationsDiv").show();
                break;
            }
            case 'moves' : {
                $("#movesDiv").show();
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
                        200: function(data) {
                            $("#evolvesHowDiv").html(data.responseText);
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
                $("#evolutionsDiv").show();
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
            url: "${pageContext.request.contextPath}/evolutions/" + "${pokemonId}",
            async: false,
            dataType: "json",
            crossDomain: true,
            success: function(data) {
                //$("#evolutions").html(data.responseText);
                console.log('evolutions: ' + data.responseText);
                return data.responseText;
            },
            error: function(jqXHR, textStatus, errorThrown) {
                if (jqXHR.status === 404) {
                    console.log('Failed');
                    $("#evolutions").html('Request failed');
                } else if (jqXHR.status === 500) {
                    console.log('Server Error');
                }
            }
        });
        if (response.responseText !== undefined) $("#evolutions").html(response.responseText);
    }

</script>

</html>
