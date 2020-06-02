package eu.xenit.alfresco.solrapi.client.spring.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AclChangeSetListModel
{
    private List<AclChangeSetModel> aclChangeSets;
    private Long maxChangeSetCommitTime;
    private Long maxChangeSetId;
}
