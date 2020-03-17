package eu.xenit.alfresco.solrapi.client.tests;

import static org.assertj.core.api.Assertions.assertThat;

import eu.xenit.alfresco.solrapi.client.spi.SolrApiClient;
import eu.xenit.alfresco.solrapi.client.spi.dto.GetTextContentResponse;
import java.util.Scanner;
import org.junit.jupiter.api.Test;


public interface GetTextContentResponseIntegrationTests {
    SolrApiClient solrApiClient();

    @Test
    default void textContent() {
        SolrApiClient client = solrApiClient();

        String textExpected = new Scanner( GetTextContentResponseIntegrationTests.class.getResourceAsStream("/node377.txt")).useDelimiter("\\A").next();
        GetTextContentResponse content = client.getTextContent(377L, null);
        String textReceived = new Scanner(content.getContent()).useDelimiter("\\A").next();
        assertThat(textReceived).isEqualTo(textExpected);
    }
}
