package eu.xenit.alfresco.solrapi.client.spi.dto;

import lombok.Data;

@Data
public class AclChangeSet
{
    private final long id;
    private final long commitTimeMs;
    private final int aclCount;
}
