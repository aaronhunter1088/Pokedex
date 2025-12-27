<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>How Evolves</title>
    <jsp:include page="headCommon.jsp"/>
    <style>
        .listStyle {
            display: block;
            margin: 0;
        }
    </style>
</head>
<body>
<c:choose>
    <c:when test="${emptyChain}">
        <h3>No Pokemon chain info found: Chain ID: ${pokemonChainId}</h3>
    </c:when>
    <c:when test="${!emptyChain}">
        <c:choose>
            <c:when test="${doesPokemonEvolve}">
                <c:if test="${hasMinimumLevel}">
                    <div id="minimumLevel">
                        <h3 id="levelOfPokemon1"><b>Evolves At Lvl: </b>${attributesMap.get('minLevel')}</h3>
                    </div>
                </c:if> <!-- with level -->
                <c:if test="${hasUseItem}">
                    <h3 id="itemToEvolveTitle" class="listStyle"><b>Evolves with: </b></h3>
                    <c:forEach var="item" items="${attributesMap.get('useItem')}">
                        <div id="useItem">
                            <h3 id="item">${item}</h3>
                        </div>
                    </c:forEach>
                </c:if> <!-- with use_item -->
                <c:if test="${hasHeldItem}">
                    <h3 id="heldItemToEvolveTitle" class="listStyle"><b>Evolves by holding: </b></h3>
                    <c:forEach var="heldItem" items="${attributesMap.get('heldItem')}">
                        <div id="useItem">
                            <h3 id="heldItem">${heldItem}</h3>
                        </div>
                    </c:forEach>
                </c:if> <!-- with held_item -->
                <c:if test="${hasMinimumHappiness}">
                    <h3 id="happiness"><b>Evolves with Happiness: </b>${attributesMap.get('minHappiness')}</h3>
                </c:if> <!-- with happiness -->
                <c:if test="${hasMinimumAffection}">
                    <h3 id="affection"><b>Evolves with Affection: </b>${attributesMap.get('minAffection')}</h3>
                </c:if> <!-- with affection -->
                <c:if test="${hasBeauty}">
                    <h3 id="beauty"><b>Evolves with Beauty: </b>${attributesMap.get('minBeauty')}</h3>
                </c:if> <!-- with beauty -->
                <c:if test="${hasDayNight}">
                    <h3 id="dayNightTitle" class="listStyle"><b>Evolves during: </b></h3>
                    <c:forEach var="dayNight" items="${attributesMap.get('timeOfDay')}">
                        <h3 id="dayNight" class="listStyle">${dayNight}</h3>
                    </c:forEach>
                </c:if> <!-- with time of day -->
                <c:if test="${hasLocations}">
                    <h3 id="locationsTitle" class="listStyle"><b>Evolves at location: </b></h3>
                    <c:forEach var="location" items="${attributesMap.get('location')}">
                        <h3 id="location{{i+1}}" class="listStyle">${location}</h3>
                    </c:forEach>
                </c:if> <!-- with locations -->
                <c:if test="${hasKnownMoves}">
                    <h3 id="knownMovesTitle" class="listStyle"><b>Evolves with move: </b></h3>
                    <c:forEach var="move" items="${attributesMap.get('knownMove')}">
                        <h3 id="move" class="listStyle">${move}</h3>
                    </c:forEach>
                </c:if> <!-- with known moves -->
                <c:if test="${hasKnownMoveType}">
                    <h3 id="knownMovesTypesTitle" class="listStyle"><b>Evolves with move type: </b></h3>
                    <c:forEach var="moveType" items="${attributesMap.get('knownMoveType')}">
                        <h3 id="knownMoveType{{i+1}}" class="listStyle">${moveType}</h3>
                    </c:forEach>
                </c:if> <!-- with known moves types -->
                <c:if test="${hasNeedsRain}">
                    <h3 id="needsRainTitle" class="listStyle"><b>Evolves with
                        rain: ${attributesMap.get('needsRain')}</b></h3>
                </c:if> <!-- with needs rain -->
                <c:if test="${hasTurnUpsideDown}">
                    <h3 id="turnUpsideDownTitle" class="listStyle"><b>Evolves by turning Upside
                        Down: ${attributesMap.get('turnUpsideDown')}</b></h3>
                </c:if> <!-- with turn upside down -->
                <c:if test="${hasTradeSpecies}">
                    <h3 id="tradeSpeciesTitle" class="listStyle"><b>Evolves by Trading</b></h3>
                </c:if> <!-- with trade species -->
            </c:when>
            <c:when test="${!doesPokemonEvolve}">
                <h3 id="levelOfPokemon">Final Form</h3>
            </c:when>
        </c:choose>
    </c:when>
</c:choose>
</body>
</html>
