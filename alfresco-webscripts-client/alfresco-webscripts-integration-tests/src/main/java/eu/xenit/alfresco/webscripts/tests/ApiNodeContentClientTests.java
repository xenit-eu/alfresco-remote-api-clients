package eu.xenit.alfresco.webscripts.tests;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import eu.xenit.alfresco.webscripts.client.spi.ApiNodeContentClient;
import eu.xenit.alfresco.webscripts.client.spi.NodeLocatorClient;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import org.junit.jupiter.api.Test;

public interface ApiNodeContentClientTests {

    ApiNodeContentClient apiNodeContentClient();

    NodeLocatorClient nodeLocatorClient();

    @Test
    default void testCompanyHome() {
        String companyHomeNodeRef = nodeLocatorClient().getCompanyHome();

        assertThat(apiNodeContentClient().hasContent(companyHomeNodeRef)).isFalse();

        assertThrows(RuntimeException.class, () -> {
            try (OutputStream outputStream = new ByteArrayOutputStream()) {
                apiNodeContentClient().getContent(companyHomeNodeRef, outputStream);
            }
        });
    }

}
