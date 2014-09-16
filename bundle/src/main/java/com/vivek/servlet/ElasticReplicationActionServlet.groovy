// TODO : Instead of this... Implement a custom transport handler as ElasticServer sends 201 on success but CQ expects 200

package com.vivek.servlet

import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Service
import org.apache.felix.scr.annotations.sling.SlingServlet
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.api.resource.ResourceUtil
import org.apache.sling.api.resource.ValueMap
import org.apache.sling.api.servlets.SlingAllMethodsServlet
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.servlet.Servlet

/**
 * Created with IntelliJ IDEA.
 * User: vivek
 * Date: 2/9/14
 * Time: 5:15 PM
 * To change this template use File | Settings | File Templates.
 */
@Service(value = Servlet.class)
@SlingServlet(
        paths = ["/bin/elasticreplication"],
        generateComponent = false,
        methods = ["POST"]

)
@Component(description = "Elastic Replication Servlet", metatype = true, immediate = true, enabled = true)
@org.apache.felix.scr.annotations.Properties([
@Property(name = "service.vendor", value = "Intelligrape"),
@Property(name = "service.description", value = "Elastic Replication Servlet")
])
class ElasticReplicationActionServlet  extends SlingAllMethodsServlet {

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        Integer contentLength =  request.getHeader("Content-Length") as Integer
        // if (contentLength == 0){
        //     response.setStatus(400)
        // }
    }
}
