package eu.xenit.alfresco.webscripts.client.spring;


import eu.xenit.alfresco.webscripts.client.spi.ApiMetadataClient;
import eu.xenit.alfresco.webscripts.tests.ApiMetadataClientTests;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.DefaultUriTemplateHandler;

class ApiMetadataClientIntegrationTests implements ApiMetadataClientTests {

    RestTemplateBuilder restTemplateBuilder() {
        return new RestTemplateBuilder()
                .basicAuthentication("admin", "admin")
                .rootUri("http://" + alfrescoHost() + ":" + alfrescoPort() + "/alfresco/service");
    }

    private static String alfrescoHost() {
        return System.getProperty("alfresco.host", "localhost");
    }

    private static Integer alfrescoPort() {
        return Integer.parseInt(System.getProperty("alfresco.tcp.8080", "8080"));
    }


    @Override
    public ApiMetadataClient apiMetadataClient() {
        return new ApiMetadataSpringClient(restTemplateBuilder());
    }
}