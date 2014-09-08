package com.vivek.servlet

import com.day.cq.wcm.api.Page
import com.vivek.ElasticSearchFieldMap
import com.vivek.utils.ElasticSearchUtil

//import com.vivek.ElasticSearchFieldMap
import org.apache.felix.scr.annotations.*
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.apache.sling.api.resource.*
import org.apache.sling.api.servlets.SlingAllMethodsServlet
import org.apache.sling.commons.json.JSONArray
import org.apache.sling.commons.json.JSONObject
import org.osgi.service.component.ComponentContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.servlet.Servlet

@Component(label = "Elastic Page Component servlet", description = "This servlet generates JSON for CQ Pages",
        enabled = true, immediate = true, metatype = true)
@Service(value = Servlet.class)
@org.apache.felix.scr.annotations.Properties([
@Property(name = "service.description", value = "This servlet gets JSON data to be indexed"),
@Property(name = "service.vendor", value = "Intelligrape"),
@Property(name = "sling.servlet.resourceTypes", value = "cq:Page", propertyPrivate = true),
@Property(name = "sling.servlet.extensions", value = "json", propertyPrivate = true),
@Property(name = "sling.servlet.selectors", value = "elas", propertyPrivate = true),
@Property(name = "sling.servlet.methods", value = "GET", propertyPrivate = true)
])
class ElasticSearchPageServlet extends SlingAllMethodsServlet {

    /**
     * It contains the reference for a service object ResourceResolverFactory.
     */
    @org.apache.felix.scr.annotations.Reference
    ResourceResolverFactory resolverFactory;

    @org.apache.felix.scr.annotations.Reference
    ElasticSearchFieldMap elasticSearchFieldMap

    /**
     * Log variable for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(ElasticSearchPageServlet.class);

    /**
     * A constant that contains the resource type for a parsys component.
     */
    private static final String PARSYS = "foundation/components/parsys";

    /**
     * A constant that contains the resource type for a iparsys component.
     */
    private static final String IPARSYS = "foundation/components/iparsys";

    /**
     * HashMap that contains the mapping for a page.
     */
    private HashMap<String, String> pageFieldMap = new HashMap<String, String>();
    /**
     * HashMap that contains the mapping for components.
     */
    private HashMap<String, ArrayList> compFieldMap = new HashMap<String, ArrayList>();

    /**
     * Activate method for this class.
     *
     * @param componentContext
     */
    @Activate
    protected void activate(ComponentContext componentContext) {
        LOG.debug("inside activate method of Page servlet");

    }

    /**
     * Modified method for this class.
     *
     * @param componentContext
     */
    @Modified
    protected void modified(ComponentContext componentContext) {
        LOG.debug("Values have been modified");
        activate(componentContext);
    }

    /**
     * doGet is the method which gets called for a GET request.
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        Resource resource = request.getResource();
        ResourceResolver resourceResolver
        try {
            resourceResolver = request.getResourceResolver();
            if (resource && !ResourceUtil.isNonExistingResource(resource)) {
                pageFieldMap = elasticSearchFieldMap.mapElasticPageFields()
                LOG.info("Elastic field names and values for page are " + pageFieldMap)
                String jsonData = generateJSONForPage(resourceResolver, resource, pageFieldMap);
                response.outputStream.write(jsonData.bytes);
            }
        }
        catch (Exception e) {
            e.printStackTrace(System.out)
            LOG.error("Some exception occured")
        } finally {
            LOG.info("finally executed");
            resourceResolver?.close();
        }

    }

    String generateJSONForPage(ResourceResolver resourceResolver, Resource resource, HashMap pagefieldMap) {
        String jsonData
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("docType", "page")
        jsonObject.put("docId", "${resource.path}")
        if (!(resource.getPath().endsWith("/jcr:content"))) {
            String tempUrl = resource.getPath() + "/jcr:content";
            Resource tempResource = resourceResolver.getResource(tempUrl);
            ValueMap pageValueMap = tempResource.adaptTo(ValueMap.class);
            jsonData = ElasticSearchUtil.parseFieldMap(pageValueMap, pagefieldMap, jsonObject);
        }
        LOG.info("JSON generated for page is" + jsonData);
        return jsonData;
    }
}
