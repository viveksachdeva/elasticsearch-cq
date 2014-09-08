package com.vivek.servlet

import com.vivek.utils.ClientNodeFactoryBean
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Properties
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Service
import org.apache.felix.scr.annotations.sling.SlingServlet
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.apache.sling.api.servlets.SlingAllMethodsServlet
import org.elasticsearch.action.search.SearchRequestBuilder
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.index.query.QueryBuilder
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.facet.FacetBuilders
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.servlet.Servlet

/**
 * Created with IntelliJ IDEA.
 * User: vivek
 * Date: 13/8/14
 * Time: 1:26 PM
 * To change this template use File | Settings | File Templates.
 */
@Service(value = Servlet.class)
@SlingServlet(
        paths = ["/bin/searchPages"],
        generateComponent = false
)
@Component(description = "Facet Servlet", metatype = true, immediate = true, enabled = true)
@Properties([
@Property(name = "service.vendor", value = "Intelligrape"),
@Property(name = "service.description", value = "Free Text Search Servlet")
])
class FreeTextSearchServlet extends SlingAllMethodsServlet {
    private static final Logger LOG = LoggerFactory.getLogger(FreeTextSearchServlet.class);

    TransportClient client = ClientNodeFactoryBean.instance.object as TransportClient

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        response.setContentType("application/json")
        response.getWriter().write(fetchMatchingPages(request.getParameter("searchTerm")))
    }

    String fetchMatchingPages(String textToSearch) {
        SearchRequestBuilder requestBuilder = client.prepareSearch("bank")
        QueryBuilder queryBuilder = QueryBuilders.matchQuery("address", textToSearch)
        SearchResponse facetResults = requestBuilder.setQuery(queryBuilder).execute().get()
        return facetResults.toString()
    }
}