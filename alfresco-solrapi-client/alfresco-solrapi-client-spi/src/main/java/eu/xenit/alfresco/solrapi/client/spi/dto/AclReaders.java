package eu.xenit.alfresco.solrapi.client.spi.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
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
        this.aclId = aclId;
        this.readers = readers;
        this.denied = denied;
        this.aclChangeSetId = aclChangeSetId;
        tenantDomain = "";
    }
}
