package eu.xenit.alfresco.solrapi.client.spi;

import lombok.Data;

@Data
public class SolrTransaction
{
    private long id;
    private long commitTimeMs;
    private long updates;
    private long deletes;
}
