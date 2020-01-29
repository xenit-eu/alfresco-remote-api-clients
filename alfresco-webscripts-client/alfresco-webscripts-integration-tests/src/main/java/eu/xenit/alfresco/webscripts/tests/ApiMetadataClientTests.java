package eu.xenit.alfresco.webscripts.tests;

import static org.assertj.core.api.Assertions.assertThat;

import eu.xenit.alfresco.webscripts.client.spi.ApiMetadataClient;
import eu.xenit.alfresco.webscripts.client.spi.Metadata;
import eu.xenit.alfresco.webscripts.client.spi.NodeLocatorClient;
import eu.xenit.alfresco.webscripts.client.spi.NodeLocatorClient.Locator;
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
        properties.put("{http://www.alfresco.org/model/content/1.0}creator" , "System");
        properties.put("{http://www.alfresco.org/model/content/1.0}name" , "Company Home");
        properties.put("{http://www.alfresco.org/model/system/1.0}store-protocol" , "workspace");
        properties.put("{http://www.alfresco.org/model/system/1.0}store-identifier" , "SpacesStore");
        properties.put("{http://www.alfresco.org/model/system/1.0}node-dbid" , "13");
        properties.put("{http://www.alfresco.org/model/system/1.0}locale" , "en_US");
        properties.put("{http://www.alfresco.org/model/content/1.0}modifier" , "System");

        String companyHomeNodeRef = nodeLocatorClient().get(Locator.COMPANY_HOME);
        Metadata metadata = apiMetadataClient().get(companyHomeNodeRef);
        final String date = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
                                            .format(LocalDateTime.now());
        assertThat(metadata).isNotNull();
        assertThat(metadata.getType()).isEqualTo("{http://www.alfresco.org/model/content/1.0}folder");
        assertThat(metadata.getNodeRef()).startsWith("workspace://SpacesStore/");
        assertThat(metadata.getAspects()).contains("{http://www.alfresco.org/model/content/1.0}auditable");
        assertThat(metadata.getProperties()).isNotEmpty();
        assertThat(metadata.getProperties()).containsAllEntriesOf(properties);
        assertThat(metadata.getProperties().getOrDefault("{http://www.alfresco.org/model/content/1.0}created", null)).startsWith(date);
        assertThat(metadata.getProperties().getOrDefault("{http://www.alfresco.org/model/content/1.0}modified", null)).startsWith(date);
    }
}
