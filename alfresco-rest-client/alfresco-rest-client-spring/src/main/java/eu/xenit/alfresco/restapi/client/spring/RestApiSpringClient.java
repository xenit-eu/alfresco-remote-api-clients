package eu.xenit.alfresco.restapi.client.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.xenit.alfresco.restapi.client.spi.query.QueryParameters;
import java.util.Collections;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
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

    protected UriComponentsBuilder withQueryParameters(UriComponentsBuilder builder, QueryParameters queryParameters) {
        queryParameters.queryParameters().forEach(builder::queryParam);
        return builder;
    }
}
