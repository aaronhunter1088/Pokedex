<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!-- Mobile Header -->
<div class="mobile-header ${isDarkMode?'darkmode':'lightmode'}">
    <div class="pokemon-logo">
        <a href="${pageContext.request.contextPath}/search?darkmode=${isDarkMode}" title="Search">
            <img alt="pokedex" src="${pageContext.request.contextPath}/images/pokedex.jpg">
        </a>
    </div>
    <button class="mobile-menu-button" onclick="toggleMobileMenu();" aria-label="Menu">
        <i class="fa-solid fa-ellipsis-vertical"></i>
    </button>
</div>

<!-- Mobile Menu Overlay -->
<div class="mobile-menu-overlay" id="mobileMenuOverlay" onclick="closeMobileMenu();"></div>

<!-- Mobile Menu -->
<div class="mobile-menu ${isDarkMode?'darkmode':'lightmode'}" id="mobileMenu">
    <button class="mobile-menu-close" onclick="closeMobileMenu();" aria-label="Close menu">
        <i class="fa-solid fa-times"></i>
    </button>

    <div class="mobile-menu-item mobile-gif-item">
        <label>Show GIFs</label>
        <label class="switch" title="If GIF is not present, official artwork will show!">
            <input id="gifSwitchMobile" type="checkbox" onclick="toggleGifs();">
            <span class="slider round"></span>
        </label>
    </div>

    <div class="mobile-menu-item mobile-gif-item">
        <label>${isDarkMode ? 'Turn on Light Mode' : 'Turn on Dark Mode'}</label>
        <label class="switch" title="Toggle darkmode">
            <input id="gifSwitchDarkmode" type="checkbox" ${isDarkMode ? 'checked' : ''}
                   onclick="toggleDarkmode('${isDarkMode}');">
            <span class="slider round"></span>
        </label>
    </div>

    <div class="mobile-menu-item mobile-gif-item">
        <label for="searchMobile">Search for Pkmn</label>
        <input id="searchMobile" name="searchMobile" type="text" placeholder="Name or ID"/>
        <img alt="Get Pokémon"
             src="${pageContext.request.contextPath}/images/pokeball1.jpg"
             style="width:30px; height:30px; cursor: pointer;"
             title="Search for Pkmn"
             onclick="searchForPkmn('${isDarkMode}');">
    </div>

    <div class="mobile-menu-item mobile-gif-item">
        <label for="pageNumberMobile">Jump to Page</label>
        <input id="pageNumberMobile" name="pageNumberMobile" type="text" placeholder="Page #"/>
        <i class="fa-regular fa-circle-right" style="font-size:30px; cursor:pointer; color:${isDarkMode?'white':'black'}"
           onclick="setPageToView($('#pageNumberMobile').val());" title="Jump to Page">
        </i>
    </div>

    <div class="mobile-menu-item mobile-gif-item">
        <label for="showPkmnNumberMobile">Pok&#233mon Per Page</label>
        <input id="showPkmnNumberMobile" name="showPkmnNumberMobile" type="text" placeholder="# of PkMn"/>
        <i class="fa-regular fa-circle-right" style="font-size:30px; cursor:pointer; color:${isDarkMode?'white':'black'}"
           onclick="setPkmnPerPageMobile();" title="Show Pok&#233mon">
        </i>
    </div>

    <div class="mobile-menu-item">
        <button class="back-to-landing-btn icon" onclick="navigateToLandingPage()"
                title="Return to Landing Page">
            Back to Landing Page
        </button>
    </div>
</div>

<script>
    $(function(){
        //checks whether the pressed key is "Enter"
        $('#searchMobile').on('keypress', function(e) {
            if ($('#searchMobile').val() === '') return;
            if (e.code === 'Enter' || e.code === 'Return') {
                searchForPkmn('${isDarkMode}');
            }
        });
    });

    function toggleMobileMenu() {
        const menu = document.getElementById('mobileMenu');
        const overlay = document.getElementById('mobileMenuOverlay');
        menu.classList.toggle('active');
        overlay.classList.toggle('active');
        // Prevent body scroll when menu is open
        if (menu.classList.contains('active')) {
            document.body.style.overflow = 'hidden';
        } else {
            document.body.style.overflow = '';
        }
    }

    function closeMobileMenu() {
        const menu = document.getElementById('mobileMenu');
        const overlay = document.getElementById('mobileMenuOverlay');
        menu.classList.remove('active');
        overlay.classList.remove('active');
        document.body.style.overflow = '';
    }

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
                        url = 'pokedexEntry/' + nameOrId + '?darkmode=' + isDarkMode;
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

    function setPkmnPerPageMobile() {
        let value = $("#showPkmnNumberMobile").val();
        setPkmnPerPageImpl(value, true);
    }

    function setPkmnPerPageImpl(value, isMobile) {
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
                    if (isMobile) closeMobileMenu();
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

                    let ids = "${pokemonIds}".replace(/[\[\]]/g, '').split(',').map(id => id.trim());
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

    function getByPkmnType(selectObject) {
        let type = selectObject.value;

        // Show loading overlay if a type is selected (not "none")
        if (type !== 'none') {
            showLoadingOverlay(type);
        }

        $.ajax({
            type: "GET",
            url: "getPokemonByType",
            data: {
                chosenType: type
            },
            async: false,
            dataType: "application/json",
            crossDomain: true,
            statusCode: {
                200: function(data) {
                    console.log('200 chosenType');
                    // Navigate to homepage instead of reload to avoid duplicate fetching
                    window.location.href = '${pageContext.request.contextPath}/?darkmode=${isDarkMode}';
                },
                400: function(data) {
                    console.log(JSON.parse(JSON.stringify(data.responseText)));
                    hideLoadingOverlay();
                },
                404: function() {
                    console.log('Resource not found');
                    hideLoadingOverlay();
                },
                500: function() {
                    console.log('Server Error');
                    hideLoadingOverlay();
                }
            }
        });
        console.log(type);
    }
</script>