package eu.xenit.alfresco.solrapi.client.spi.query;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import eu.xenit.alfresco.solrapi.client.spi.dto.AclChangeSet;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonInclude(Include.NON_NULL)
public class AclsQueryParameters {
    List<Long> aclChangeSetIds;
    Long fromId;
    int maxResults;

    public AclsQueryParameters withAclChangeSets(List<Long> aclChangeSetIds) {
        this.aclChangeSetIds = aclChangeSetIds;
        this.maxResults = Integer.MAX_VALUE;
        return this;
    }
}
