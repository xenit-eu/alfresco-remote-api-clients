package eu.xenit.alfresco.webscripts.tests;

import static eu.xenit.alfresco.webscripts.tests.ApiMetadataClientTests.iso8601DateFormat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import eu.xenit.alfresco.webscripts.client.spi.NodeLocatorClient;
import eu.xenit.alfresco.webscripts.client.spi.SlingshotClient;
import eu.xenit.alfresco.webscripts.client.spi.model.slingshot.Metadata;
import eu.xenit.testing.ditto.api.data.ContentModel.Application;
import eu.xenit.testing.ditto.api.data.ContentModel.Content;
import eu.xenit.testing.ditto.api.data.ContentModel.System;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;


public interface SlingShotClientTests {

    SlingshotClient slingShotClient();

    NodeLocatorClient nodeLocatorClient();


    @Test
    default void testGetMetadata_companyHome() {
        String companyHomeNodeRef = nodeLocatorClient().getCompanyHome();
        String companyHomeUuid = companyHomeNodeRef.substring(companyHomeNodeRef.lastIndexOf("/") + 1);

        Metadata metadata = slingShotClient().get(companyHomeNodeRef);

        assertThat(metadata).isNotNull();

        assertThat(metadata.getNodeRef()).isEqualTo(companyHomeNodeRef);

        assertThat(metadata.getQnamePath().getName()).startsWith("/{http://www.alfresco.org/model/application/1.0}company_home");
        assertThat(metadata.getQnamePath().getPrefixedName()).isEqualTo("/app:company_home");

        assertThat(metadata.getName().getName()).startsWith("{http://www.alfresco.org/model/application/1.0}company_home");
        assertThat(metadata.getName().getPrefixedName()).isEqualTo("app:company_home");

        assertThat(metadata.getParentNodeRef()).startsWith("workspace://SpacesStore/");

        assertThat(metadata.getType().getName()).isEqualTo("{http://www.alfresco.org/model/content/1.0}folder");
        assertThat(metadata.getType().getPrefixedName()).isEqualTo("cm:folder");

        assertThat(metadata.getId()).isEqualTo(companyHomeUuid);

        assertThat(metadata.getAspects()).anySatisfy(aspect -> {
            assertThat(aspect.getName()).contains(Content.AUDITABLE.toString());
            assertThat(aspect.getPrefixedName()).contains(Content.AUDITABLE.toPrefixString());
        });
        assertThat(metadata.getType().getName()).isEqualTo(Content.FOLDER.toString());
        assertThat(metadata.getType().getPrefixedName()).isEqualTo(Content.FOLDER.toPrefixString());

        assertThat(metadata.getProperties().stream()
                .collect(Collectors.toMap(p -> p.getName().getName(), property -> property.getValue().getValue())))
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
                        entry(System.NODE_DBID.toString(), "13"),
                        entry(System.LOCALE.toString(), "en_US"),
                        entry(Content.MODIFIER.toString(), "System")
                )
                .hasEntrySatisfying(
                        Content.CREATED.toString(),
                        created -> assertThat(created).isNotNull().matches(iso8601DateFormat()))
                .hasEntrySatisfying(
                        Content.MODIFIED.toString(),
                        modified -> assertThat(modified).isNotNull().matches(iso8601DateFormat()));

        assertThat(metadata.getParents())
                .isNotEmpty()
                .hasSize(1)
                .allSatisfy(p -> assertThat(p.getNodeRef()).isEqualTo(metadata.getParentNodeRef())
                );

    }


}
