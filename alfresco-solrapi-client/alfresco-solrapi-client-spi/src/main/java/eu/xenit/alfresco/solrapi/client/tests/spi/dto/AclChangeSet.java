package eu.xenit.alfresco.solrapi.client.tests.spi.dto;

import lombok.Data;

@Data
public class AclChangeSet
{
    private final long id;
    private final long commitTimeMs;
    private final int aclCount;
}
