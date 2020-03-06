package eu.xenit.alfresco.webscripts.client.spring;

import eu.xenit.alfresco.webscripts.client.spi.ApiNodeContentClient;
import eu.xenit.alfresco.webscripts.client.spi.NodeLocatorClient;
import eu.xenit.alfresco.webscripts.tests.ApiNodeContentClientTests;
import org.junit.jupiter.api.Nested;

public class ApiNodeContentClientIntegrationTests extends WebscriptsSpringClientTestsBase {

    @Nested
    class WithProvidedRestTemplate implements ApiNodeContentClientTests {

        @Override
        public ApiNodeContentClient apiNodeContentClient() {
            return new ApiNodeContentSpringClient(alfrescoProperties());
        }

        @Override
        public NodeLocatorClient nodeLocatorClient() {
            return new NodeLocatorSpringClient(alfrescoProperties());
        }
    }

    @Nested
    class WithCustomRestTemplate implements ApiNodeContentClientTests {

        @Override
        public ApiNodeContentClient apiNodeContentClient() {
            return new ApiNodeContentSpringClient(alfrescoProperties(), restTemplateBuilder().build());
        }

        @Override
        public NodeLocatorClient nodeLocatorClient() {
            return new NodeLocatorSpringClient(alfrescoProperties(), restTemplateBuilder().build());
        }
    }
}
