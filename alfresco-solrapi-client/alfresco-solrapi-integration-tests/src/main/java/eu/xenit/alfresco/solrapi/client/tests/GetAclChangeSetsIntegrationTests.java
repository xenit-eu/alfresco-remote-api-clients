package eu.xenit.alfresco.solrapi.client.tests;

import static org.assertj.core.api.Assertions.assertThat;

import eu.xenit.alfresco.client.solrapi.api.SolrApiClient;
import eu.xenit.alfresco.client.solrapi.api.model.AclChangeSetList;
import org.junit.jupiter.api.Test;

public interface GetAclChangeSetsIntegrationTests {
    SolrApiClient solrApiClient();

    @Test
    default void getChangeSets() {

        SolrApiClient client = solrApiClient();

        AclChangeSetList changesets = client.getAclChangeSets(1L, null, 1);
        assertThat(changesets.getAclChangeSets())
                .hasOnlyOneElementSatisfying(changeset -> {
                    assertThat(changeset.getId()).isEqualTo(1);
                    assertThat(changeset.getAclCount()).isEqualTo(2);
                });
    }
}
