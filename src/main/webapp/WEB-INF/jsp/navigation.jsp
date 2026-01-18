<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
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
                            <button class="page-link ${isDarkMode ? 'darkmode !important' : 'lightmode !important'}" disabled>Previous</button>
                        </li>
                    </c:when>
                    <c:when test="${page != 1}">
                        <li class="page-item">
                            <button class="page-link ${isDarkMode ? 'darkmode !important' : 'lightmode !important'}" onclick="setPageToView(${page-1});">Previous</button>
                        </li>
                    </c:when>
                </c:choose>
                <!-- Pages -->
                <c:choose>
                    <c:when test="${totalPages <= 8}">
                        <!-- If total pages is 8 or less, show all pages -->
                        <c:forEach begin="1" end="${totalPages}" var="pageNumber">
                            <li class="page-item">
                              <button class="page-link ${isDarkMode ? 'darkmode !important' : 'lightmode !important'}" onclick="setPageToView(${pageNumber});">${pageNumber}</button>
                            </li>
                        </c:forEach>
                    </c:when>
                    <c:when test="${page < 6}">
                        <!-- Show first 6-8 pages, ellipsis, and last page -->
                        <c:forEach begin="1" end="${page < 6 ? 7 : 8}" var="pageNumber">
                            <c:if test="${pageNumber <= totalPages}">
                                <li class="page-item">
                                  <button class="page-link ${isDarkMode ? 'darkmode !important' : 'lightmode !important'}" onclick="setPageToView(${pageNumber});">${pageNumber}</button>
                                </li>
                            </c:if>
                        </c:forEach>
                        <c:if test="${totalPages > 8}">
                            <li class="page-item">
                              <button class="page-link ${isDarkMode ? 'darkmode !important' : 'lightmode !important'}" onclick="setPageToView(${totalPages - 1});">...</button>
                            </li>
                            <li class="page-item">
                              <button class="page-link ${isDarkMode ? 'darkmode !important' : 'lightmode !important'}" onclick="setPageToView(${totalPages});">${totalPages}</button>
                            </li>
                        </c:if>
                    </c:when>
                    <c:when test="${page >= 6 && page <= (totalPages - 5)}">
                        <!-- Show first page, ellipsis, pages around current, ellipsis, last page -->
                        <li class="page-item">
                          <button class="page-link ${isDarkMode ? 'darkmode !important' : 'lightmode !important'}" onclick="setPageToView(1);">1</button>
                        </li>
                        <li class="page-item">
                          <button class="page-link ${isDarkMode ? 'darkmode !important' : 'lightmode !important'}" onclick="setPageToView(${page-3});">...</button>
                        </li>
                        <c:forEach begin="${page-2}" end="${page+2}" var="pageNumber">
                            <c:if test="${pageNumber > 1 && pageNumber < totalPages}">
                                <li class="page-item">
                                  <button class="page-link ${isDarkMode ? 'darkmode !important' : 'lightmode !important'}" onclick="setPageToView(${pageNumber});">${pageNumber}</button>
                                </li>
                            </c:if>
                        </c:forEach>
                        <li class="page-item">
                          <button class="page-link ${isDarkMode ? 'darkmode !important' : 'lightmode !important'}" onclick="setPageToView(${page+3});">...</button>
                        </li>
                        <li class="page-item">
                          <button class="page-link ${isDarkMode ? 'darkmode !important' : 'lightmode !important'}" onclick="setPageToView(${totalPages});">${totalPages}</button>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <!-- Near the end: show first page, ellipsis, and last 6-8 pages -->
                        <li class="page-item">
                          <button class="page-link ${isDarkMode ? 'darkmode !important' : 'lightmode !important'}" onclick="setPageToView(1);">1</button>
                        </li>
                        <li class="page-item">
                          <button class="page-link ${isDarkMode ? 'darkmode !important' : 'lightmode !important'}" onclick="setPageToView(${totalPages - 7});">...</button>
                        </li>
                        <c:forEach begin="${totalPages - 6}" end="${totalPages}" var="pageNumber">
                            <li class="page-item">
                              <button class="page-link ${isDarkMode ? 'darkmode !important' : 'lightmode !important'}" onclick="setPageToView(${pageNumber});">${pageNumber}</button>
                            </li>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
                <!-- Next -->
                <c:choose>
                    <c:when test="${page == totalPages}">
                        <li class="page-item disabled">
                          <button class="page-link ${isDarkMode ? 'darkmode !important' : 'lightmode !important'}" disabled>Next</button>
                        </li>
                    </c:when>
                    <c:when test="${page != totalPages}">
                        <li class="page-item">
                          <button class="page-link ${isDarkMode ? 'darkmode !important' : 'lightmode !important'}" onclick="setPageToView(${page+1});">Next</button>
                        </li>
                    </c:when>
                </c:choose>
              </ul>
        </nav>
    </body>
</html>