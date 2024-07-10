<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Pokedex Springboot</title>
    <jsp:include page="headCommon.jsp"/>
    <style></style>
</head>

<body style="justify-content:space-evenly;text-align:center;">
    <h1 id="indexSearchImgSearchLink" style="vertical-align:middle;">
        <a href="${pageContext.request.contextPath}/search" style="cursor:zoom-in;" title="Search">
            <span class="center">
            <img alt="pokedex" src="${pageContext.request.contextPath}/images/pokedexImage.jpg" style="width:100%;"></span>
        </a>
    </h1>
    <br>

    <jsp:include page="navigation.jsp"/>

    <div style="display:inline-flex;align-items:center;">
        <label class="switch" title="If GIF is not present, official artwork will show!">
            <input id="gifSwitch" type="checkbox" onclick="toggleGifs();">
            <span class="slider round"></span>
        </label>
        &nbsp;
        <label style="margin:10px auto;width:auto;padding-top:0;">Show GIFs</label>
        &emsp;
        <div id="jumpToPage" style="display:flex;">
            <label for="pageNumber"></label>
            <input id="pageNumber" name="pageNumber" type="text" placeholder="Page #" style="width:auto;"/>
            <button class="icon" onclick="setPageToView();" title="Jump to Page">Jump to Page</button>
        </div>
        &emsp;
        <div id="showPokemon" style="display:flex;">
            <label for="showPkmnNumber"></label>
            <input id="showPkmnNumber" name="showPkmnNumber" type="text" placeholder="# of PkMn" style="width:auto;"/>
            <button class="icon" onclick="setPkmnPerPage();" title="Show Pokemon">Show Pokemon</button>
        </div>
    </div>
    <br>
    <div id="pokemonGrid" class="list-grid">
        <c:forEach items="${pokemonMap.entrySet()}" var="pokemon">
            <c:set var="pokemonId" value="${pokemon.value.id}" />
            <div id="pokemon${pokemonId}">
                <a href="pokedex/${pokemon.value.id}">
                    <div id="pokemon${pokemonId}Box" class="box" title="Click for more info" style="background-color:${pokemon.value.color};">
                        <div id="nameAndId" style="display:inline-flex;">
                            <h3 id="name" style="color:black;">${pokemon.value.name}</h3>
                            <div style="display: block;">&nbsp;&nbsp;&nbsp;&nbsp;</div>
                            <h3 id="id" style="color:black;">ID: ${pokemon.value.id}</h3>
                        </div>
                        <div id="image">
                            <c:choose>
                                <c:when test="${pokemon.value.defaultImage != null}">
                                    <c:if test="${!showGifs}">
                                        <img onmouseover="showArtwork(this, '${pokemon.value.officialImage}')" onmouseout="showArtwork(this, '${pokemon.value.defaultImage}');"
                                             src="${pokemon.value.defaultImage}" alt="${pokemon.value.name}-default" style="height:200px;width:200px;">
                                    </c:if>
                                    <c:if test="${showGifs && pokemon.value.gifImage != null}">
                                        <img src="${pokemon.value.gifImage}" alt="${pokemon.value.name}-gif">
                                    </c:if>
                                    <c:if test="${showGifs && pokemon.value.gifImage == null}">
                                        <c:if test="${pokemon.value.officialImage != null}">
                                            <img src="${pokemon.value.officialImage}" alt="${pokemon.value.name}-official">
                                        </c:if>
                                        <c:if test="${pokemon.value.officialImage == null}">
                                            <img src="${pageContext.request.contextPath}/images/pokeball1.jpg" alt="no image found">
                                        </c:if>
                                    </c:if>
                                </c:when>
                                <c:otherwise> <!-- officialImage -->
                                    <c:if test="${pokemon.value.officialImage != null}">
                                        <img src="${pokemon.value.officialImage}" alt="${pokemon.value.name}-official">
                                    </c:if>
                                    <c:if test="${pokemon.value.officialImage == null}">
                                        <img src="${pageContext.request.contextPath}/images/pokeball1.jpg" alt="no image found">
                                    </c:if>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div id="info" style="display:inline-block;">
                            <h3 id="heightOfPokemon" style="color:black;">Height: ${pokemon.value.height} m</h3><br>
                            <h3 id="weightOfPokemon" style="color:black;">Weight: ${pokemon.value.weight} kg</h3><br>
                            <h3 id="colorOfPokemon" style="color:black;">Color: ${pokemon.value.capitalizedColor}</h3><br>
                            <h3 id="typeOfPokemon" style="color:black;">Type: ${pokemon.value.type}</h3><br>
                        </div>
                    </div>
                </a>
            </div>
        </c:forEach>
    </div>

    <jsp:include page="navigation.jsp"/>

</body>

<script src="https://cdn.jsdelivr.net/npm/popper.js@1.12.9/dist/umd/popper.min.js" integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/js/bootstrap.min.js" integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl" crossorigin="anonymous"></script>
<script type="text/javascript">
    $(function(){
        updateGifToggle(false, "${showGifs}");

        let ids = ${pokemonIds};
        for(let i=0; i<ids.length; i++) {
            let pokemonBox = document.getElementById("pokemon"+(ids[i])+"Box");
            let currentColor = pokemonBox.style.backgroundColor;
            pokemonBox.style.backgroundColor = changeColor(currentColor);
        }

        let element = $(".page-link").filter(function() {
            return $(this).html() === '${page}';
        });
        if (element !== undefined) {
            element.addClass("active");
        }

        $('#pageNumber').on('keypress', function(e) {
            if ($('#pageNumber').val() === '') return;
            if (e.code === 'Enter' || e.code === 'Return') {
                setPageToView($('#pageNumber').val());
            }
        });

        $('#showPkmnNumber').on('keypress', function(e) {
            if ($('#showPkmnNumber').val() === '') return;
            if (e.code === 'Enter' || e.code === 'Return') {
                setPkmnPerPage();
            }
        });
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

    function setPageToView(pageNumber) {
        let value = $("#pageNumber").val();
        if (pageNumber !== undefined) value = pageNumber;
        console.log("page to view: " + value);
        $.ajax({
            type: "GET",
            url: "page",
            data: {
                pageNumber: value
            },
            async: false,
            dataType: "application/json",
            crossDomain: true,
            statusCode: {
                200: function(data) {
                    console.log('200 setPageToView');
                    console.log(JSON.parse(JSON.stringify(data.responseText)));
                    location.reload();

                    let ids = ${pokemonIds};
                    for(let i=0; i<ids.length; i++) {
                        let pokemonBox = document.getElementById("pokemon"+(ids[0])+"Box");
                        let currentColor = pokemonBox.style.backgroundColor;
                        pokemonBox.style.backgroundColor = changeColor(currentColor);
                    }
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