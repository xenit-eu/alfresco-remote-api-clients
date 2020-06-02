package eu.xenit.alfresco.client.solrapi.api.model;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@Accessors(chain = true)
public class SolrNodeMetaData
{
    private long id;
    private long aclId;
    private long txnId;

    private String nodeRef;
    private String type;

    private Map<String, Object> properties;
    private List<String> aspects;

    private List<NodePathInfo> paths;
    private List<NodeNamePaths> namePaths;
    private List<String> ancestors;
    private List<String> parentAssocs;
    private long parentAssocsCrc;

    private List<String> childAssocs;
    private List<Long> childIds;

    private String owner;

    private String tenantDomain;
}
