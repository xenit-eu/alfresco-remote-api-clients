package eu.xenit.alfresco.webscripts.client.spring.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class AlfrescoProperties {

    private String url = "http://localhost:8080/alfresco/";

    private String user = "admin";
    private String password = "admin";

    private HttpProperties http = new HttpProperties();

}
