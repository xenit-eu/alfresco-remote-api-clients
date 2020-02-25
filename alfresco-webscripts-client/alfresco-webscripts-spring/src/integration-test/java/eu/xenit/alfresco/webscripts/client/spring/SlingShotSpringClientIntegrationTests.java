package eu.xenit.alfresco.webscripts.client.spring;

import eu.xenit.alfresco.webscripts.client.spi.NodeLocatorClient;
import eu.xenit.alfresco.webscripts.client.spi.SlingShotClient;
import eu.xenit.alfresco.webscripts.tests.SlingShotClientTests;
import org.junit.jupiter.api.Nested;

public class SlingShotSpringClientIntegrationTests extends WebscriptsSpringClientTestsBase {

    @Nested
    class WithProvidedRestTemplate implements SlingShotClientTests {

        @Override
        public SlingShotClient slingShotClient() {
            return new SlingShotSpringClient(alfrescoProperties());
        }

        @Override
        public NodeLocatorClient nodeLocatorClient() {
            return new NodeLocatorSpringClient(alfrescoProperties());
        }
    }

    @Nested
    class WithCustomRestTemplate implements SlingShotClientTests {

        @Override
        public SlingShotClient slingShotClient() {
            return new SlingShotSpringClient(alfrescoProperties(), restTemplateBuilder().build());
        }

        @Override
        public NodeLocatorClient nodeLocatorClient() {
            return new NodeLocatorSpringClient(alfrescoProperties(), restTemplateBuilder().build());
        }
    }

}
