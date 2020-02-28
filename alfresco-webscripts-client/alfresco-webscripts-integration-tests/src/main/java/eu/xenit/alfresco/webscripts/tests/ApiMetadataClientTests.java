package eu.xenit.alfresco.webscripts.tests;

import static org.assertj.core.api.Assertions.assertThat;

import eu.xenit.alfresco.webscripts.client.spi.ApiMetadataClient;
import eu.xenit.alfresco.webscripts.client.spi.ApiMetadataClient.BulkMetadata;
import eu.xenit.alfresco.webscripts.client.spi.ApiMetadataClient.Metadata;
import eu.xenit.alfresco.webscripts.client.spi.NodeLocatorClient;
import eu.xenit.testing.ditto.api.data.ContentModel;
import eu.xenit.testing.ditto.api.data.ContentModel.Application;
import eu.xenit.testing.ditto.api.data.ContentModel.Content;
import eu.xenit.testing.ditto.api.data.ContentModel.System;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
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
                .containsEntry(Application.ICON.toString(), "space-icon-default")
                .containsEntry(Content.TITLE.toString(), "Company Home")
                .containsEntry(Content.DESCRIPTION.toString(), "The company root space")
                .containsEntry(Content.CREATOR.toString(), "System")
                .containsEntry(System.NODE_UUID.toString(), companyHomeUuid)
                .containsEntry(Content.NAME.toString(), "Company Home")
                .containsEntry(System.STORE_PROTOCOL.toString(), "workspace")
                .containsEntry(System.STORE_IDENTIFIER.toString(), "SpacesStore")
                .containsEntry(System.NODE_DBID.toString(), 13)
                .containsEntry(System.LOCALE.toString(), "en_US")
                .hasEntrySatisfying(Content.MODIFIER.toString(), s -> assertThat(s).asString().isNotBlank())
                .hasEntrySatisfying(
                        Content.CREATED.toString(),
                        created -> assertThat(created).isNotNull().matches(iso8601DateFormat()))
                .hasEntrySatisfying(
                        Content.MODIFIED.toString(),
                        modified -> assertThat(modified).isNotNull().matches(iso8601DateFormat()))
        ;
    }

    @Test
    default void testGetMetadataBulked_companyHome() {

        String companyHomeNodeRef = nodeLocatorClient().getCompanyHome();

        List<BulkMetadata> bulkMetadata = apiMetadataClient().get(Collections.singletonList(companyHomeNodeRef));
        assertThat(bulkMetadata).hasOnlyOneElementSatisfying(metadata -> {
            assertThat(metadata.getNodeRef()).isEqualTo(companyHomeNodeRef);
            assertThat(metadata.getParentNodeRef()).isNotNull();
            assertThat(metadata.getType()).isEqualTo(Content.FOLDER.toString());
            assertThat(metadata.getShortType()).isEqualTo(Content.FOLDER.toPrefixString());
//            assertThat(metadata.getTypeTitle()).isEqualTo("Folder"); not yet supported in fake implementation
            assertThat(metadata.getName()).isEqualTo("Company Home");
            assertThat(metadata.getTitle()).isEqualTo("Company Home");
            assertThat(metadata.getMimeType()).isEqualTo("");
            assertThat(metadata.getHasWritePermission()).isTrue();
            assertThat(metadata.getHasDeletePermission()).isTrue();
        });
    }

    @Test
    default void testGetMetadataBulked_nonExistingUuid() {
        final String nonExistingUuid = "workspace://SpacesStore/123-ik-denk-het-nie";

        List<BulkMetadata> bulkMetadata = apiMetadataClient().get(Collections.singletonList(nonExistingUuid));
        assertThat(bulkMetadata).hasOnlyOneElementSatisfying(metadata -> {
            assertThat(metadata.getNodeRef()).isNotNull().isEqualTo(nonExistingUuid);
            assertThat(metadata.getError()).isEqualTo("true");
            assertThat(metadata.getErrorCode()).isEqualTo("invalidNodeRef");
            assertThat(metadata.getErrorText()).isEqualTo("");
        });
    }

    @Test
    default void testGetMetadataBulked_emptyList() {
        List<BulkMetadata> bulkMetadata = apiMetadataClient().get(Collections.emptyList());
        assertThat(bulkMetadata).isEmpty();
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
