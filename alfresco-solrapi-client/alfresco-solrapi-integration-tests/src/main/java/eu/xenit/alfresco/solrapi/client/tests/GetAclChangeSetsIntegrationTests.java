package eu.xenit.alfresco.solrapi.client.tests;

import static org.assertj.core.api.Assertions.assertThat;

import eu.xenit.alfresco.solrapi.client.spi.SolrApiClient;
import eu.xenit.alfresco.solrapi.client.spi.dto.AclChangeSet;
import java.util.List;
import org.junit.jupiter.api.Test;

public interface GetAclChangeSetsIntegrationTests {
    SolrApiClient solrApiClient();

    @Test
    default void getChangeSets() {

        SolrApiClient client = solrApiClient();

        List<AclChangeSet> changesets = client.getAclChangeSets(1L, null, 1);
        assertThat(changesets)
                .hasOnlyOneElementSatisfying(changeset -> {
                    assertThat(changeset.getId()).isEqualTo(1);
                    assertThat(changeset.getAclCount()).isEqualTo(2);
                });
    }
}
