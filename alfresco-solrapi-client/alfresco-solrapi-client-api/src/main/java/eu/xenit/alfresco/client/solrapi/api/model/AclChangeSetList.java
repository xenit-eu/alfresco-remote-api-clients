package eu.xenit.alfresco.client.solrapi.api.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
public class AclChangeSetList
{
    private List<AclChangeSet> aclChangeSets;
    private Long maxChangeSetCommitTime;
    private Long maxChangeSetId;
}
