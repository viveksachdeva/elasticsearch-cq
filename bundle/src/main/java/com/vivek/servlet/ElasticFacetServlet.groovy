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
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.index.query.FilterBuilders
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
        paths = ["/bin/generateFacet"],
        generateComponent = false
)
@Component(description = "Facet Servlet", metatype = true, immediate = true, enabled = true)
@Properties([
@Property(name = "service.vendor", value = "Time Warner Cable"),
@Property(name = "service.description", value = "Processes and stores FAQ feedback posts")
])
class ElasticFacetServlet extends SlingAllMethodsServlet {
    private static final Logger LOG = LoggerFactory.getLogger(ElasticFacetServlet.class);

    TransportClient client = ClientNodeFactoryBean.instance.object as TransportClient

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        response.setContentType("application/json")
        List filteredPages = request.getParameter("filteredPages")?.tokenize(",")
        response.getWriter().write(generateFacet("tagId_facet", request.getParameter("showAll") as Boolean, filteredPages))
    }

    String generateFacet(String fieldName, Boolean showAll = false, List filteredPages) {
        SearchRequestBuilder requestBuilder = client.prepareSearch("vivek")
                .setTypes("tags").setSize(20)
        if (!showAll){
//            requestBuilder.setQuery(QueryBuilders.filteredQuery(QueryBuilders.matchAllQuery(), FilterBuilders.termFilter("pageId_sort", filteredPages ?: "")))
            requestBuilder.setQuery(QueryBuilders.termsQuery("pageId_sort", filteredPages?:"").minimumShouldMatch("1"))
            LOG.info("::::facet query::${QueryBuilders.termsQuery("pageId_sort", filteredPages?:"").minimumShouldMatch("1")}")
        }
        String facetResults = requestBuilder.addFacet(FacetBuilders.termsFacet("tagFacet").field(fieldName).allTerms(true)).execute().get().toString()
        return facetResults
    }
}

