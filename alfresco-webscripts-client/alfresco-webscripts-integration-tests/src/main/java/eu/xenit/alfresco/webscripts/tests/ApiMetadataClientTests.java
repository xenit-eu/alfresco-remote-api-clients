package eu.xenit.alfresco.webscripts.tests;

import static org.assertj.core.api.Assertions.assertThat;

import eu.xenit.alfresco.webscripts.client.spi.ApiMetadataClient;
import eu.xenit.alfresco.webscripts.client.spi.ApiMetadataClient.Metadata;
import org.junit.jupiter.api.Test;

public interface ApiMetadataClientTests {

    ApiMetadataClient apiMetadataClient();

    @Test
    default void testGetMetadata() {
        Metadata metadata = apiMetadataClient().get("workspace://SpacesStore/01cfb3cc-a84b-46c1-a92d-b1ad6af9fb57");

        assertThat(metadata).isNotNull();
        assertThat(metadata.getType()).isEqualTo("{http://www.alfresco.org/model/content/1.0}folder");

    }
}
