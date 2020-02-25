package eu.xenit.alfresco.webscripts.client.spring;

import static org.assertj.core.api.Assertions.assertThat;

import eu.xenit.alfresco.webscripts.client.spi.NodeLocatorClient;
import eu.xenit.alfresco.webscripts.client.spi.SlingShotClient;
import eu.xenit.alfresco.webscripts.client.spi.model.slingshot.Metadata;
import eu.xenit.alfresco.webscripts.client.spring.model.SearchResponse;
import eu.xenit.alfresco.webscripts.client.spring.model.SearchResponse.Entry;
import eu.xenit.alfresco.webscripts.tests.SlingShotClientTests;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class SlingShotSpringClientIntegrationTests extends WebscriptsSpringClientTestsBase {

    @Nested
    class WithProvidedRestTemplate implements SlingShotClientTests {

        @Override
        public SlingShotClient slingShotClient() {
            return new SlingShotSpringClient(alfrescoProperties());
        }

        @Override
        public NodeLocatorClient nodeLocatorClient() {
            return new NodeLocatorSpringClient(alfrescoProperties());
        }
    }

    @Nested
    class WithCustomRestTemplate implements SlingShotClientTests {

        @Override
        public SlingShotClient slingShotClient() {
            return new SlingShotSpringClient(alfrescoProperties(), restTemplateBuilder().build());
        }

        @Override
        public NodeLocatorClient nodeLocatorClient() {
            return new NodeLocatorSpringClient(alfrescoProperties(), restTemplateBuilder().build());
        }

        @Test
        public void associationInBootstrappedAlfresco() {
            String url = UriComponentsBuilder.fromHttpUrl(alfrescoProperties().getUrl())
                    .path("/api/-default-/public/search/versions/1/search").toUriString();
            UriComponents uri = UriComponentsBuilder.fromHttpUrl(url).build();
            RestTemplate restTemplate = restTemplateBuilder().build();
            // This is a document that should exist in a bootstrapped Alfresco that has a peer-association
            String query = "{\"query\": {\"query\":\"=@cm:title:\\\"Budget cut\\\"\",\"language\": \"afts\"}}";
            SearchResponse searchResponse = restTemplate.postForObject(uri.toUri(), query, SearchResponse.class);

            if (searchResponse == null || searchResponse.getList() == null
                    || searchResponse.getList().getEntries() == null
                    || searchResponse.getList().getEntries().size() != 1) {
                throw new RuntimeException(
                        "Expecting exactly 1 doc \"Budget cut\" (with association) to be present in default Alfresco");
            }
            Entry entry = searchResponse.getList().getEntries().get(0).getEntry();
            String uuid = entry.getId();

            SlingShotClient slingShotClient = new SlingShotSpringClient(alfrescoProperties(), restTemplateBuilder().build());
            Metadata metadata = slingShotClient.get("workspace://SpacesStore/" + uuid);

            assertThat(metadata.getAssocs().size()).isEqualTo(1);
            assertThat(metadata.getAssocs()).allSatisfy(association -> {
                assertThat(association.getAssocType().getPrefixedName()).isEqualTo("cm:attachments");
            });

        }
    }


}
