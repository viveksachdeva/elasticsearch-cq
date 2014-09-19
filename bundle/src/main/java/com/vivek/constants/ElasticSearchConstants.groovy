package com.vivek.constants

/**
 * Created with IntelliJ IDEA.
 * User: vivek
 * Date: 23/7/14
 * Time: 6:22 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ElasticSearchConstants {

    String PAGE_SCHEMA_SETTING = """{
   "properties": {
       "docId": {
           "type": "string",
           "fields": {
               "docId_default_analyzer": {
                   "type": "string"
               },
               "docId_sort": {
                   "type": "string",
                   "index": "not_analyzed"
               }
           }
       }
   }
}"""

    String TAG_SCHEMA_SETTING = """{
   "properties": {
       "tagId": {
           "type": "string",
           "fields": {
               "tagId_default_analyzer": {
                   "type": "string"
               },
               "tagId_facet": {
                   "type": "string",
                   "index": "not_analyzed"
               }
           }
       },

       "pageId" :  {
           "type": "string",
           "fields": {
              "pageId_sort": {
                   "type": "string",
                   "index": "not_analyzed"
               }
           }
       }
   }
}"""

    Integer TRANSPORT_PORT_1 = 9300
    Integer TRANSPORT_PORT_2 = 9301

}
