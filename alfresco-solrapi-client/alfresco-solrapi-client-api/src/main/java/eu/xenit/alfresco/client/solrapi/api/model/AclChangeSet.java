package eu.xenit.alfresco.client.solrapi.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AclChangeSet
{
    private long id;
    private long commitTimeMs;
    private int aclCount;
}
