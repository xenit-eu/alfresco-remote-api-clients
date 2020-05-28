package eu.xenit.alfresco.solrapi.client.spring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NodePathInfoModel {

    private String apath;
    private String path;
    private String qname;

}
