package eu.xenit.alfresco.client.solrapi.api.model;

import lombok.Value;

@Value
public class SolrTransaction
{
    private long id;
    private long commitTimeMs;
    private long updates;
    private long deletes;
}
