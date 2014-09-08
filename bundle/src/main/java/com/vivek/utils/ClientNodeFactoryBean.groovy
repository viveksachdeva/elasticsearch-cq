package com.vivek.utils

import com.vivek.constants.ElasticSearchConstants
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.ImmutableSettings
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.springframework.beans.factory.FactoryBean

@Singleton
class ClientNodeFactoryBean implements FactoryBean {
    TransportClient client

    Object getObject() {
        return createClient()
    }

    private TransportClient createClient() {
        if (!client) {
            client = new TransportClient(settingsBuilder())

            // Configure transport addresses
            client.addTransportAddress(new InetSocketTransportAddress('localhost', ElasticSearchConstants.TRANSPORT_PORT))

        }
        return client
    }

    private def settingsBuilder() {
        def transportSettings = ImmutableSettings.settingsBuilder();
        transportSettings.put("client.transport.ping_timeout", "60000")
        transportSettings.put("client.transport.ignore_cluster_name", true)
        // Use the "sniff" feature of transport client ?
        transportSettings.put("client.transport.sniff", false)
        return transportSettings
    }

    Class getObjectType() {
        return TransportClient
    }

    boolean isSingleton() {
        return true
    }
}