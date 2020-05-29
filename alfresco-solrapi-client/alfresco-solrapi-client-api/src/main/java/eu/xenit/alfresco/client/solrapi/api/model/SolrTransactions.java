package eu.xenit.alfresco.client.solrapi.api.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
public class SolrTransactions
{
    private List<SolrTransaction> transactions;

    private Long maxTxnCommitTime;
    private Long maxTxnId;
    
}
