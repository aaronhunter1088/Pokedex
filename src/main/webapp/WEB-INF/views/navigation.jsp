<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Navigation</title>
        <jsp:include page="headCommon.jsp"/>
        <style></style>
    </head>
    <body>
        <nav aria-label="Pokedex navigation">
            <ul class="pagination justify-content-center">
                <!-- Previous -->
                <c:choose>
                    <c:when test="${page == 1}">
                        <li class="page-item disabled">
                            <button class="page-link">Previous</button>
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
                                  <button class="page-link" onclick="setPageToView(${pageNumber+1});">...</button>
                                </li>
                                <li class="page-item">
                                  <button class="page-link" onclick="setPageToView(${totalPages});">${totalPages}</button>
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
                          <button class="page-link">Next</button>
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