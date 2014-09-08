package com.vivek.utils

import com.vivek.constants.ElasticSearchConstants
import org.apache.commons.lang.StringEscapeUtils
import org.apache.sling.api.resource.ValueMap
import org.apache.sling.commons.json.JSONObject
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest
import org.elasticsearch.action.admin.indices.status.IndicesStatusRequest
import org.elasticsearch.action.admin.indices.status.IndicesStatusResponse
import org.elasticsearch.action.search.SearchRequestBuilder
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.client.Requests
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.index.query.QueryBuilder
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.indices.IndexMissingException

/**
 * Created with IntelliJ IDEA.
 * User: vivek
 * Date: 15/7/14
 * Time: 9:26 PM
 * To change this template use File | Settings | File Templates.
 */
class ElasticSearchUtil {

    public static String parseFieldMap(ValueMap valueMap, HashMap fieldMap, JSONObject jsonObject) {
        fieldMap.keySet().each { String propName ->
            String type = fieldMap.get(propName).toString();
            if (valueMap.get(propName) instanceof Object[]) {
                Object[] arr = (Object[]) valueMap.get(propName);
                List valueList = []
                for (Object value : arr) {
                    if (value) {
                        value = stripHTML(value.toString());
                        value = StringEscapeUtils.escapeXml(value.toString());
                        valueList << value
                    }
                }
                generateJSONData(jsonObject, type, valueList)
            } else {
                String value = String.valueOf(valueMap.get(propName));
                if (value && !value.equalsIgnoreCase("null")) {
                    value = stripHTML(value.toString());
                    value = StringEscapeUtils.escapeXml(value.toString());
                    generateJSONData(jsonObject, type, value);
                }
            }
        }

        return jsonObject.toString();
    }

    public static String parseFieldMap(HashMap assetMetadata, HashMap fieldMap, JSONObject jsonObject) {
        fieldMap.keySet().each { String propName ->
            String type = fieldMap.get(propName).toString();
            if (assetMetadata.get(propName) instanceof Object[]) {
                Object[] arr = (Object[]) assetMetadata.get(propName);
                List valueList = []
                for (Object value : arr) {
                    if (value) {
                        value = ElasticSearchUtil.stripHTML(value.toString());
                        value = StringEscapeUtils.escapeXml(value.toString());
                        valueList << value
                    }
                }
                generateJSONData(jsonObject, type, valueList)
            } else {
                String value = String.valueOf(assetMetadata.get(propName));
                if (value && !value.equalsIgnoreCase("null")) {
                    value = ElasticSearchUtil.stripHTML(value.toString());
                    value = StringEscapeUtils.escapeXml(value.toString());
                    generateJSONData(jsonObject, type, value);
                }
            }
        }
        return jsonObject.toString();

    }

    public static generateJSONData(JSONObject jsonObject, String type, Object value) {
        jsonObject.put("${type}", "${value.toString()}")
    }

    public static String stripHTML(String text) {
        return text.replaceAll("\\<.*?\\>", "");
    }

    String defineSchemaForElaticSearch() {
        //Mapping required for schema
        Map map = ["pages": ElasticSearchConstants.PAGE_SCHEMA_SETTING, "tags": ElasticSearchConstants.TAG_SCHEMA_SETTING]

        TransportClient gClient = ClientNodeFactoryBean.instance.object as TransportClient
        CreateIndexRequest request = Requests.createIndexRequest("vivek")
        map?.eachWithIndex { key, value, index ->
            String mappingFile = "{${key}:${value}}"
            request.mapping(key, mappingFile)
        }
        if (!checkIfIndexExists("vivek")) {
            gClient.admin().indices().create(request).actionGet()
        }
    }

    //Check if page exists on the server
    def checkIfPageExistsOnElastic(String documentId) {
        TransportClient client = ClientNodeFactoryBean.instance.object as TransportClient
            QueryBuilder queryBuilder = QueryBuilders.termQuery("docId_sort", documentId)
            SearchRequestBuilder requestBuilder = client.prepareSearch("vivek")
                    .setTypes("pages")
            SearchResponse searchResponse = requestBuilder.setQuery(queryBuilder).execute().get()
            return searchResponse?.hits?.hits?.id
    }

    private boolean checkIfIndexExists(String indexName) {
        IndicesStatusResponse statusResponse
        Boolean retValue = false
        TransportClient gClient = ClientNodeFactoryBean.instance.object as TransportClient
        try {
            statusResponse = gClient.admin().indices().status(new IndicesStatusRequest()).actionGet()
            retValue = statusResponse.indices.containsKey(indexName)
        } catch (IndexMissingException ime) {
            ime.printStackTrace(System.out)
        }

        return retValue
    }


}
