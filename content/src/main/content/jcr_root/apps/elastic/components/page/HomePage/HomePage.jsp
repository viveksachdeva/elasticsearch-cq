<%
%>
<%@include file="/apps/elastic/components/page/global.jspx" %>
<%
%>
<%@page session="false" %>
<%
%>
<%@include file="/apps/elastic/components/page/init.jsp" %>
<cq:includeClientLib categories="elastic.demo"/>

<body>
Facet Search Results:
<div class="facet-results">

</div>

<div style="border-top: 2px dotted #000000">
    <label>Enter Search Term : </label><input type="text" name="search-field" placeholder="Find Like Me..." class="span2 search-query">
    <button value="Search" id="search-button" class="btn" type="submit">Search</button>
    <div class="search-results">

    </div>
</div>
</body>