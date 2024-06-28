<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/images/pokeball.jpg"/>
    <title>Pokedex Springboot</title>
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

    <link href="${pageContext.request.contextPath}/resources/css/gifSlider.css"
          type="text/css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/resources/css/pokemonGrid.css"
          type="text/css" rel="stylesheet">
    <style>
        html {
            background-position: center center;
            background-repeat:  no-repeat;
            background-attachment: fixed;
            background-size:  cover;
        }
        body {
            display: block;
            margin: 8px;
            background-position: center center;
            background-repeat:  no-repeat;
            background-attachment: fixed;
            background-size:  cover;
        }
        .button {
            font-weight: bold;
        }
        .cursor {
            cursor:pointer;
        }
        .center {
            padding: 70px 0;
            text-align: center;
            vertical-align: middle;
        }
        h1 {
            /*padding: 70px 0;*/
            text-align: center;
            line-height: 1.5;
            display: inline-block;
            vertical-align: middle;
        }
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
        .pokedexStyle {
            display: flow;
            text-align: center;
            vertical-align: middle;
        }
        @media only screen and (max-width: 300px) {
            .pokedexStyle {
                display: inline-flex;
                text-align: center;
                vertical-align: middle;
                width: 100px;
            }
        }
    </style>
</head>

<body style="justify-content:space-evenly;text-align:center;">
    <h1 id="title" style="vertical-align: middle;">
        <a href="${pageContext.request.contextPath}/search">
            <span class="center">
            <img alt="pokedex" src="${pageContext.request.contextPath}/images/pokedexImage.jpg" style="width:100%;"></span>
        </a>
    </h1>
    <br>

    <nav aria-label="Pokedex navigation">
        <ul class="pagination justify-content-center">
            <c:forEach begin="${page}" end="8" var="pageNumber">
                <c:if test="${pageNumber == 1}">
                    <li class="page-item disabled">
                        <a class="page-link" href="#" tabindex="-1">Previous</a>
                    </li>
                </c:if>
                <c:if test="${pageNumber <= 8}">
                    <li class="page-item"><a class="page-link" href="/${pageNumber}">${pageNumber}</a></li>
                </c:if>
                <c:if test="${pageNumber >= 8}">
                    <li class="page-item"><a class="page-link" href="/">...</a></li>
                    <li class="page-item">
                        <a class="page-link" href="/${totalPages}" >
                                ${totalPages}</a>
                    </li>
                </c:if>
                <c:if test="${pageNumber != totalPages && pageNumber == 8}">
                    <li class="page-item">
                        <a class="page-link" href="#">Next</a>
                    </li>
                </c:if>
            </c:forEach>
        </ul>
    </nav>

    <div style="display:inline-flex;align-items:center;">
