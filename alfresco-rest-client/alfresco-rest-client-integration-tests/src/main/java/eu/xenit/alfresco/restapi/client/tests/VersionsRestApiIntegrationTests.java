package eu.xenit.alfresco.restapi.client.tests;

import static org.assertj.core.api.Assertions.assertThat;

import eu.xenit.alfresco.restapi.client.spi.Constants;
import eu.xenit.alfresco.restapi.client.spi.VersionsRestApiClient;
import eu.xenit.alfresco.restapi.client.spi.model.NodeEntry;
import eu.xenit.alfresco.restapi.client.spi.model.NodeList;
import org.junit.jupiter.api.Test;

public interface VersionsRestApiIntegrationTests {

    // Fixed NodeRef in bootstrapped Alfresco:
    // '/app:company_home/st:sites/cm:swsdp/cm:documentLibrary/cm:Agency_x0020_Files/cm:Contracts/cm:Project_x0020_Contract.pdf'
    String FIXED_NODE_PROJECT_CONTRACT = "1a0b110f-1e09-4ca2-b367-fe25e4964a4e";

    VersionsRestApiClient versionsRestApiClient();

    @Test
    default void getVersions_root() {

        NodeList versions = versionsRestApiClient().getVersions(Constants.Node.ROOT);

        assertThat(versions).isNotNull();
        assertThat(versions.getList().getEntries()).isEmpty();
    }

    @Test
    default void getVersions_projectContract() {

        NodeList versions = versionsRestApiClient().getVersions(FIXED_NODE_PROJECT_CONTRACT);

        assertThat(versions).isNotNull();
        assertThat(versions.getList().getEntries()).isNotEmpty();
        assertThat(versions.getList().getEntries()).hasSize(1);

        final String versionId =
                versions.getList().getEntries().get(0).getEntry().getId();

        NodeEntry version = versionsRestApiClient().getVersion(FIXED_NODE_PROJECT_CONTRACT, versionId);
        assertThat(version).isNotNull();
        assertThat(version.getEntry().getId()).isEqualTo(versionId);
    }

}
