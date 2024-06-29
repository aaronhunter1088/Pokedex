<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/images/pokeball.jpg"/>
  <title>Navigation</title>
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
<body>
<nav aria-label="Pokedex navigation">
  <ul class="pagination justify-content-center">
    <!-- Previous -->
    <c:choose>
      <c:when test="${page == 1}">
        <li class="page-item disabled">
          <button class="page-link" onclick="setPageToView(${page-1});">Previous</button>
        </li>
      </c:when>
      <c:when test="${page != 1}">
        <li class="page-item">
          <button class="page-link" onclick="setPageToView(${page-1});">Previous</button>
        </li>
      </c:when>
    </c:choose>
    <!-- Pages -->
    <c:choose>
      <c:when test="${page < 6}">
        <c:forEach begin="1" end="8" var="pageNumber">
          <c:if test="${pageNumber <= 8}">
            <li class="page-item">
              <button class="page-link" onclick="setPageToView(${pageNumber});">${pageNumber}</button>
            </li>
          </c:if>
          <c:if test="${pageNumber == 8}">
            <li class="page-item">
              <button class="page-link" onclick="setPageToView();">...</button>
            </li>
            <li class="page-item">
              <button class="page-link" onclick="setPageToView();">${totalPages}</button>
            </li>
          </c:if>
        </c:forEach>
      </c:when>
      <c:when test="${page >= 6}">
        <c:forEach begin="1" end="${page+8}" var="pageNumber">
          <c:if test="${pageNumber == 1}">
            <li class="page-item">
              <button class="page-link" onclick="setPageToView(1);">1</button>
            </li>
            <li class="page-item">
              <button class="page-link" onclick="setPageToView();">...</button>
            </li>
          </c:if>
          <c:if test="${pageNumber == 2}">
            <li class="page-item">
              <button class="page-link" onclick="setPageToView(${page-2});">${page-2}</button>
            </li>
          </c:if>
          <c:if test="${pageNumber == 3}">
            <li class="page-item">
              <button class="page-link" onclick="setPageToView(${page-1});">${page-1}</button>
            </li>
          </c:if>
          <c:if test="${pageNumber == 4}">
            <li class="page-item">
              <button class="page-link" style="background-color:#2196F3;color:#ffffff;" onclick="setPageToView(${page});">${page}</button>
            </li>
          </c:if>
          <c:if test="${pageNumber == 5}">
            <li class="page-item">
              <button class="page-link" onclick="setPageToView(${page+1});">${page+1}</button>
            </li>
          </c:if>
          <c:if test="${pageNumber == 6}">
            <li class="page-item">
              <button class="page-link" onclick="setPageToView(${page+2});">${page+2}</button>
            </li>
          </c:if>
          <c:if test="${pageNumber == 7}">
            <li class="page-item">
              <button class="page-link" onclick="setPageToView(${page+3});">${page+3}</button>
            </li>
          </c:if>
          <c:if test="${pageNumber == 8}">
            <li class="page-item">
              <button class="page-link" onclick="setPageToView();">...</button>
            </li>
            <li class="page-item">
              <button class="page-link" onclick="setPageToView();">${totalPages}</button>
            </li>
          </c:if>
        </c:forEach>
      </c:when>
    </c:choose>
    <!-- Next -->
    <c:choose>
      <c:when test="${page == Math.ceil(totalPokemon/pkmnPerPage)}">
        <li class="page-item disabled">
          <button class="page-link" onclick="setPageToView(${page+1});">Next</button>
        </li>
      </c:when>
      <c:when test="${page != Math.ceil(totalPokemon/pkmnPerPage)}">
        <li class="page-item">
          <button class="page-link" onclick="setPageToView(${page+1});">Next</button>
        </li>
      </c:when>
    </c:choose>
  </ul>
</nav>
</body>
</html>