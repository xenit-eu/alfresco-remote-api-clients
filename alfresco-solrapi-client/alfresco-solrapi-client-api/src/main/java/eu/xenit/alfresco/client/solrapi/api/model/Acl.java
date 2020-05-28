package eu.xenit.alfresco.client.solrapi.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Acl
{
    private long aclChangeSetId;
    private long id;
    private long inheritedId;
}
