package com.vivek.servlet

import com.day.cq.dam.api.Asset
import com.vivek.ElasticSearchFieldMap
import com.vivek.utils.ElasticSearchUtil
import org.apache.felix.scr.annotations.*
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.api.resource.ResourceUtil
import org.apache.sling.api.servlets.SlingAllMethodsServlet
import org.apache.sling.commons.json.JSONObject
import org.osgi.service.component.ComponentContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.servlet.Servlet

/**
 * Created with IntelliJ IDEA.
 * User: vivek
 * Date: 23/7/14
 * Time: 6:34 PM
 * To change this template use File | Settings | File Templates.
 */
@Component(label = "Elastic DAM Asset servlet", description = "This servlet generates for DAM assets",
        enabled = true, immediate = true, metatype = true)
@Service(value = Servlet.class)
@org.apache.felix.scr.annotations.Properties([
@Property(name = "service.description", value = "This servlet posts data to Elastic server for DAM assets"),
@Property(name = "service.vendor", value = "Intelligrape"),
@Property(name = "sling.servlet.resourceTypes", value = "dam:Asset", propertyPrivate = true),
@Property(name = "sling.servlet.extensions", value = "xml", propertyPrivate = true),
@Property(name = "sling.servlet.selectors", value = "elas", propertyPrivate = true),
@Property(name = "sling.servlet.methods", value = "GET", propertyPrivate = true)
])
class ElasticSearchDAMServlet extends SlingAllMethodsServlet {
    @org.apache.felix.scr.annotations.Reference
    ElasticSearchFieldMap elasticSearchFieldMap;

    /**
     * Log variable for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(ElasticSearchDAMServlet.class);

    /**
     * Map which holds the mappings for CQ and Elastic fields for an asset.
     */
    private HashMap<String, String> map = new HashMap<String, String>();

    /**
     * Activate method for this component.
     * @param componentContext
     */
    @Activate
    protected void activate(ComponentContext componentContext) {
        LOG.debug("inside activate method of DAM servlet");
    }

    /**
     * Modified method for this component.
     * @param componentContext
     */
    @Modified
    protected void modified(ComponentContext componentContext) {
        LOG.debug("Values have been modified");
        activate(componentContext);
    }

    /**
     * doGet is the method which gets called for a GET request.
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        Resource resource = request.getResource();
        Asset asset = resource.adaptTo(Asset.class);
        ResourceResolver resourceResolver;
        try {
            resourceResolver = request.getResourceResolver();
            if (resource && !ResourceUtil.isNonExistingResource(resource)) {
                map = elasticSearchFieldMap.mapElasticAssetFields();
                String jsonData = generateJSONForAsset(asset, map);
                response.getOutputStream().write(jsonData.getBytes());
            }
        } finally {
            resourceResolver?.close();
        }
    }

    /**
     * It extracts the metadata properties for  an asset.
     * @param asset
     * @param fieldMap
     * @return
     */
    private String generateJSONForAsset(Asset asset, HashMap fieldMap) {
        HashMap assetMetadata = (HashMap) asset.getMetadata();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("docType", "page")
        jsonObject.put("docId", "${asset.path}")
        return ElasticSearchUtil.parseFieldMap(assetMetadata, fieldMap, jsonObject);
    }
}
