package eu.xenit.alfresco.webscripts.tests;

import eu.xenit.alfresco.webscripts.client.spi.NodeLocatorClient;
import eu.xenit.alfresco.webscripts.client.spi.NodeLocatorClient.Locator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public interface NodeLocatorClientTests {

    NodeLocatorClient nodeLocatorClient();

    @Test
    default void testGetCompanyHome() {
        String nodeReference = nodeLocatorClient().get(Locator.COMPANY_HOME);
        assertThat(nodeReference).isNotNull().startsWith("workspace://SpacesStore/");
    }
}
