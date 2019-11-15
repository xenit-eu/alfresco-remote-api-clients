package eu.xenit.alfresco.solrapi.client.spi;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class SolrNodeMetadataList {

    private List<SolrNodeMetaData> nodes = new ArrayList<>();

}
