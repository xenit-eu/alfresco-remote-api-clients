package eu.xenit.alfresco.restapi.client.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.xenit.alfresco.client.api.exception.HttpStatusException;
import eu.xenit.alfresco.client.api.exception.ResourceNotFoundException;
import eu.xenit.alfresco.client.api.exception.StatusCode;
import eu.xenit.alfresco.restapi.client.spi.query.QueryParameters;
import java.net.URI;
import java.util.Collections;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException.NotFound;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public abstract class RestApiSpringClient {

    protected final String url;
    protected final RestTemplate restTemplate;

    public RestApiSpringClient(AlfrescoRestProperties alfrescoRestProperties) {
        this(alfrescoRestProperties, buildRestTemplate(alfrescoRestProperties));
    }

    public RestApiSpringClient(AlfrescoRestProperties alfrescoRestProperties, RestTemplate restTemplate) {
        this.url = UriComponentsBuilder.fromHttpUrl(alfrescoRestProperties.getUrl())
                .path("/api/" + alfrescoRestProperties.getTenant() + "/public/alfresco/versions/1" + getApiBasePath())
                .toUriString();

        this.restTemplate = restTemplate;
    }

    /**
     * Build a RestTemplate, but side-step all features that use classpath-detection. That gives superfluous errors when
     * used in environments with a special classloader (e.g. Fusion connector)
     */
    public static RestTemplate buildRestTemplate(AlfrescoRestProperties props) {
        RestTemplate client = new RestTemplate(Collections.singletonList(
                new MappingJackson2HttpMessageConverter(new ObjectMapper()))
        );
        client.getInterceptors().add(new BasicAuthenticationInterceptor(props.getUser(), props.getPassword()));
        return client;
    }

    protected abstract String getApiBasePath();

    protected <T, R> R execute(String nodeId, RequestEntity<T> requestEntity, Class<R> responseClass) {
        try {
            ResponseEntity<R> responseEntity = restTemplate.exchange(requestEntity, responseClass);
            return responseEntity.getBody();
        } catch (NotFound e) {
            throw new ResourceNotFoundException("Node", nodeId);
        } catch (HttpStatusCodeException e) {
            throw new HttpStatusException(StatusCode.valueOf(e.getRawStatusCode()), e.getStatusText(), e);
        }
    }

    protected UriComponentsBuilder withQueryParameters(UriComponentsBuilder builder, QueryParameters queryParameters) {
        queryParameters.queryParameters().forEach(builder::queryParam);
        return builder;
    }

    protected URI uri(String nodeId, String path, QueryParameters... queryParameters) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url).path("/" + nodeId + path);
        if (queryParameters != null) {
            for (QueryParameters queryParameter : queryParameters) {
                withQueryParameters(uriBuilder, queryParameter);
            }
        }
        return uriBuilder.build().toUri();
    }
}
