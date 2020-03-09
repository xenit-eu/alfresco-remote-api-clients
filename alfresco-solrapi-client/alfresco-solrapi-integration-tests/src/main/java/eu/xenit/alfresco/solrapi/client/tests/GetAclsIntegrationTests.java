package eu.xenit.alfresco.solrapi.client.tests;

import static org.assertj.core.api.Assertions.assertThat;

import eu.xenit.alfresco.solrapi.client.spi.SolrApiClient;
import eu.xenit.alfresco.solrapi.client.spi.dto.Acl;
import eu.xenit.alfresco.solrapi.client.spi.dto.AclChangeSet;
import eu.xenit.alfresco.solrapi.client.spi.query.AclsQueryParameters;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

public interface GetAclsIntegrationTests {
    SolrApiClient solrApiClient();

    @Test
    default void getAcls() {

        SolrApiClient client = solrApiClient();

        List<Acl> acls = client.getAcls(new AclsQueryParameters().withAclChangeSets(Collections.singletonList(1L)));
        assertThat(acls)
                .hasSize(2)
                .first()
                .satisfies(acl -> {
                    assertThat(acl.getId()).isEqualTo(1);
                    assertThat(acl.getAclChangeSetId()).isEqualTo(1);
                });
    }
}
