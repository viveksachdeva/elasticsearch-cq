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
<div class="facet-results" style="display: none">

</div>

<div style="border-top: 2px dotted #000000">
    <div>
        <label style="float: left">Enter Search Term : </label>

        <div style="margin-left: 10px; float: left">
            <input type="text" name="search-field" placeholder="Find Like Me..."
                   class="span2 search-query">
            <button value="Search" id="search-button" class="btn" type="submit">Search</button>
        </div>
    </div>
    <div class="search-results" style="float: left;width: 70%; clear: left">

    </div>
    <div class="search-facet-results" style="float: left;width: 25%">

    </div>
</div>
</body>
