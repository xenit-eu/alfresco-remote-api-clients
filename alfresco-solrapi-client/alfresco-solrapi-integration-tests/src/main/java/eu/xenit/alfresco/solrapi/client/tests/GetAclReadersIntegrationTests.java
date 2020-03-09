package eu.xenit.alfresco.solrapi.client.tests;

import static org.assertj.core.api.Assertions.assertThat;
import eu.xenit.alfresco.solrapi.client.spi.SolrApiClient;
import eu.xenit.alfresco.solrapi.client.spi.dto.AclReaders;
import eu.xenit.alfresco.solrapi.client.spi.query.AclReadersQueryParameters;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

public interface GetAclReadersIntegrationTests {
    List READERS_EVERYONE = Collections.singletonList("GROUP_EVERYONE");

    SolrApiClient solrApiClient();

    @Test
    default void getAclReaders() {

        SolrApiClient client = solrApiClient();

        AclReadersQueryParameters a = new AclReadersQueryParameters(Collections.singletonList(5L));
        List<AclReaders> readersList = client.getAclReaders(a);
        assertThat(readersList)
                .hasSize(1)
                .first()
                .satisfies(readers -> {
                    assertThat(readers.getAclId()).isEqualTo(5);
                    assertThat(readers.getAclChangeSetId()).isEqualTo(3);
                    assertThat(readers.getReaders()).isEqualTo(READERS_EVERYONE);
                });
    }
}
