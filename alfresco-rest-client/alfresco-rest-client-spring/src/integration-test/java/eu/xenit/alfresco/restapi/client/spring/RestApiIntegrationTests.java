package eu.xenit.alfresco.restapi.client.spring;

public class RestApiIntegrationTests {

    protected AlfrescoRestProperties alfrescoRestProperties() {
        return AlfrescoRestProperties.builder()
                .host(System.getProperty("alfresco.host", "localhost"))
                .port(Integer.parseInt(System.getProperty("alfresco.tcp.8080", "8080")))
                .build();
    }

}
