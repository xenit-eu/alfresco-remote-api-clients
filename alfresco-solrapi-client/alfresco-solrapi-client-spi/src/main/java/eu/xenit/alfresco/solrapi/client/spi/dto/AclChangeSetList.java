package eu.xenit.alfresco.solrapi.client.spi.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AclChangeSetList
{
    private List<AclChangeSet> aclChangeSets;
    private Long maxChangeSetCommitTime;
    private Long maxChangeSetId;
}
