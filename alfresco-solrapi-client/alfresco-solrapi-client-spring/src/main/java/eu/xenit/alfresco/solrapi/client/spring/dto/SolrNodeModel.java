package eu.xenit.alfresco.solrapi.client.spring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolrNodeModel
{
    private long id;
    private String nodeRef;
    private long txnId;
    private String status;
    private String tenant;
    private long aclId;
    private String shardPropertyValue;
}
