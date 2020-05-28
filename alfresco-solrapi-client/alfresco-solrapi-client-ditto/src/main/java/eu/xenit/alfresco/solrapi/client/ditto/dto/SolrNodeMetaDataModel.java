package eu.xenit.alfresco.solrapi.client.ditto.dto;

import eu.xenit.alfresco.client.solrapi.api.model.NodeNamePaths;
import eu.xenit.alfresco.client.solrapi.api.model.NodePathInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.Value;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SolrNodeMetaDataModel
{
    private long id;
    private long aclId;
    private long txnId;

    private String nodeRef;
    private String type;

    private Map<String, Object> properties = new HashMap<>();
    private List<String> aspects= new ArrayList<>();

    private List<NodePathInfo> paths = new ArrayList<>();
    private List<NodeNamePaths> namePaths = new ArrayList<>();
    private List<String> ancestors = new ArrayList<>();
    private List<String> parentAssocs = new ArrayList<>();
    private long parentAssocsCrc;

    private List<String> childAssocs = new ArrayList<>();
    private List<Long> childIds = new ArrayList<>();

    private String owner;

    private String tenantDomain;
}
