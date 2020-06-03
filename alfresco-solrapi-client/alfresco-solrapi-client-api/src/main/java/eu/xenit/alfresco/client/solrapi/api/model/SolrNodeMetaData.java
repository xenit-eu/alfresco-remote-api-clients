package eu.xenit.alfresco.client.solrapi.api.model;

import java.util.List;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
@EqualsAndHashCode
public class SolrNodeMetaData {

    private final long id;
    private final long aclId;
    private final long txnId;

    private final String nodeRef;
    private final String type;

    private final Map<String, Object> properties;
    private final List<String> aspects;

    private final List<NodePathInfo> paths;
    private final List<NodeNamePaths> namePaths;
    private final List<String> ancestors;
    private final List<ChildAssociation> parentAssocs;
    private final long parentAssocsCrc;

    private final List<ChildAssociation> childAssocs;
    private final List<Long> childIds;

    private final String owner;

    private final String tenantDomain;
}
