package eu.xenit.alfresco.client.solrapi.api.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolrTransactions
{
    private List<SolrTransaction> transactions;

    private Long maxTxnCommitTime;
    private Long maxTxnId;
    
}
