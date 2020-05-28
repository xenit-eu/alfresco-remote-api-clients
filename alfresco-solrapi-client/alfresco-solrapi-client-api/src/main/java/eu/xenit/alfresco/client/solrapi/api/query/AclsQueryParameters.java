package eu.xenit.alfresco.client.solrapi.api.query;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
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
        return this;
    }
}
