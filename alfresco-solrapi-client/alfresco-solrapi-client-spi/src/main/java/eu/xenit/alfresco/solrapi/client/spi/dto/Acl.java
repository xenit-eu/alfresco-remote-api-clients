package eu.xenit.alfresco.solrapi.client.spi.dto;

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
