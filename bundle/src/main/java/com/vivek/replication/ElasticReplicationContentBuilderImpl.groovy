package com.vivek.replication

import com.day.cq.contentsync.handler.util.RequestResponseFactory
import com.day.cq.dam.api.Asset
import com.day.cq.replication.*
import com.day.cq.wcm.api.Page
import com.vivek.ElasticSearchOperation
import org.apache.felix.scr.annotations.*
import org.apache.sling.api.resource.*
import org.apache.sling.commons.json.JSONObject
import org.apache.sling.engine.SlingRequestProcessor
import org.osgi.service.component.ComponentContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.jcr.Session
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created with IntelliJ IDEA.
 * User: vivek
 * Date: 14/7/14
 * Time: 5:17 PM
 * To change this template use File | Settings | File Templates.
 */
@Component(label = "Elastic Replication Content Builder", description = "This servlet replicates data to Elastic", immediate = true, enabled = true, metatype = true)
@Service(ContentBuilder.class)
@Property(name = "name", value = "ElasticReplication", propertyPrivate = true)
class ElasticReplicationContentBuilderImpl implements ContentBuilder {
    public static final String name = "ElasticReplication";
    /**
     * This serves as the title of the serialization type.
     */
    public static final String title = "Elastic Replication Content Builder";

    /**
     * Log variable for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(ElasticReplicationContentBuilderImpl.class);

    /**
     * It contains the reference for ResourceResolverFactory service.
     */
    @org.apache.felix.scr.annotations.Reference
    ResourceResolverFactory resourceResolverFactory;

    @org.apache.felix.scr.annotations.Reference
    ElasticSearchOperation elasticSearchOperation

    @org.apache.felix.scr.annotations.Reference
    RequestResponseFactory requestResponseFactory;

    @org.apache.felix.scr.annotations.Reference
    SlingRequestProcessor requestProcessor;

    /**
     * Activate method of this component.
     *
     * @param componentContext
     */
    @Activate
    protected void activate(ComponentContext componentContext) {
        LOG.info("Activating Content Builder")
    }

    /**
     * Modified method of this service.
     *
     * @param componentContext
     */
    @Modified
    protected void modified(ComponentContext componentContext) {
        activate(componentContext);
    }

    /**
     * @param session
     * @param action
     * @param replicationContentFactory
     * @param stringObjectMap
     * @return
     * @throws ReplicationException
     */
    @Override
    public ReplicationContent create(Session session, ReplicationAction action, ReplicationContentFactory replicationContentFactory, Map<String, Object> stringObjectMap) throws ReplicationException {
        return null;
    }

    /**
     * It caters for any move operation on the content.
     *
     * @param resourceResolver
     * @param currentResUri
     * @param elasticUri
     * @param operation
     * @param factory
     * @return ReplicationContent
     * @throws ServletException
     * @throws IOException
     */
    private ReplicationContent modificationEvents(ResourceResolver resourceResolver, String currentResUri, String elasticUri, ReplicationActionType operation, ReplicationContentFactory factory) throws ServletException, IOException {
        if (elasticUri) {
            if (operation.equals(ReplicationActionType.ACTIVATE)) {
                return pageActivationEvent(resourceResolver, currentResUri, factory);
            }
        }
        LOG.error("Hitting blank data in modification event")
        return blankData();
    }

    /**
     * It caters for any activation events on content.
     *
     * @param resourceResolver
     * @param currentUri
     * @param factory
     * @return
     * @throws ServletException
     * @throws IOException
     */
    private ReplicationContent pageActivationEvent(ResourceResolver resourceResolver, String currentUri, ReplicationContentFactory factory) throws ServletException, IOException {
        String jsonData = generateJSONDataForElastic(resourceResolver, "${currentUri}.elas.json");
        String retVal = elasticSearchOperation.indexDataOnElasticServer(new JSONObject(jsonData))
        LOG.info(":::::returned from indexing:::${retVal}")
        String outputData = "{\"message\":\"${retVal}\", \"resourceUri\": \"${currentUri}\"}"
        return (!retVal.contains("Exception")) ? createReplicationData(factory, outputData) : blankData();
    }

    String generateJSONDataForElastic(ResourceResolver resourceResolver, String elasticDataURL) throws ServletException, IOException {
        HttpServletRequest request = requestResponseFactory.createRequest("GET", elasticDataURL);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        HttpServletResponse response = requestResponseFactory.createResponse(out);
        requestProcessor.processRequest(request, response, resourceResolver);
        return new String(out.toByteArray(), "UTF-8");
    }

    /**
     * It creates a temporary file with the content passed in the method.
     *
     * @param factory
     * @param jsonData
     * @return ReplicationContent
     * @throws IOException
     */
    private ReplicationContent createReplicationData(ReplicationContentFactory factory, String jsonData) throws IOException {

        File tempFile;
        BufferedWriter out;
        try {
            tempFile = File.createTempFile("jsonData", ".tmp");
            out = new BufferedWriter(new FileWriter(tempFile));
            out.write(jsonData);
            out.close();
            return factory.create("application/json", tempFile, true);
        } finally {
            out?.close()
            tempFile?.delete()
        }
    }

    /**
     * It creates a blank xml.
     *
     * @param factory
     * @return
     */
    private ReplicationContent blankData() {
        return ReplicationContent.VOID;
    }

    @Override
    ReplicationContent create(Session session, ReplicationAction action, ReplicationContentFactory factory) throws ReplicationException {
        String elasticUri = action.config.getTransportURI();
        ResourceResolver resourceResolver;
        String resourcePath = action.getPath();
        try {
            HashMap map = new HashMap();
            map.put("user.jcr.session", session);
            resourceResolver = resourceResolverFactory.getResourceResolver(map);
            Resource resource = resourceResolver.resolve(resourcePath);
            ValueMap properties = resource.adaptTo(ValueMap.class);
            String primaryType = (String) properties.get("jcr:primaryType");
            LOG.info("Primary type is {}", primaryType);
            if (primaryType.equalsIgnoreCase("cq:Page")) {
                return modificationEvents(resourceResolver, resourcePath, elasticUri, action.getType(), factory);
            } else {
                //Comment this else block to get error and check replication queue in case any tag is asset is published along with page: Just for testing
                String outputData = "{\"message\":\"No Page\"}"
                return createReplicationData(factory, outputData)
            }
        } catch (LoginException e) {
            LOG.error("Exception occured while getting the resource resolver ");
            e.printStackTrace(System.out);
        } catch (ServletException e) {
            LOG.error("Servlet exception occured");
            e.printStackTrace(System.out);
        } catch (IOException e) {
            LOG.error("Exception occured while doing I/O operations");
            e.printStackTrace(System.out);
        } finally {
            resourceResolver?.close();
            LOG.debug("finally executed successfully");
        }
        LOG.info("Returning blank data from create")
        return blankData();
    }

    @Override
    String getName() {
        return name
    }

    @Override
    String getTitle() {
        return title
    }
}

