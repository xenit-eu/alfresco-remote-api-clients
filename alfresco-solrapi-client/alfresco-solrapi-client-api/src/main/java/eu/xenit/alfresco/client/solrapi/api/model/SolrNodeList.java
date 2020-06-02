package eu.xenit.alfresco.client.solrapi.api.model;

import java.util.List;
import lombok.Data;
import lombok.Value;

@Value
public class SolrNodeList {

    private List<SolrNode> nodes;
}

