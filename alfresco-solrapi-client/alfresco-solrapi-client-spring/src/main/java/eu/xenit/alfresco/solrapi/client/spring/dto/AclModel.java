package eu.xenit.alfresco.solrapi.client.spring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AclModel
{
    private long aclChangeSetId;
    private long id;
    private long inheritedId;
}
