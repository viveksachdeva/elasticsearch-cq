package com.vivek

import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.commons.json.JSONObject

import javax.servlet.ServletException

/**
 * Created with IntelliJ IDEA.
 * User: vivek
 * Date: 14/7/14
 * Time: 2:24 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ElasticSearchOperation {
    String indexDataOnElasticServer(JSONObject jsonObject);
}