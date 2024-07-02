<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/images/pokeball.jpg"/>
    <title>Pokémon Evolutions</title>
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

    <link href="${pageContext.request.contextPath}/resources/css/evolutions.css"
          type="text/css" rel="stylesheet">
    <style></style>
</head>
<body id="">
    <h2>Evolutions</h2>
    <c:forEach var="stage" items="${stages}">
        <div id="stagesHeader" style="display:inline-flex;justify-content:center;text-align-all:center;width:300px;height:10px;" class="pokemon-grid box">
            <h2>Stage ${stage}</h2>
        </div>
    </c:forEach>
    <div id="evolutions" style="display:inline-flex; justify-content: center; width:100%; height:45%;">
        <c:forEach var="pokemonList" items="${pokemonFamily}">
            <div style="overflow-y:scroll; height:460px;">
                <c:forEach var="pokemon" items="${pokemonList}" varStatus="status">
                    <div class="pokemon-grid">
                        <a href="${pageContext.request.contextPath}/pokedex/${pokemon.id}" style="text-decoration: none; color: black;">
                            <div id="pokemon${pokemon.id}" style="background-color:${pokemon.color};display:inline-block;" class="box">
                                <div id="idAndName" style="display:inline-flex;">
                                    <h3 id="name">${pokemon.name}</h3>
                                    <p>&nbsp;&nbsp;&nbsp;&nbsp;</p>
                                    <h3 id="idOfPokemon">ID: ${pokemon.id}</h3>
                                </div>
                                <div id="image">
                                    <img style="width:100px; height:100px;"
                                         src="${pokemon.defaultImage}" alt="${pokemon.name}-default">
                                </div>
                                <h3 id="heightOfPokemon">Height: ${pokemon.height} m</h3>
                                <h3 id="weightOfPokemon">Weight: ${pokemon.weight} kg</h3>
                                <h3 id="colorOfPokemon">Color: ${pokemon.color}</h3>
                                <h3 id="typeOfPokemon">Type: ${pokemon.type}</h3>
                            </div>
                        </a>
                            <div style="padding-top:25px; text-align:center; justify-content:center;">
                                <c:if test="${!status.last}">
                                    <img style="width:25px; height:25px;" alt="pokeball"
                                         src="${pageContext.request.contextPath}/images/pokeball1.jpg">
                                </c:if>
                            </div>
                        </div>
                </c:forEach>
            </div>
        </c:forEach>
    </div>
</body>

<script src="https://cdn.jsdelivr.net/npm/popper.js@1.12.9/dist/umd/popper.min.js" integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/js/bootstrap.min.js" integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl" crossorigin="anonymous"></script>
<script type="text/javascript">
    $(function(){
    });


</script>

</html>
