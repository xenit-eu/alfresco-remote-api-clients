package eu.xenit.alfresco.solrapi.client.spi.dto;

import java.util.List;
import lombok.Data;

@Data
public class AclChangeSets
{
    private final List<AclChangeSet> aclChangeSets;
    private final Long maxChangeSetCommitTime;
    private final Long maxChangeSetId;
    
}
