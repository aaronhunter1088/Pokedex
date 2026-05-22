---
applyTo: 'src/main/webapp/WEB-INF/jsp/*.jsp'
description: 'Java Servlet Page Instructions'
---
Apply these instructions when editing JSP files.

- Keep existing JSP + JSTL style (`<c:choose>`, `<c:when>`, `<c:if>`, `<c:forEach>`, `<c:set>`) and avoid Java scriptlets.
- Use Jakarta taglibs at the top of JSPs when needed:
  - `<%@ taglib prefix="c" uri="jakarta.tags.core" %>`
  - `<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>`
- Use `<jsp:include page="headCommon.jsp"/>` for shared `<head>` resources and `<jsp:include .../>` for reusable
  fragments such as `mobileMenu.jsp`, `navigation.jsp`, and `evolves-how.jsp`.
- Build app-local links and static asset paths with `${pageContext.request.contextPath}` instead of hardcoded hostnames or absolute URLs.
- Keep server-rendered fragment behavior intact (for example, `pokedex.jsp` loading `evolutions.jsp`/`evolves-how.jsp` HTML via AJAX).
- Preserve existing dark mode and GIF toggle bindings (`${isDarkMode?'darkmode':'lightmode'}`, `${showGifs ? 'checked' : ''}`) across desktop and mobile markup.
- Prefer existing UI conventions: semantic `alt` text, consistent IDs used by current jQuery handlers, and matching controller view names (`index`, `pokedex`, `search`, `evolutions`, `evolves-how`).
- Keep JavaScript in-page when touching existing pages unless a dedicated shared file already exists; match current jQuery-based event and AJAX patterns.
- Images are used throughout the JSPs. Read the `images.instructions.md` file for specific instructions on how to use images in the JSP files and which images are used for what purpose.