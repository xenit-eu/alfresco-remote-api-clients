package eu.xenit.alfresco.solrapi.client.spring.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class SolrApiProperties {

    String url = "https://localhost:8443/alfresco/";

    private HttpProperties http = new HttpProperties();

}
