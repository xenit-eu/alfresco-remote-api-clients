package eu.xenit.alfresco.webscripts.tests;

import static org.assertj.core.api.Assertions.assertThat;

import eu.xenit.alfresco.webscripts.client.spi.NodeLocatorClient;
import eu.xenit.alfresco.webscripts.client.spi.SlingShotClient;
import eu.xenit.alfresco.webscripts.client.spi.model.slingshot.Metadata;
import eu.xenit.testing.ditto.api.data.ContentModel.Application;
import eu.xenit.testing.ditto.api.data.ContentModel.Content;
import eu.xenit.testing.ditto.api.data.ContentModel.System;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;


public interface SlingShotClientTests {

    SlingShotClient slingShotClient();

    NodeLocatorClient nodeLocatorClient();


    @Test
    default void testGetMetadata_companyHome() {
        String companyHomeNodeRef = nodeLocatorClient().getCompanyHome();
        String companyHomeUuid = companyHomeNodeRef.substring(companyHomeNodeRef.lastIndexOf("/") + 1);

        Metadata metadata = slingShotClient().get(companyHomeNodeRef);

        assertThat(metadata).isNotNull();

        assertThat(metadata.getNodeRef()).isEqualTo(companyHomeNodeRef);

        assertThat(metadata.getQnamePath().getName())
                .startsWith("/{http://www.alfresco.org/model/application/1.0}company_home");
        assertThat(metadata.getQnamePath().getPrefixedName()).isEqualTo("/app:company_home");

        assertThat(metadata.getName().getName())
                .startsWith("{http://www.alfresco.org/model/application/1.0}company_home");
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
                .collect(Collectors
                        .toMap(p -> p.getName().getName(), property -> property.getValues().get(0).getValue())))
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
                .containsEntry(System.NODE_DBID.toString(), "13")
                .containsEntry(System.LOCALE.toString(), "en_US")
                .hasEntrySatisfying(Content.MODIFIER.toString(), s -> assertThat(s).isNotBlank());
        assertThat(metadata.getParents())
                .isNotEmpty()
                .hasSize(1)
                .allSatisfy(p -> assertThat(p.getNodeRef()).isEqualTo(metadata.getParentNodeRef())
                );

    }


}
