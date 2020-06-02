package eu.xenit.alfresco.solrapi.client.spring.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolrTransactionsModel
{
    private List<SolrTransactionModel> transactions;

    private Long maxTxnCommitTime;
    private Long maxTxnId;
    
}
