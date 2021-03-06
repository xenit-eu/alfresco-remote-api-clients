package eu.xenit.alfresco.client.solrapi.api.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
public class AclReaders
{
    @EqualsAndHashCode.Include
    private long aclId;

    private List<String> readers;
    private List<String> denied;
    private long aclChangeSetId;
    private String tenantDomain;

    public AclReaders(long aclId, List<String> readers, List<String> denied, long aclChangeSetId) {
        this(aclId, readers, denied, aclChangeSetId, "");
    }
}
