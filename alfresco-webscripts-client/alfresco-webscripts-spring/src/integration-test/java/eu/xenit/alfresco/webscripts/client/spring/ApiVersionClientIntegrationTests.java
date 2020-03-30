package eu.xenit.alfresco.webscripts.client.spring;


import eu.xenit.alfresco.webscripts.client.spi.ApiVersionClient;
import eu.xenit.alfresco.webscripts.client.spi.NodeLocatorClient;
import eu.xenit.alfresco.webscripts.tests.ApiVersionClientTests;
import org.junit.jupiter.api.Nested;

class ApiVersionClientIntegrationTests extends WebscriptsSpringClientTestsBase {

    @Nested
    class WithProvidedRestTemplate implements ApiVersionClientTests {

        @Override
        public ApiVersionClient apiVersionClient() {
            return new ApiVersionSpringClient(alfrescoProperties());
        }

        @Override
        public NodeLocatorClient nodeLocatorClient() {
            return new NodeLocatorSpringClient(alfrescoProperties());
        }
    }

    @Nested
    class WithCustomRestTemplate implements ApiVersionClientTests {

        @Override
        public ApiVersionClient apiVersionClient() {
            return new ApiVersionSpringClient(alfrescoProperties(), restTemplateBuilder().build());
        }

        @Override
        public NodeLocatorClient nodeLocatorClient() {
            return new NodeLocatorSpringClient(alfrescoProperties(), restTemplateBuilder().build());
        }
    }
}