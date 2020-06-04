package eu.xenit.alfresco.solrapi.client.spring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasItems;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.fail;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import eu.xenit.alfresco.client.solrapi.api.model.SolrNode;
import eu.xenit.alfresco.client.solrapi.api.model.SolrNodeMetadata;
import eu.xenit.alfresco.client.solrapi.api.model.SolrTransaction;
import eu.xenit.alfresco.client.solrapi.api.model.SolrTransactions;
import eu.xenit.alfresco.client.solrapi.api.query.NodeMetadataQueryParameters;
import eu.xenit.alfresco.client.solrapi.api.query.NodesQueryParameters;
import eu.xenit.alfresco.solrapi.client.spring.config.SolrApiProperties;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

class SolrApiSpringClientTest {

    @Test
    void getTransactions() {
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        SolrApiSpringClient client = new SolrApiSpringClient(new SolrApiProperties(), restTemplate);

        MockRestServiceServer.createServer(restTemplate)
                .expect(requestUriPath("/alfresco/service/api/solr/transactions"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(queryParam("minTxnId", "1"))
                .andExpect(queryParam("maxTxnId", "10"))
                .andExpect(queryParam("maxResults", "1"))
                .andExpect(queryParamDoesNotExists("fromCommitTime"))
                .andExpect(queryParamDoesNotExists("toCommitTime"))

                .andRespond(withSuccess(String.join(System.lineSeparator(),
                        "{",
                        "  \"transactions\": [",
                        "    {",
                        "      \"id\": 1,",
                        "      \"commitTimeMs\": 1573832424822,",
                        "      \"updates\": 4,",
                        "      \"deletes\": 0",
                        "    }",
                        "  ],",
                        "  \"maxTxnCommitTime\": 1573832436505,",
                        "  \"maxTxnId\": 16",
                        "}"), MediaType.APPLICATION_JSON));

        SolrTransactions result = client.getTransactions(null, 1L, null, 10L, 1);
        assertThat(result)
                .isNotNull()
                .satisfies(txn -> assertThat(txn.getMaxTxnCommitTime())
                        .as("check maxTxnCommitTime")
                        .isEqualTo(1573832436505L))
                .satisfies(txn -> assertThat(txn.getMaxTxnId())
                        .as("check maxTxnId")
                        .isEqualTo(16L))
                .satisfies(txn -> assertThat(txn.getTransactions())
                        .containsExactly(new SolrTransaction(1, 1573832424822L, 4L, 0L)));
    }

    @Test
    void getNodes() {

        RestTemplate restTemplate = new RestTemplateBuilder().build();
        SolrApiSpringClient client = new SolrApiSpringClient(new SolrApiProperties(), restTemplate);

        MockRestServiceServer.createServer(restTemplate)
                .expect(requestUriPath("/alfresco/service/api/solr/nodes"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.txnIds", hasItems(1, 2, 3)))
                .andExpect(jsonPath("$.fromNodeId", is(501)))
                .andExpect(jsonPath("$.toNodeId", is(505)))
                .andExpect(jsonPath("$.maxResults").exists())
                .andExpect(jsonPath("$.maxResults").exists())
                .andExpect(jsonPath("$.storeProtocol").doesNotExist())
                .andExpect(jsonPath("$.storeIdentifier").doesNotExist())
                .andExpect(jsonPath("$.includeNodeTypes").doesNotExist())
                .andExpect(jsonPath("$.excludeNodeTypes").doesNotExist())
                .andExpect(jsonPath("$.includeAspects").doesNotExist())
                .andExpect(jsonPath("$.excludeAspects").doesNotExist())

                .andRespond(withSuccess(new ClassPathResource("solrapi-nodes-txn-6-nodes-501-505.json"),
                        MediaType.APPLICATION_JSON));

        List<SolrNode> nodes = client.getNodes(
                new NodesQueryParameters()
                        .setTxnIds(Arrays.asList(1L, 2L, 3L))
                        .setFromNodeId(501L)
                        .setToNodeId(505L));

        assertThat(nodes)
                .isNotNull()
                .hasSize(5)
                .containsExactly(
                        new SolrNode(501, "workspace://SpacesStore/0eedf5dc-ee27-44c8-907c-2a4784ae0587", 6, "u", 13),
                        new SolrNode(502, "workspace://SpacesStore/67f49146-0a79-46cc-bcb9-d716dbf29a8b", 6, "u", 13),
                        new SolrNode(503, "workspace://SpacesStore/587c732e-0825-45df-ad41-ded1312d3f43", 6, "u", 13),
                        new SolrNode(504, "workspace://SpacesStore/fc522f04-2270-4717-8453-9252cd8fce9c", 6, "u", 13),
                        new SolrNode(505, "workspace://SpacesStore/6ed0f4cf-c7fb-481d-b8aa-d01676cb8c50", 6, "u", 13)
                );
    }

    @Test
    void getNodesMetadata() {

        RestTemplate restTemplate = new RestTemplateBuilder().build();
        SolrApiSpringClient client = new SolrApiSpringClient(new SolrApiProperties(), restTemplate);

        MockRestServiceServer.createServer(restTemplate)
                .expect(requestUriPath("/alfresco/service/api/solr/metadata"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType("application/json"))
                .andRespond(withSuccess(new ClassPathResource("solrapi-metadata-nodes-501-505.json"),
                        MediaType.APPLICATION_JSON));

        List<SolrNodeMetadata> nodesMetadata = client.getNodesMetadata(
                new NodeMetadataQueryParameters()
                        .setFromNodeId(501L)
                        .setToNodeId(505L)
                        .setMaxResults(5));

        assertThat(nodesMetadata)
                .isNotNull()
                .hasSize(5);

    }

    private static RequestMatcher requestUriPath(String expectedPath) {
        Assert.notNull(expectedPath, "'expectedPath' must not be null");
        return request -> assertEquals("Request URI path", expectedPath, request.getURI().getPath());
    }

    private static RequestMatcher queryParamDoesNotExists(String queryParam) {
        Assert.notNull(queryParam, "'queryParam' must not be null");
        return request -> {
            List<String> queryParamValues = getQueryParams(request).get(queryParam);
            if (queryParamValues != null) {
                fail("Expected query-param <" + queryParam + "> not to exist, but it exists with values: "
                        + queryParamValues + " in " + request.getURI());
            }
        };
    }

    private static MultiValueMap<String, String> getQueryParams(ClientHttpRequest request) {
        return UriComponentsBuilder.fromUri(request.getURI()).build().getQueryParams();
    }
}