package eu.xenit.alfresco.webscripts.tests;

import static org.assertj.core.api.Assertions.assertThat;

import eu.xenit.alfresco.webscripts.client.spi.NodeLocatorClient;
import eu.xenit.alfresco.webscripts.client.spi.SlingShotClient;
import eu.xenit.alfresco.webscripts.client.spi.model.slingshot.Metadata;
import eu.xenit.alfresco.webscripts.client.spi.model.slingshot.Metadata.Property;
import eu.xenit.alfresco.webscripts.client.spi.model.slingshot.Metadata.ValueContainer;
import eu.xenit.testing.ditto.api.data.ContentModel.Application;
import eu.xenit.testing.ditto.api.data.ContentModel.Content;
import eu.xenit.testing.ditto.api.data.ContentModel.System;
import eu.xenit.testing.ditto.api.model.QName;
import java.util.stream.Collectors;
import org.assertj.core.api.AbstractAssert;
import org.junit.jupiter.api.Test;


public interface SlingShotClientTests {

    // Fixed NodeRef in bootstrapped Alfresco:
    // '/app:company_home/st:sites/cm:swsdp/cm:documentLibrary/cm:Agency_x0020_Files/cm:Contracts/cm:Project_x0020_Contract.pdf'
    String FIXED_NODE_PROJECT_CONTRACT = "1a0b110f-1e09-4ca2-b367-fe25e4964a4e";
    String FIXED_NODEREF_PROJECT_CONTRACT = "workspace://SpacesStore/" + FIXED_NODE_PROJECT_CONTRACT;

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


    @Test
    default void testGetMetadata_projectContract() {
        Metadata metadata = slingShotClient().get(FIXED_NODEREF_PROJECT_CONTRACT);

        assertThat(metadata).isNotNull();

        assertThat(metadata.getNodeRef()).isEqualTo(FIXED_NODEREF_PROJECT_CONTRACT);

        assertThat(metadata.getQnamePath().getName())
                .isEqualTo("/{http://www.alfresco.org/model/application/1.0}company_home"
                        + "/{http://www.alfresco.org/model/site/1.0}sites"
                        + "/{http://www.alfresco.org/model/content/1.0}swsdp"
                        + "/{http://www.alfresco.org/model/content/1.0}documentLibrary"
                        + "/{http://www.alfresco.org/model/content/1.0}Agency_x0020_Files"
                        + "/{http://www.alfresco.org/model/content/1.0}Contracts"
                        + "/{http://www.alfresco.org/model/content/1.0}Project_x0020_Contract.pdf");
        assertThat(metadata.getQnamePath().getPrefixedName())
                .isEqualTo("/app:company_home"
                        + "/st:sites"
                        + "/cm:swsdp"
                        + "/cm:documentLibrary"
                        + "/cm:Agency_x0020_Files"
                        + "/cm:Contracts"
                        + "/cm:Project_x0020_Contract.pdf");

        assertThat(metadata.getName().getName())
                .isEqualTo("{http://www.alfresco.org/model/content/1.0}Project Contract.pdf");
        assertThat(metadata.getName().getPrefixedName()).isEqualTo("cm:Project Contract.pdf");

        assertThat(metadata.getParentNodeRef())
                .isEqualTo("workspace://SpacesStore/e0856836-ed5e-4eee-b8e5-bd7e8fb9384c");

        assertThat(metadata.getType().getName()).isEqualTo("{http://www.alfresco.org/model/content/1.0}content");
        assertThat(metadata.getType().getPrefixedName()).isEqualTo("cm:content");

        assertThat(metadata.getId()).isEqualTo(FIXED_NODE_PROJECT_CONTRACT);

        assertThat(metadata.getAspects()).anySatisfy(aspect -> {
            assertThat(aspect.getName()).contains(Content.AUDITABLE.toString());
            assertThat(aspect.getPrefixedName()).contains(Content.AUDITABLE.toPrefixString());
        });

        assertThat(metadata.getProperties())
                .anySatisfy(p -> new PropertyAssert(p)
                        .hasKey(Content.AUTO_VERSION)
                        .hasValue("true")
                        .hasType("d:boolean"))
                .anySatisfy(p -> new PropertyAssert(p)
                        .hasKey(Content.AUTO_VERSION_ON_UPDATE_PROPS)
                        .hasValue("false")
                        .hasType("d:boolean"))
                .anySatisfy(p -> new PropertyAssert(p)
                        .hasKey(Content.INITIAL_VERSION)
                        .hasValue("true")
                        .hasType("d:boolean"))
                .anySatisfy(p -> new PropertyAssert(p)
                        .hasKey(Content.DESCRIPTION)
                        .hasValue("Contract for the Green Energy project")
                        .hasType("d:mltext"))
                .anySatisfy(p -> new PropertyAssert(p)
                        .hasKey(Content.NAME)
                        .hasValue("Project Contract.pdf")
                        .hasType("d:text"))
                .anySatisfy(p -> new PropertyAssert(p)
                        .hasKey(Content.TITLE)
                        .hasValue("Project Contract for Green Energy")
                        .hasType("d:mltext"))
                .anySatisfy(p -> new PropertyAssert(p)
                        .hasKey(System.NODE_DBID)
                        .hasValue("605")
                        .hasType("d:long"));
    }

    class PropertyAssert extends AbstractAssert<PropertyAssert, Property> {

        public PropertyAssert(Property actual) {
            super(actual, PropertyAssert.class);
        }

        public PropertyAssert hasKey(final QName key) {
            isNotNull();
            if (!actual.getName().getName().equals(key.toString())) {
                failWithMessage("Expected property name %s", key.toString());
            }
            return this;
        }

        public PropertyAssert hasValue(final String value) {
            isNotNull();
            assertThat(actual.getValues())
                    .isNotNull()
                    .extracting(ValueContainer::getValue)
                    .hasOnlyOneElementSatisfying(v -> assertThat(v).isNotNull().isEqualTo(value));
            return this;
        }

        public PropertyAssert hasType(final String type) {
            isNotNull();
            assertThat(actual.getType())
                    .isNotNull()
                    .satisfiesAnyOf(
                            nameContainer -> assertThat(nameContainer.getName()).isEqualTo(type),
                            nameContainer -> assertThat(nameContainer.getPrefixedName()).isEqualTo(type)
                    );
            return this;
        }
    }


}
