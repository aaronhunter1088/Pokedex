<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/images/pokeball.jpg"/>

<!-- jQuery UI CSS -->
<link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">

<!-- Bootstrap (keep a single version: 3.4.1) -->
<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">
<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap-theme.min.css">

<!-- Font Awesome -->
<%--<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css">--%>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/7.0.1/css/all.min.css" integrity="sha512-2SwdPD6INVrV/lHTZbO2nodKhrnDdJK9/kg2XD1r9uGqPo1cUbujc+IYdlYdEErWNu69gVcYgdxlmVmzTWnetw==" crossorigin="anonymous" referrerpolicy="no-referrer" />

<!-- Index / app styles -->
<link href="${pageContext.request.contextPath}/resources/css/gifSlider.css" type="text/css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/resources/css/pokemonGrid.css" type="text/css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/resources/css/pokedex.css" type="text/css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/resources/css/evolutions.css" type="text/css" rel="stylesheet">

<!-- jQuery (must come before jquery-ui and Bootstrap JS) -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.7.1/jquery.min.js"
        integrity="sha512-v2CJ7UaYy4JwqLDIrZUI/4hqeoQieOmAZNXBeQyjo21dadnwR+8ZaIJVT8EE2iyI61OV8e6M8PP2/4hpQINQ/g=="
        crossorigin="anonymous" referrerpolicy="no-referrer"></script>

<!-- jQuery UI (after jQuery) -->
<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>

<!-- Bootstrap JS (after jQuery) -->
<script src="https://stackpath.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script>

<!-- Add more common links and scripts here -->

<!-- Common styling -->
<style>
    html {
        background-position: center center;
        background-repeat: no-repeat;
        background-attachment: fixed;
        background-size: cover;
    }

    body {
        display: block;
        margin: 8px;
        background-position: center center;
        background-repeat: no-repeat;
        background-attachment: fixed;
        background-size: cover;
        justify-content: space-evenly;
        text-align: center;
    }

    .button {
        font-weight: bold;
    }

    .cursor {
        cursor: pointer;
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

    .lightmode {
        background-color: white;
        color: black;
    }
    .darkmode {
        background-color: black;
        color: white;
    }
</style>
