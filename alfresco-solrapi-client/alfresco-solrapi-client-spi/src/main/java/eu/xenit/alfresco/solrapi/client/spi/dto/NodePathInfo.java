package eu.xenit.alfresco.solrapi.client.spi.dto;

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
