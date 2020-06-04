package eu.xenit.alfresco.solrapi.client.spring.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class SolrNodeMetadataListModel {

    private List<SolrNodeMetadataModel> nodes = new ArrayList<>();

}
