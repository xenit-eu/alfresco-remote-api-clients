package eu.xenit.alfresco.solrapi.client.spi;

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


    
    private long id;
    private String nodeRef;
    private long txnId;
    private String status;

    //private String tenant;

    private long aclId;
    private String shardPropertyValue;
}
