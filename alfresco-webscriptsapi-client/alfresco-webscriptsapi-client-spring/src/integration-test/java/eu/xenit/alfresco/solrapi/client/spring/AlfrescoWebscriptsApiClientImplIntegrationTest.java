package eu.xenit.alfresco.solrapi.client.spring;

import eu.xenit.alfresco.webscriptsapi.client.spi.AlfrescoWebscriptsApiClient;
import eu.xenit.alfresco.webscriptsapi.client.spi.dto.Metadata;
import eu.xenit.alfresco.webscriptsapi.client.spi.dto.search.afts.SlingshotNodeStoreSearchResult;
import eu.xenit.alfresco.webscriptsapi.client.spi.dto.search.afts.SlingshotNodeStoreSearchResultNode;
import eu.xenit.alfresco.webscriptsapi.client.spring.AlfrescoWebscriptsApiClientImpl;
import eu.xenit.alfresco.webscriptsapi.client.spring.HttpBasicAuthRequestFactory;
import org.apache.http.HttpHost;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AlfrescoWebscriptsApiClientImplIntegrationTest {
    private static AlfrescoWebscriptsApiClientImpl alfrescoWebscriptsApiClient() throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder()
                .setScheme("http")
                .setHost(System.getProperty("alfresco.host", "localhost"))
                .setPort(Integer.parseInt(System.getProperty("alfresco.tcp.8080", "8080")))
                .setPath("alfresco");
        String url = uriBuilder.build().toString();

        HttpBasicAuthRequestFactory requestFactory = new HttpBasicAuthRequestFactory(
                new HttpHost(new URI(url).getHost()));

        RestTemplate restTemplate = new RestTemplate(requestFactory);
        restTemplate.getInterceptors()
                .add(new BasicAuthenticationInterceptor("admin", "admin"));

        DefaultUriBuilderFactory defaultUriBuilderFactory = new DefaultUriBuilderFactory();
        defaultUriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);
        restTemplate.setUriTemplateHandler(defaultUriBuilderFactory);

        return new AlfrescoWebscriptsApiClientImpl(url, restTemplate);
    }

    static class SlingshotTestSuite {
        @Test
        public void getSlingshotNodeSearch_companyHome() throws URISyntaxException {
            String shortQName = "app:company_home";
            String fullyQualifiedQName = "{http://www.alfresco.org/model/application/1.0}company_home";
            String qNamePathQry = "PATH:\"/" + shortQName + "\"";
            String store = AlfrescoWebscriptsApiClient.Stores.WORKSPACE_SPACESSTORE;
            String lang = AlfrescoWebscriptsApiClient.QueryLanguages.FTS_ALFRESCO;

            AlfrescoWebscriptsApiClient client = alfrescoWebscriptsApiClient();
            SlingshotNodeStoreSearchResult resultsObject = client
                    .getSlingshotNodeSearch(store, qNamePathQry, lang);
            List<SlingshotNodeStoreSearchResultNode> nodes = resultsObject.getResults();

            assertThat(nodes)
                    .hasOnlyOneElementSatisfying(n -> {
                            assertThat(n.getNodeRef())
                                    .startsWith(AlfrescoWebscriptsApiClient.Stores.WORKSPACE_SPACESSTORE);
                            assertThat(n.getQName())
                                    .satisfies(q -> {
                                        assertThat(q.getShortQName())
                                                .isEqualTo(shortQName);
                                        assertThat(q.getFullyQualifiedQName())
                                                .isEqualTo(fullyQualifiedQName);
                                    });
                            assertThat(n.getQNamePath())
                                    .satisfies(q -> {
                                        assertThat(q.getShortQName())
                                                .isEqualTo("/" + shortQName);
                                        assertThat(q.getFullyQualifiedQName())
                                                .isEqualTo("/" + fullyQualifiedQName);
                                    });
                        });
        }
    }

    static class MetadataTestSuite {
        @Test
        public void getMetadata_companyHome() throws URISyntaxException {
            String qNamePathQry = "PATH:\"/app:company_home\"";
            String store = AlfrescoWebscriptsApiClient.Stores.WORKSPACE_SPACESSTORE;
            String lang = AlfrescoWebscriptsApiClient.QueryLanguages.FTS_ALFRESCO;

            AlfrescoWebscriptsApiClient client = alfrescoWebscriptsApiClient();
            SlingshotNodeStoreSearchResult resultsObject = client
                    .getSlingshotNodeSearch(store, qNamePathQry,lang);
            List<SlingshotNodeStoreSearchResultNode> nodes = resultsObject.getResults();
            String companyHomeNodeRef = nodes.get(0).getNodeRef();
            Metadata metadata = client.getMetadata(companyHomeNodeRef);

            assertThat(metadata)
                    .isNotNull()
                    .satisfies(m -> {
                        assertThat(m.getType()).isEqualTo("{http://www.alfresco.org/model/content/1.0}folder");
                        assertThat(m.getNodeRef()).startsWith(store);
                        assertThat(m.getMetadataProperties())
                                .satisfies(p -> {
                                    assertThat(p.getName()).isEqualTo("Company Home");
                                });
                    });
        }
    }
}
