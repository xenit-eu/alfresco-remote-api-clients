package eu.xenit.alfresco.client.solrapi.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NodePathInfo {

    private String apath;
    private String path;
    private String qname;

}
