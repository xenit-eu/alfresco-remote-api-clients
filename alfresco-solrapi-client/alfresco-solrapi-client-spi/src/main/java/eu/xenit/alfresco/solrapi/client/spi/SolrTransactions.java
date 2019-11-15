package eu.xenit.alfresco.solrapi.client.spi;

import java.util.List;
import lombok.Data;

@Data
public class SolrTransactions
{
    private List<SolrTransaction> transactions;
    private Long maxTxnCommitTime;
    private Long maxTxnId;
    
}
