package eu.xenit.alfresco.webscripts.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import eu.xenit.alfresco.webscripts.client.spi.ApiMetadataClient;
import eu.xenit.alfresco.webscripts.client.spi.ApiMetadataClient.Metadata;
import eu.xenit.alfresco.webscripts.client.spi.NodeLocatorClient;
import eu.xenit.testing.ditto.api.data.ContentModel;
import eu.xenit.testing.ditto.api.data.ContentModel.Application;
import eu.xenit.testing.ditto.api.data.ContentModel.Content;
import eu.xenit.testing.ditto.api.data.ContentModel.System;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;

public interface ApiMetadataClientTests {

    ApiMetadataClient apiMetadataClient();

    NodeLocatorClient nodeLocatorClient();

    @Test
    default void testGetMetadata_companyHome() {

        String companyHomeNodeRef = nodeLocatorClient().getCompanyHome();
        String companyHomeUuid = companyHomeNodeRef.substring(companyHomeNodeRef.lastIndexOf("/") + 1);

        Metadata metadata = apiMetadataClient().get(companyHomeNodeRef);

        assertThat(metadata).isNotNull();
        assertThat(metadata.getNodeRef()).startsWith("workspace://SpacesStore/");
        assertThat(metadata.getAspects())
                .contains(ContentModel.Content.AUDITABLE.toString());
        assertThat(metadata.getMimetype()).isEqualTo("application/octet-stream");
        assertThat(metadata.getType()).isEqualTo(ContentModel.Content.FOLDER.toString());

        assertThat(metadata.getProperties())
                .isNotEmpty()
                .containsOnlyKeys(
                        Content.CREATOR.toString(),
                        Content.CREATED.toString(),
                        Content.MODIFIED.toString(),
                        Content.MODIFIER.toString(),
                        Content.NAME.toString(),
                        Content.TITLE.toString(),
                        Content.DESCRIPTION.toString(),
                        Application.ICON.toString(),
                        System.NODE_UUID.toString(),
                        System.STORE_PROTOCOL.toString(),
                        System.STORE_IDENTIFIER.toString(),
                        System.NODE_DBID.toString(),
                        System.LOCALE.toString()
                )
                .contains(
                        entry(Application.ICON.toString(), "space-icon-default"),

                        entry(Content.TITLE.toString(), "Company Home"),
                        entry(Content.DESCRIPTION.toString(), "The company root space"),

                        entry(Content.CREATOR.toString(), "System"),
                        entry(System.NODE_UUID.toString(), companyHomeUuid),
                        entry(Content.NAME.toString(), "Company Home"),
                        entry(System.STORE_PROTOCOL.toString(), "workspace"),
                        entry(System.STORE_IDENTIFIER.toString(), "SpacesStore"),
                        entry(System.NODE_DBID.toString(), 13),
                        entry(System.LOCALE.toString(), "en_US"),
                        entry(Content.MODIFIER.toString(), "System")
                )
                .hasEntrySatisfying(
                        Content.CREATED.toString(),
                        created -> assertThat(created).isNotNull().matches(iso8601DateFormat()))
                .hasEntrySatisfying(
                        Content.MODIFIED.toString(),
                        modified -> assertThat(modified).isNotNull().matches(iso8601DateFormat()))
        ;
    }

    static Predicate<Object> iso8601DateFormat() {
        return date -> {
            if (!(date instanceof String)) {
                return false;
            }
            String dateString = (String) date;
            if (dateString.trim().equals("")) {
                return false;
            }

            try {
                return ISO8601_DATE_FORMAT.parse(dateString) != null;
            } catch (ParseException e) {
                return false;
            }

        };
    }

    DateFormat ISO8601_DATE_FORMAT = iso8601();

    static DateFormat iso8601() {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormat.setLenient(false);
        return dateFormat;
    }
}
