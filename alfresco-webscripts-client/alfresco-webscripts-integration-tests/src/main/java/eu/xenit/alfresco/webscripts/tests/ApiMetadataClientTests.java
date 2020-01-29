package eu.xenit.alfresco.webscripts.tests;

import static org.assertj.core.api.Assertions.assertThat;

import eu.xenit.alfresco.webscripts.client.spi.ApiMetadataClient;
import eu.xenit.alfresco.webscripts.client.spi.Metadata;
import eu.xenit.alfresco.webscripts.client.spi.NodeLocatorClient;
import eu.xenit.alfresco.webscripts.client.spi.NodeLocatorClient.Locator;
import eu.xenit.testing.ditto.api.data.ContentModel;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Locale;

public interface ApiMetadataClientTests {
    ApiMetadataClient apiMetadataClient();
    NodeLocatorClient nodeLocatorClient();

    @Test
    default void testGetMetadata() {
        Map<String, String> properties = new HashMap<>();
        properties.put(ContentModel.Content.CREATOR.toString(), "System");
        properties.put(ContentModel.Content.NAME.toString(), "Company Home");
        properties.put(ContentModel.System.STORE_PROTOCOL.toString(), "workspace");
        properties.put(ContentModel.System.STORE_IDENTIFIER.toString(), "SpacesStore");
        properties.put(ContentModel.System.NODE_DBID.toString(), "13");
        properties.put(ContentModel.System.LOCALE.toString(), "en_US");
        properties.put(ContentModel.Content.MODIFIER.toString(), "System");

        String companyHomeNodeRef = nodeLocatorClient().get(Locator.COMPANY_HOME);
        Metadata metadata = apiMetadataClient().get(companyHomeNodeRef);
        final String date = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
                                            .format(LocalDateTime.now());
        assertThat(metadata).isNotNull();
        assertThat(metadata.getType()).isEqualTo(ContentModel.Content.FOLDER.toString());
        assertThat(metadata.getNodeRef()).startsWith("workspace://SpacesStore/");
        assertThat(metadata.getAspects()).contains(ContentModel.Content.AUDITABLE.toString());
        assertThat(metadata.getProperties()).isNotEmpty();
        assertThat(metadata.getProperties()).containsAllEntriesOf(properties);
        assertThat(metadata.getProperties()
                .getOrDefault(ContentModel.Content.CREATED.toString(), null)).startsWith(date);
        assertThat(metadata.getProperties()
                .getOrDefault(ContentModel.Content.MODIFIED.toString(), null)).startsWith(date);
    }
}
