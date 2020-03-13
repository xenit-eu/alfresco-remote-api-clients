package eu.xenit.alfresco.webscripts.client.spring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.fail;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import eu.xenit.alfresco.webscripts.client.spi.ApiMetadataClient;
import eu.xenit.alfresco.webscripts.client.spi.ApiMetadataClient.Metadata;
import eu.xenit.alfresco.webscripts.client.spring.model.AlfrescoProperties;
import java.util.Collection;
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
        ApiMetadataClient client = new ApiMetadataSpringClient(new AlfrescoProperties(), restTemplate);

        MockRestServiceServer.createServer(restTemplate)
                .expect(requestUriPath("/alfresco/service/api/metadata"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(queryParam("nodeRef", nodeRef))

                .andRespond(withSuccess(
                        "{" +
                                "\"nodeRef\":\"" + nodeRef + "\"," +
                                "\"aspects\":[" +
                                "\"{http://www.alfresco.org/model/content/1.0}titled\"," +
                                "\"{http://www.alfresco.org/model/content/1.0}auditable\"," +
                                "\"{http://www.alfresco.org/model/system/1.0}referenceable\"" +
                                "]," +
                                "\"mimetype\":\"application/octet-stream\"," +
                                "\"type\":\"{http://www.alfresco.org/model/content/1.0}folder\"," +
                                "\"properties\":{" +
                                "\"{http://www.alfresco.org/model/content/1.0}created\":\"2020-02-24T08:04:36.613Z\"," +
                                "\"{http://www.alfresco.org/model/content/1.0}title\":\"Company Home\"," +
                                "\"{http://www.alfresco.org/model/content/1.0}description\":\"The company root space\""
                                + "}" +
                                "}", MediaType.APPLICATION_JSON));

        Metadata result = client.get(nodeRef);
        assertThat(result)
                .isNotNull()
                .satisfies(m -> assertThat(m.getNodeRef()).isEqualTo(nodeRef))
                .satisfies(m -> assertThat(m.getMimetype()).isEqualTo("application/octet-stream"))
                .satisfies(m -> assertThat(m.getType()).isEqualTo("{http://www.alfresco.org/model/content/1.0}folder"))
                .satisfies(m -> assertThat(m.getAspects()).contains(
                        "{http://www.alfresco.org/model/content/1.0}titled",
                        "{http://www.alfresco.org/model/content/1.0}auditable",
                        "{http://www.alfresco.org/model/system/1.0}referenceable"
                ))
                .satisfies(m -> assertThat(m.getProperties())
                        .containsEntry("{http://www.alfresco.org/model/content/1.0}title", "Company Home"));
    }

    @Test
    void getMetadata_multiValueProperty() {
        final String nodeRef = UUID.randomUUID().toString();

        RestTemplate restTemplate = new RestTemplateBuilder().build();
        ApiMetadataClient client = new ApiMetadataSpringClient(new AlfrescoProperties(), restTemplate);

        MockRestServiceServer.createServer(restTemplate)
                .expect(requestUriPath("/alfresco/service/api/metadata"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(queryParam("nodeRef", nodeRef))

                .andRespond(withSuccess(
                        "{" +
                                "\"nodeRef\":\"" + nodeRef + "\"," +
                                "\"aspects\":[" +
                                "\"{http://www.alfresco.org/model/content/1.0}titled\"," +
                                "\"{http://www.alfresco.org/model/content/1.0}auditable\"," +
                                "\"{http://www.alfresco.org/model/system/1.0}referenceable\"" +
                                "]," +
                                "\"mimetype\":\"application/octet-stream\"," +
                                "\"type\":\"{http://www.alfresco.org/model/rule/1.0}rule\"," +
                                "\"properties\":{" +
                                "\"{http://www.alfresco.org/model/content/1.0}created\":\"2020-02-20T14:49:14.517Z\"," +
                                "\"{http://www.alfresco.org/model/content/1.0}title\":\"Specialise Type to Dictionary Model\","
                                +
                                "\"{http://www.alfresco.org/model/rule/1.0}disabled\":\"false\"," +
                                "\"{http://www.alfresco.org/model/content/1.0}description\":\"Specialise Type to Dictionary Model\","
                                +
                                "\"{http://www.alfresco.org/model/rule/1.0}ruleType\":[\"inbound\"]"
                                + "}" +
                                "}", MediaType.APPLICATION_JSON));

        Metadata result = client.get(nodeRef);
        assertThat(result)
                .isNotNull()
                .satisfies(m -> assertThat(m.getNodeRef()).isEqualTo(nodeRef))
                .satisfies(m -> assertThat(m.getMimetype()).isEqualTo("application/octet-stream"))
                .satisfies(m -> assertThat(m.getType()).isEqualTo("{http://www.alfresco.org/model/rule/1.0}rule"))
                .satisfies(m -> assertThat(m.getAspects()).contains(
                        "{http://www.alfresco.org/model/content/1.0}titled",
                        "{http://www.alfresco.org/model/content/1.0}auditable",
                        "{http://www.alfresco.org/model/system/1.0}referenceable"
                ))
                .satisfies(m -> assertThat(m.getProperties())
                        .containsEntry("{http://www.alfresco.org/model/content/1.0}title",
                                "Specialise Type to Dictionary Model")
                        .hasEntrySatisfying("{http://www.alfresco.org/model/rule/1.0}ruleType",
                                v -> {
                                    assertThat(v).isInstanceOf(Collection.class);
                                    assertThat((Collection<String>) v).hasSize(1).contains("inbound");
                                }));
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