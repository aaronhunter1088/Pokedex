<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Pokémon Evolutions</title>
    <jsp:include page="headCommon.jsp"/>
    <style></style>
</head>
<body id="">
<c:choose>
    <c:when test="${pokemonFamily == null}">
        <h2>No Evolutions</h2>
    </c:when>
    <c:when test="${pokemonFamily != null}">
        <h2>Evolutions</h2>
        <div id="evolutions" style="display:inline-flex; justify-content: center; width:100%; height:45%;">
            <c:forEach var="pokemonList" items="${pokemonFamily}" varStatus="status">
                <c:set var="listNumber" value="${status.count}"/>
                <div style="display:block;">
                    <div id="stagesHeader"
                         style="display:inline-flex;justify-content:center;text-align-all:center;width:300px;height:10px;"
                         class=""> <!-- class="pokemon-grid box" -->
                        <h2>Stage ${listNumber}</h2>
                    </div>
                    <div style="overflow-y:scroll;overflow-x:hidden;height:460px;width:90%;">
                        <c:forEach var="pokemon" items="${pokemonList}" varStatus="status">
                            <div class="evolution-grid" style="">
                                <a href="${pageContext.request.contextPath}/pokedex/${pokemon.id}"
                                   style="text-decoration:none;color:black;">
                                    <div id="pokemon${pokemon.id}"
                                         style="background-color:${pokemon.color};display:inline-block;" class="box">
                                        <div id="idAndName" style="display:inline-flex;">
                                            <h3 id="${pokemon.name}-name">${pokemon.name}</h3>
                                            <p>&nbsp;&nbsp;&nbsp;&nbsp;</p>
                                            <h3 id="${pokemon.id}-id">ID: ${pokemon.id}</h3>
                                        </div>
                                        <div id="${pokemon.id}-img">
                                            <img style="width:200px;height:200px;"
                                                 src="${pokemon.defaultImage}" alt="${pokemon.name}-default">
                                        </div>
                                        <h3 id="${pokemon.id}-height">Height: ${pokemon.heightInInches} in</h3>
                                        <h3 id="${pokemon.id}-weight">Weight: ${pokemon.weightInPounds} lbs</h3>
                                        <h3 id="${pokemon.id}-color">Color: ${pokemon.capitalizedColor}</h3>
                                        <h3 id="${pokemon.id}-type">Type: ${pokemon.type}</h3>
                                    </div>
                                </a>
                                <c:if test="${!status.last}">
                                    <div style="padding-top:25px; text-align:center; justify-content:center;">
                                        <img style="width:25px;height:25px;" alt="pokeball"
                                             src="${pageContext.request.contextPath}/images/pokeball1.jpg">
                                    </div>
                                </c:if> <!-- pokeball divider -->
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:when>
</c:choose>
</body>

<script src="https://cdn.jsdelivr.net/npm/popper.js@1.12.9/dist/umd/popper.min.js"
        integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q"
        crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/js/bootstrap.min.js"
        integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl"
        crossorigin="anonymous"></script>
<script type="text/javascript">
    $(function () {
    });
</script>

</html>
