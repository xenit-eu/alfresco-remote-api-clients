package eu.xenit.alfresco.webscripts.tests;

import eu.xenit.alfresco.webscripts.client.spi.NodeLocatorClient;
import eu.xenit.alfresco.webscripts.client.spi.NodeLocatorClient.Locator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public interface NodeLocatorClientTests {

    NodeLocatorClient nodeLocatorClient();

    @Test
    default void testGetCompanyHome() {
        NodeLocatorClient nodeLocator = nodeLocatorClient();
        String nodeReference = nodeLocator.get(Locator.COMPANY_HOME);

        assertThat(nodeReference)
                .isNotNull()
                .startsWith("workspace://SpacesStore/")
                .isEqualTo(nodeLocator.getCompanyHome());
    }

    @Test
    default void testInvalidNodeLocator() {
        NodeLocatorClient nodeLocator = nodeLocatorClient();
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> nodeLocator.get("foobar"));
    }
}
