package eu.xenit.alfresco.client.solrapi.api.model;

import lombok.Value;

@Value
public class AclChangeSet
{
    long id;
    long commitTimeMs;
    int aclCount;
}
