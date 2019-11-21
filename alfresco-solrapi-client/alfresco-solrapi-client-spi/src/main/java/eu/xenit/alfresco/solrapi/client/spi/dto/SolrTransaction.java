package eu.xenit.alfresco.solrapi.client.spi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolrTransaction
{
    private long id;
    private long commitTimeMs;
    private long updates;
    private long deletes;
}
