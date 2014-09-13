package com.vivek.impl

import com.vivek.ElasticSearchOperation
import com.vivek.constants.ElasticSearchConstants
import com.vivek.utils.ClientNodeFactoryBean
import com.vivek.utils.ElasticSearchUtil
import org.apache.felix.scr.annotations.Activate
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Service
import org.apache.sling.commons.json.JSONObject
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest
import org.elasticsearch.action.admin.indices.status.IndicesStatusRequest
import org.elasticsearch.action.admin.indices.status.IndicesStatusResponse
import org.elasticsearch.action.index.IndexRequestBuilder
import org.elasticsearch.action.search.SearchRequestBuilder
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.client.Requests
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.index.query.QueryBuilder
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.indices.IndexMissingException
import org.elasticsearch.search.facet.FacetBuilders
import org.osgi.service.component.ComponentContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created with IntelliJ IDEA.
 * User: vivek
 * Date: 14/7/14
 * Time: 2:27 PM
 * To change this template use File | Settings | File Templates.
 */
@Component(label = "CQ operations for Elastic Replication",
        description = "This servlet is for interacting with ElasticSearch Server",
        metatype = true, immediate = true, enabled = true)
@Service(ElasticSearchOperation.class)
class ElasticSearchOperationImpl implements ElasticSearchOperation {
    private static final Logger LOG = LoggerFactory.getLogger(ElasticSearchOperationImpl.class);

    TransportClient client = ClientNodeFactoryBean.instance.object as TransportClient
    ElasticSearchUtil elasticSearchUtil = new ElasticSearchUtil()

    @Activate
    protected void activate(ComponentContext componentContext) {
        LOG.info("inside activate method of Elastic Operation Impl");
        elasticSearchUtil.defineSchemaForElaticSearch();
    }

    @Override
    String indexDataOnElasticServer(JSONObject jsonObject) {
        String message = "Successfully Indexed"
        try {
            def id = elasticSearchUtil.checkIfPageExistsOnElastic(jsonObject.get("docId").toString())
            if (id) {
                LOG.info("Page exists::::::::::::::::::::")
//                 Update existing document
//                client.prepareDeleteByQuery("vivek").setTypes("tags").
//                        setQuery(QueryBuilders.termQuery("pageId_sort", jsonObject.get("docId")))
//                        .execute()
//                        .actionGet();
                client.prepareIndex("vivek", "pages", id.first())
                        .setSource(jsonObject.toString())
                        .execute()
                        .actionGet();
            } else {
//                New document Added
                indexNewDocument("vivek", "pages", jsonObject)
            }
            addTagToElasticSearch(jsonObject)
        }
        catch (Exception e) {
            //If there is any exception, it stays in the replication queue
            LOG.info("Exception occured");
            e.printStackTrace(System.out)
            message = "Exception occured in indexing"
        }
        finally {
            LOG.info("Finally in elastic")
        }
        return message
    }

    private void indexNewDocument(String indexName, String typeName, JSONObject jsonObject) {
        IndexRequestBuilder requestBuilder = client.prepareIndex(indexName, typeName)
                .setSource(jsonObject.toString())
        requestBuilder.execute().get()
    }

    private void addTagToElasticSearch(JSONObject jsonObject) {
        if (jsonObject.has("pageTags")) {
            String allTags = jsonObject.get("pageTags")?.toString()
            allTags.substring(1, allTags.length() - 1).tokenize(",").each {
                JSONObject tagJSON = new JSONObject();
                QueryBuilder queryBuilder = QueryBuilders.termQuery("tagId_facet", it.trim())
                QueryBuilder queryBuilder2 = QueryBuilders.termQuery("pageId_sort", jsonObject.get("docId").toString())

                SearchRequestBuilder requestBuilder = client.prepareSearch("vivek")
                        .setTypes("tags")
                SearchResponse searchResponse = requestBuilder.setQuery(QueryBuilders.boolQuery().must(queryBuilder).must(queryBuilder2)).execute().get()
                Integer totalHits = searchResponse.hits.totalHits
                if (totalHits == 0) {
                    tagJSON.put("pageId", jsonObject.get("docId"))
                    tagJSON.put("tagId", it.trim())
                    indexNewDocument("vivek", "tags", tagJSON)
                }

            }
        }
    }
}

