package eu.xenit.alfresco.webscripts.tests;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import eu.xenit.alfresco.webscripts.client.spi.ApiVersionClient;
import eu.xenit.alfresco.webscripts.client.spi.ApiVersionClient.Version;
import eu.xenit.alfresco.webscripts.client.spi.NodeLocatorClient;
import java.util.List;
import org.junit.jupiter.api.Test;

public interface ApiVersionClientTests {

    // Fixed NodeRef in bootstrapped Alfresco:
    // '/app:company_home/st:sites/cm:swsdp/cm:documentLibrary/cm:Agency_x0020_Files/cm:Contracts/cm:Project_x0020_Contract.pdf'
    String FIXED_NODE_PROJECT_CONTRACT = "1a0b110f-1e09-4ca2-b367-fe25e4964a4e";
    String FIXED_NODEREF_PROJECT_CONTRACT = "workspace://SpacesStore/" + FIXED_NODE_PROJECT_CONTRACT;

    ApiVersionClient apiVersionClient();

    NodeLocatorClient nodeLocatorClient();

    @Test
    default void testCompanyHome() {
        String companyHomeNodeRef = nodeLocatorClient().getCompanyHome();

        List<Version> versions = apiVersionClient().getVersions(companyHomeNodeRef);
        assertThat(versions).hasOnlyOneElementSatisfying(v -> {
            assertThat(v).extracting(Version::getNodeRef).isEqualTo(companyHomeNodeRef);
            assertThat(v).extracting(Version::getCreator)
                    .hasFieldOrPropertyWithValue("userName", "System")
                    .hasFieldOrPropertyWithValue("firstName", "System")
                    .hasFieldOrPropertyWithValue("lastName", "User");
        });
    }

    @Test
    default void testProjectContract() {
        List<Version> versions = apiVersionClient().getVersions(FIXED_NODEREF_PROJECT_CONTRACT);

        assertThat(versions).hasOnlyOneElementSatisfying(v -> {
            assertThat(v).extracting(Version::getNodeRef).asString().startsWith("versionStore://version2Store");
            assertThat(v).extracting(Version::getName).isEqualTo("Project Contract.pdf");
            assertThat(v).extracting(Version::getLabel).isEqualTo("1.1");
            assertThat(v).extracting(Version::getCreatedDateISO).isEqualTo("2011-06-14T10:28:54.714Z");
            assertThat(v).extracting(Version::getCreator)
                    .hasFieldOrPropertyWithValue("userName", "admin")
                    .hasFieldOrPropertyWithValue("firstName", "Administrator")
                    .hasFieldOrPropertyWithValue("lastName", "");
        });
    }
}
