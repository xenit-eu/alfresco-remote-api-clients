package eu.xenit.alfresco.webscripts.client.spring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.fail;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import eu.xenit.alfresco.webscripts.client.spi.ApiMetadataClient;
import eu.xenit.alfresco.webscripts.client.spi.ApiMetadataClient.Metadata;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

class ApiMetadataSpringClientTest {

    @Test
    void getMetadata() {
        final String nodeRef = UUID.randomUUID().toString();

        RestTemplate restTemplate = new RestTemplateBuilder().build();
        ApiMetadataClient client = new ApiMetadataSpringClient(new MetadataApiProperties(), restTemplate);

        MockRestServiceServer.createServer(restTemplate)
                .expect(requestUriPath("/alfresco/service/api/metadata"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(queryParam("nodeRef", nodeRef))

                .andRespond(withSuccess(
                        "{" +
                                "\"nodeRef\": \"" + nodeRef + "\"" +
                                // TODO expand
                                "}", MediaType.APPLICATION_JSON));

        Metadata result = client.get(nodeRef);
        assertThat(result)
                .isNotNull()
                .satisfies(metadata -> assertThat(metadata.getNodeRef())
                        .isEqualTo(nodeRef));
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