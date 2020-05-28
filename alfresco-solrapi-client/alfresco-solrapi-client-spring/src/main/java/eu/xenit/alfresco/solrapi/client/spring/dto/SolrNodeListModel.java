package eu.xenit.alfresco.solrapi.client.spring.dto;

import java.util.List;
import lombok.Data;

@Data
public class SolrNodeListModel {

    private List<SolrNodeModel> nodes;
}