<!--        <mat-slide-toggle (click)="showGifs = !showGifs;" title="If GIF is not present, official artwork will show!">-->
<!--            Show GIFs-->
<!--        </mat-slide-toggle>-->
        <label class="switch" title="If GIF is not present, official artwork will show!">
            <input id="gifSwitch" type="checkbox" onclick="toggleGifs();">
            <span class="slider round"></span>
        </label>
        &nbsp;
        <label style="margin:10px auto;">Show GIFs</label>
        &emsp;
        <div id="jumpToPage" style="display:flex;">
            <label for="pageNumber"></label>
            <input id="pageNumber" type="text" placeholder="Page #" style="width:auto;"/>
            <button class="icon">Jump to Page</button>
        </div>
        &emsp;
        <div id="showPokemon" style="display:flex;">
            <label for="showPkmnNumber"></label>
            <input id="showPkmnNumber" name="showPkmnNumber" type="text" placeholder="# of PkMn" style="width:auto;"/>
            <button class="icon" onclick="setPkmnPerPage();">Show Pokemon</button>
        </div>
    </div>
    <br>
    <div class="pokemon-grid">
        <c:forEach items="${pokemonMap.entrySet()}" var="pokemon">
            <c:set var="pokemonId" value="${pokemon.value.id}" />
            <div id="pokemon${pokemonId}">
                <a href="search/${pokemon.value.id}">
                    <div id="pokemon${pokemonId}Box" class="box" title="Click for more info" style="background-color:${pokemon.value.color}">
                        <div id="nameAndId" style="display:inline-flex;">
                            <h3 id="name" style="color:black;">${pokemon.value.name}</h3>
                            <div style="display: block;">&nbsp;&nbsp;&nbsp;&nbsp;</div>
                            <h3 id="id" style="color:black;">ID: ${pokemon.value.id}</h3>
                        </div>
                        <div id="image">
                            <c:choose>
                                <c:when test="${defaultImagePresent}">
                                    <c:if test="${!showGifs}">
                                        <img onmouseover="showArtwork(this, '${pokemon.value.officialImage}')" onmouseout="showArtwork(this, '${pokemon.value.defaultImage}');"
                                             src="${pokemon.value.defaultImage}" alt="${pokemon.value.name}-default">
                                    </c:if>
                                    <c:if test="${showGifs && gifImagePresent}">
                                        <img src="${pokemon.value.gifImage}" alt="${pokemon.value.name}-gif">
                                    </c:if>
                                    <c:if test="${showGifs && !gifImagePresent}">
                                        <c:if test="${officialImagePresent}">
                                            <img src="${pokemon.value.officialImage}" alt="${pokemon.value.name}-official">
                                        </c:if>
                                        <c:if test="${!officialImagePresent}">
                                            <img src="${pageContext.request.contextPath}/images/pokeball1.jpg" alt="no image found">
                                        </c:if>
                                    </c:if>
                                </c:when>
                                <c:otherwise> <!-- officialImage -->
                                    <c:if test="${officialImagePresent}">
                                        <img src="${pokemon.value.officialImage}" alt="${pokemon.value.name}-official">
                                    </c:if>
                                    <c:if test="${!officialImagePresent}">
                                        <img src="${pageContext.request.contextPath}/images/pokeball1.jpg" alt="no image found">
                                    </c:if>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div id="info" style="display:inline-block;">
                            <h3 id="heightOfPokemon" style="color:black;">Height: ${pokemon.value.height} m</h3><br>
                            <h3 id="weightOfPokemon" style="color:black;">Weight: ${pokemon.value.weight} kg</h3><br>
                            <h3 id="colorOfPokemon" style="color:black;">Color: ${pokemon.value.color}</h3><br>
                            <h3 id="typeOfPokemon" style="color:black;">Type: ${pokemon.value.type}</h3><br>
                        </div>
                    </div>
                </a>
            </div>
        </c:forEach>
    </div>

    <nav aria-label="Pokedex navigation">
        <ul class="pagination justify-content-center">
            <c:forEach begin="${page}" end="8" var="pageNumber">
                <c:if test="${pageNumber == 1}">
                    <li class="page-item disabled">
                        <a class="page-link" href="#" tabindex="-1">Previous</a>
                    </li>
                </c:if>
                <c:if test="${pageNumber <= 8}">
                    <li class="page-item"><a class="page-link" href="/${pageNumber}">${pageNumber}</a></li>
                </c:if>
                <c:if test="${pageNumber >= 8}">
                    <li class="page-item"><a class="page-link" href="/">...</a></li>
                    <li class="page-item">
                        <a class="page-link" href="/${totalPages}" >
                                ${totalPages}</a>
                    </li>
                </c:if>
                <c:if test="${pageNumber != totalPages && pageNumber == 8}">
                    <li class="page-item">
                        <a class="page-link" href="#">Next</a>
                    </li>
                </c:if>
            </c:forEach>
        </ul>
    </nav>

</body>

<script src="https://cdn.jsdelivr.net/npm/popper.js@1.12.9/dist/umd/popper.min.js" integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/js/bootstrap.min.js" integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl" crossorigin="anonymous"></script>
<script type="text/javascript">
    $(function(){
        updateGifToggle(false, "${showGifs}");

        let ids = ${pokemonIds};
        for(let i=0; i<ids.length; i++) {
            let pokemonBox = document.getElementById("pokemon"+(i+1)+"Box");
            let currentColor = pokemonBox.style.backgroundColor;
            pokemonBox.style.backgroundColor = changeColor(currentColor);
        }
    });

    function toggleGifs() {
        $.ajax({
            type: "GET",
            url: "toggleGifs",
            async: false,
            dataType: "application/json",
            crossDomain: true,
            statusCode: {
                200: function(data) {
                    updateGifToggle(true, data);
                },
                404: function() {
                    console.log('Failed');
                },
                500: function() {
                    console.log('Server Error');
                }
            }
        });
    }

    function updateGifToggle(reload, data) {
        let showGifs;
        try {
            showGifs = JSON.parse(data.responseText);
        } catch (error) {
            showGifs = data;
        }
        console.log("showGifs: " + showGifs);
        if (showGifs === 'true') $("#gifSwitch").attr("checked", true);
        else $("#gifSwitch").attr("checked", false);
        if (reload) location.reload();
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

    function showArtwork(imgTag, artwork) {
        imgTag.src = artwork;
    }

    function setPkmnPerPage() {
        let value = $("#showPkmnNumber").val();
        console.log("showPkmnNumber: " + value);
        $.ajax({
            type: "GET",
            url: "pkmnPerPage",
            data: {
                pkmnPerPage: value
            },
            async: false,
            dataType: "application/json",
            crossDomain: true,
            statusCode: {
                200: function(data) {
                    console.log(JSON.parse(JSON.stringify(data.responseText)));
                    location.reload();
                },
                400: function(data) {
                    console.log(JSON.parse(JSON.stringify(data.responseText)));
                    },
                404: function() {
                    console.log('Resource not found');
                },
                500: function() {
                    console.log('Server Error');
                }
            }
        });
    }

</script>

</html>