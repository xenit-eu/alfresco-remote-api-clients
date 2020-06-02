package eu.xenit.alfresco.client.solrapi.api.model;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class SolrNode
{

    public enum SolrApiNodeStatus
    {
        UPDATED,
        DELETED,
        UNKNOWN,
        NON_SHARD_DELETED,
        NON_SHARD_UPDATED
    }

    public SolrNode (long nodeId, String nodeRef, long txnId, String status, long aclId)
    {
        this.id = nodeId;
        this.nodeRef = nodeRef;
        this.txnId = txnId;
        this.status = status;
        this.aclId = aclId;

        // default value for tenant is NOT null, but an empty String
        this.tenant = "";
        this.shardPropertyValue = null;
    }
    
    private long id;
    private String nodeRef;
    private long txnId;
    private String status;
    private String tenant;
    private long aclId;
    private String shardPropertyValue;
}
