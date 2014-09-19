package com.vivek

import com.day.cq.replication.AgentConfig
import com.day.cq.replication.ReplicationAction
import com.day.cq.replication.ReplicationActionType
import com.day.cq.replication.ReplicationException
import com.day.cq.replication.ReplicationResult
import com.day.cq.replication.ReplicationTransaction
import com.day.cq.replication.Replicator
import com.day.cq.replication.TransportContext
import com.day.cq.replication.TransportHandler
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Service
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.api.resource.ResourceResolverFactory
import org.apache.sling.commons.json.JSONObject

import javax.jcr.Session

/**
 * Created with IntelliJ IDEA.
 * User: vivek
 * Date: 16/9/14
 * Time: 12:40 PM
 * To change this template use File | Settings | File Templates.
 */
@Service(TransportHandler.class)
@Component(label = "My Transport Handler", immediate = true, enabled = true)
class CustomTransportHandler implements TransportHandler {

    @org.apache.felix.scr.annotations.Reference
    Replicator replicator

    @org.apache.felix.scr.annotations.Reference
    ResourceResolverFactory resourceResolverFactory

    @Override
    boolean canHandle(AgentConfig agentConfig) {
        agentConfig.transportURI.toLowerCase().contains("elastic")
    }

    @Override
    ReplicationResult deliver(TransportContext transportContext, ReplicationTransaction replicationTransaction) throws ReplicationException {
        ResourceResolver resourceResolver
        Session session
        String replicationMessage = replicationTransaction.content.inputStream.text
        JSONObject jsonObject = new JSONObject(replicationMessage)
        if (replicationMessage?.toLowerCase()?.contains("exception")) {
            try {
                resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
                session = resourceResolver.adaptTo(Session.class);
                replicator.replicate(session, ReplicationActionType.ACTIVATE, jsonObject.get("resourceUri").toString())
            }
            catch (Exception exception) {
                exception.printStackTrace()
            }
            finally {
                session?.logout()
                resourceResolver?.close()
            }
        }
        return ReplicationResult.OK
    }
}

