package eu.xenit.alfresco.solrapi.client.tests.spi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
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
    }
    
    private long id;
    private String nodeRef;
    private long txnId;
    private String status;
    private String tenant;
    private long aclId;
    private String shardPropertyValue;
}
