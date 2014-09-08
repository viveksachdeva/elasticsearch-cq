package com.vivek.replication

import com.day.cq.contentsync.handler.util.RequestResponseFactory
import com.day.cq.dam.api.Asset
import com.day.cq.replication.ReplicationAction
import com.day.cq.wcm.api.Page
import com.vivek.ElasticSearchOperation
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Service
import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.api.resource.ResourceResolverFactory
import org.apache.sling.api.resource.ValueMap
import org.apache.sling.commons.json.JSONObject
import org.apache.sling.engine.SlingRequestProcessor
import org.apache.sling.jcr.api.SlingRepository
import org.osgi.service.event.Event
import org.osgi.service.event.EventHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.jcr.Session
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
* Created with IntelliJ IDEA.
* User: vivek
* Date: 21/7/14
* Time: 8:09 PM
* To change this template use File | Settings | File Templates.
*/
@Component(metatype = true, immediate = true)
@Service
@Property(name = "event.topics", value = [ReplicationAction.EVENT_TOPIC])
class ReplicationEventHandler implements EventHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ReplicationEventHandler.class);

    @Reference
    SlingRepository repository;

    @Reference
    ResourceResolverFactory resourceResolverFactory

    @Reference
    ElasticSearchOperation elasticSearchOperation

    @org.apache.felix.scr.annotations.Reference
    RequestResponseFactory requestResponseFactory;

    @org.apache.felix.scr.annotations.Reference
    SlingRequestProcessor requestProcessor;

    Session session;

    @Override
    void handleEvent(Event event) {
//        ReplicationAction action = ReplicationAction.fromEvent(event);
//        String resourcePath = action.path
//        LOG.info("Activation has been triggered" + action.path)
//        HashMap map = new HashMap();
//        try {
//            session = repository.loginAdministrative(null);
//            map.put("user.jcr.session", session);
//            ResourceResolver resourceResolver = resourceResolverFactory.getResourceResolver(map);
//            Resource resource = resourceResolver.resolve(resourcePath);
//            ValueMap properties = resource.adaptTo(ValueMap.class);
//            String primaryType = (String) properties.get("jcr:primaryType");
//            if (primaryType.equalsIgnoreCase("cq:Page")) {
//                String jsonData = generateJSONDataForElastic(resourceResolver, "${resourcePath}.elas.json");
//                elasticSearchOperation.indexDataOnElasticServer(new JSONObject(jsonData))
//            }
//
//            else if (primaryType.equalsIgnoreCase("dam:Asset")) {
//                String jsonData = generateJSONDataForElastic(resourceResolver, "${resourcePath}.elas.json");
//                elasticSearchOperation.indexDataOnElasticServer(new JSONObject(jsonData))
//            }
//        }
//        catch (Exception e) {
//            e.printStackTrace(System.out)
//        }
//        finally {
//            session?.logout()
//        }
    }

    String generateJSONDataForElastic(ResourceResolver resourceResolver, String elasticDataURL) throws ServletException, IOException {
        HttpServletRequest request = requestResponseFactory.createRequest("GET", elasticDataURL);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        HttpServletResponse response = requestResponseFactory.createResponse(out);
        requestProcessor.processRequest(request, response, resourceResolver);
        return new String(out.toByteArray(), "UTF-8");
    }
}
