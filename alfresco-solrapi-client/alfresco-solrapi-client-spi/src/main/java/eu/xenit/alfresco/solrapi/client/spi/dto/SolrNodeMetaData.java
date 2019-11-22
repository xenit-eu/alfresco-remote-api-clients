package eu.xenit.alfresco.solrapi.client.spi.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class SolrNodeMetaData
{
    private long id = -1L;
    private long aclId = -1L;
    private long txnId = -1L;

    private String nodeRef;
    private String type;

    private Map<String, Object> properties = new HashMap<>();
    private List<String> aspects = new ArrayList<>();

    private List<String> paths = new ArrayList<>();
    private List<String> ancestors = new ArrayList<>();
    private List<String> parentAssocs = new ArrayList<>();
    private long parentAssocsCrc = -1L;

    private List<String> childAssocs = new ArrayList<>();
    private List<Long> childIds = new ArrayList<>();

    private String owner;

    private String tenantDomain;

}