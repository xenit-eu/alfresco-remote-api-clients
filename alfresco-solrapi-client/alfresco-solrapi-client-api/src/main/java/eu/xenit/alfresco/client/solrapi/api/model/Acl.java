package eu.xenit.alfresco.client.solrapi.api.model;

import lombok.Value;

@Value
public class Acl
{
    private long aclChangeSetId;
    private long id;
    private long inheritedId;
}
