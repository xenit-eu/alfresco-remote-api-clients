package eu.xenit.alfresco.solrapi.client.spi;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AclReaders
{
    @EqualsAndHashCode.Include
    private final long id;

    private final List<String> readers;
    private final List<String> denied;
    private final long aclChangeSetId;
    private final String tenantDomain;

}
