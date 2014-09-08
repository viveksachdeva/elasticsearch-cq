package com.vivek.impl

import com.vivek.ElasticSearchFieldMap
import org.apache.felix.scr.annotations.Activate
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Service
import org.osgi.service.component.ComponentContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
* Created with IntelliJ IDEA.
* User: vivek
* Date: 15/7/14
* Time: 8:51 PM
* To change this template use File | Settings | File Templates.
*/
@Component(label = "Elastic Search Field Map servlet", description = "This servlet contains the mapping of Elastic and CQ fields", enabled = true, immediate = true, metatype = true)
@Service(ElasticSearchFieldMap.class)
class ElasticSearchFieldMapImpl implements ElasticSearchFieldMap{
    private static final Logger LOG = LoggerFactory.getLogger(ElasticSearchFieldMapImpl.class)

    private static final String PAGE_METADATA_VALUES_TO_BE_INDEXED = "page.indexed.values";

    private static final String ASSET_METADATA_VALUES_TO_BE_INDEXED = "asset.indexed.values";

    private static final String PROP_SEARCH_ENGINE_URL = "search.engine.url";

    private String[] pageIndexedVal
    private String[] damIndexedVal

    private String searchURL;

    private Map pageFieldMap = [:]

    private Map assetFieldMap = [:]

    @Activate
    protected void activate(ComponentContext componentContext) {
        Dictionary properties = componentContext.getProperties();
        LOG.info("inside activate method of Page servlet");
        pageIndexedVal = (String[]) properties.get(PAGE_METADATA_VALUES_TO_BE_INDEXED);
        damIndexedVal = (String[]) properties.get(ASSET_METADATA_VALUES_TO_BE_INDEXED);
        searchURL=(String) properties.get(PROP_SEARCH_ENGINE_URL);
        parsePageFieldMap(pageIndexedVal);
        parseAssetFieldMap(pageIndexedVal);
    }

    private void parsePageFieldMap(String[] pageIndexedVal) {
        for (String s : pageIndexedVal) {
            String[] parts = s.split("=");
            pageFieldMap.put(parts[1], parts[0]);
        }

        LOG.info("Elastic field names and values for page are " + pageFieldMap);
    }

    private void parseAssetFieldMap(String[] damIndexedVal) {
        for (String s : damIndexedVal) {
            LOG.debug("String is" + s);
            String[] parts = s.split("=");
            assetFieldMap.put(parts[1], parts[0]);
        }
    }


    @Override
    HashMap<String, String> mapElasticPageFields() {
        return pageFieldMap
    }

    @Override
    HashMap<String, String> mapElasticAssetFields() {
        return assetFieldMap
    }
}
