<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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
    <nav aria-label="Page navigation example">
        <ul class="pagination justify-content-center">
            <li class="page-item disabled">
                <a class="page-link" href="#" tabindex="-1">Previous</a>
            </li>
            <li class="page-item"><a class="page-link" href="#">1</a></li>
            <li class="page-item"><a class="page-link" href="#">2</a></li>
            <li class="page-item"><a class="page-link" href="#">3</a></li>
            <li class="page-item">
                <a class="page-link" href="#">Next</a>
            </li>
        </ul>
    </nav>
    <div style="display:inline-flex;align-items:center;">
<!--        <mat-slide-toggle (click)="showGifs = !showGifs;" title="If GIF is not present, official artwork will show!">-->
<!--            Show GIFs-->
<!--        </mat-slide-toggle>-->
        <label class="switch">
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
            <input id="showPkmnNumber" type="text" placeholder="# of PkMn" style="width:auto;"/>
            <button class="icon">Show Pokemon</button>
        </div>
    </div>
    <br>
    <div class="pokemon-grid">
        <c:forEach items="${pokemonMap.entrySet()}" var="pokemon">
            <c:set var="pokemonId" value="${pokemon.value.id}" />
            <div id="pokemon${pokemonId}">
                <a href="search/${pokemon.value.id}">
                    <div class="box" title="Click for more info" style="background-color:${pokemon.value.color}">
                        <div id="nameAndId" style="display:inline-flex;">
                            <h3 id="name" style="color:black;">${pokemon.value.name}</h3>
                            <div style="display: block;">&nbsp;&nbsp;&nbsp;&nbsp;</div>
                            <h3 id="id" style="color:black;">ID: ${pokemon.value.id}</h3>
                        </div>
                        <div id="image">
                            <c:choose>
                                <c:when test="${defaultImagePresent}">
                                    <c:if test="${!showGifs}">
                                        <img src="${pokemon.value.defaultImage}" alt="${pokemon.value.name}-default">
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

    <nav aria-label="Page navigation example">
        <ul class="pagination justify-content-center">
            <li class="page-item disabled">
                <a class="page-link" href="#" tabindex="-1">Previous</a>
            </li>
            <li class="page-item"><a class="page-link" href="#">1</a></li>
            <li class="page-item"><a class="page-link" href="#">2</a></li>
            <li class="page-item"><a class="page-link" href="#">3</a></li>
            <li class="page-item">
                <a class="page-link" href="#">Next</a>
            </li>
        </ul>
    </nav>
</body>

<%--<script src="https://code.jquery.com/jquery-3.2.1.slim.min.js" integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN" crossorigin="anonymous"></script>--%>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.12.9/dist/umd/popper.min.js" integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/js/bootstrap.min.js" integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl" crossorigin="anonymous"></script>
<script type="text/javascript">
    $(function(){
        let showGifs = "${showGifs}";
        console.log("showGifs: " + showGifs);
        if (showGifs === 'true') $("#gifSwitch").attr("checked", true);
        else $("#gifSwitch").attr("checked", false);
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
                    let showGifs = JSON.parse(data.responseText);
                    console.log("showGifs: " + showGifs);
                    if (showGifs === 'true') $("#gifSwitch").attr("checked", true);
                    else $("#gifSwitch").attr("checked", false);
                    location.reload();
                },
                404: function() {
                    console.log('Failed')
                }
            }
        });
        <%--$.ajax({--%>
        <%--    type: "GET",--%>
        <%--    url: "/toggleGifs",--%>
        <%--    async: false,--%>
        <%--    dataType: "application/json",--%>
        <%--    success: function(data) {--%>
        <%--        let showGifs = data;--%>
        <%--        console.log("showGifs: " + showGifs);--%>
        <%--        if (showGifs === 'true') $("#gifSwitch").attr("checked", true);--%>
        <%--        else $("#gifSwitch").attr("checked", false);--%>
        <%--        let ids = ${pokemonIds};--%>
        <%--        for(let i=0; i<ids.length; i++) {--%>
        <%--            $("#pokemon"+i+1).load(location.href + " #pokemon"+i+1);--%>
        <%--        }--%>
        <%--    }--%>
        <%--});--%>
    }

</script>

</html>