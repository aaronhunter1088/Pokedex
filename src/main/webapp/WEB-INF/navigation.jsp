<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Navigation</title>
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
                                  <button class="page-link" onclick="setPageToView(${page-3});">...</button>
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
                            <c:if test="${pageNumber == 5 && ((page+1) <= totalPages)}">
                                <li class="page-item">
                                  <button class="page-link" onclick="setPageToView(${page+1});">${page+1}</button>
                                </li>
                            </c:if>
                            <c:if test="${pageNumber == 6 && ((page+2) <= totalPages)}">
                                <li class="page-item">
                                  <button class="page-link" onclick="setPageToView(${page+2});">${page+2}</button>
                                </li>
                            </c:if>
                            <c:if test="${pageNumber == 7 && ((page+3) <= totalPages)}">
                                <li class="page-item">
                                  <button class="page-link" onclick="setPageToView(${page+3});">${page+3}</button>
                                </li>
                            </c:if>
                            <c:if test="${pageNumber == 8 && ((page+4) <= totalPages)}">
                                <li class="page-item">
                                  <button class="page-link" onclick="setPageToView(${page+4});">...</button>
                                </li>
                                <li class="page-item">
                                  <button class="page-link" onclick="setPageToView(${totalPages});">${totalPages}</button>
                                </li>
                            </c:if>
                          </c:forEach>
                    </c:when>
                </c:choose>
                <!-- Next -->
                <c:choose>
                    <c:when test="${page == totalPages}">
                        <li class="page-item disabled">
                          <button class="page-link" onclick="setPageToView(${page+1});">Next</button>
                        </li>
                    </c:when>
                    <c:when test="${page != totalPages}">
                        <li class="page-item">
                          <button class="page-link" onclick="setPageToView(${page+1});">Next</button>
                        </li>
                    </c:when>
                </c:choose>
              </ul>
        </nav>
    </body>
</html>