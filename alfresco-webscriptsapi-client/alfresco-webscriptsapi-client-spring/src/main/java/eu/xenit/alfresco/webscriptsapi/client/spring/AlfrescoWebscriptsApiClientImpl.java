package eu.xenit.alfresco.webscriptsapi.client.spring;

import eu.xenit.alfresco.webscriptsapi.client.spi.AlfrescoWebscriptsApiClient;
import eu.xenit.alfresco.webscriptsapi.client.spi.dto.Metadata;
import eu.xenit.alfresco.webscriptsapi.client.spi.dto.search.afts.SlingshotNodeStoreSearchResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.function.Predicate;

@Slf4j
public class AlfrescoWebscriptsApiClientImpl implements AlfrescoWebscriptsApiClient {

    private final RestTemplate restTemplate;
    private final String url;

    public AlfrescoWebscriptsApiClientImpl(String url, String username, String password) throws URISyntaxException {
        this(url, new HttpBasicAuthRequestFactory(
                new HttpHost(
                        new URI(url)
                                        .getHost())));
        this.restTemplate.getInterceptors()
                .add(new BasicAuthenticationInterceptor(username, password));
    }

    public AlfrescoWebscriptsApiClientImpl(String url, ClientHttpRequestFactory requestFactory) {
        this(url, new RestTemplate(requestFactory));
    }

    public AlfrescoWebscriptsApiClientImpl(String url, RestTemplate restTemplate) {
        this.url =UriComponentsBuilder.fromHttpUrl(url)
                .path("/service")
                .toUriString();
        this.restTemplate = restTemplate;
        this.restTemplate.getInterceptors()
                .add(new LogAsCurlRequestsInterceptor());
        DefaultUriBuilderFactory defaultUriBuilderFactory = new DefaultUriBuilderFactory();
        defaultUriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);
        this.restTemplate.setUriTemplateHandler(defaultUriBuilderFactory);
    }

    @Override
    public Metadata getMetadata(String nodeRef) {
        log.debug("getMetadata for " + nodeRef);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url).path("/api/metadata");
        conditionalQueryParam(uriBuilder, "nodeRef", nodeRef, Objects::nonNull);
        return restTemplate.getForObject(uriBuilder.toUriString(), Metadata.class);
    }

    @Override
    public SlingshotNodeStoreSearchResult getSlingshotNodeSearch(String store, String query, String lang) {
        log.debug("getMetadata for store=" + store + "&q=" + query + "&lang=" + lang);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url).path("/slingshot/node/search");
        conditionalQueryParam(uriBuilder, "store", store, Objects::nonNull);
        conditionalQueryParam(uriBuilder, "q", query, Objects::nonNull);
        conditionalQueryParam(uriBuilder, "lang", lang, Objects::nonNull);
        return restTemplate.getForObject(uriBuilder.toUriString(), SlingshotNodeStoreSearchResult.class);
    }

    private static void conditionalQueryParam(UriComponentsBuilder builder, String key, Object value,
                                              Predicate<Object> condition) {
        Objects.requireNonNull(builder, "builder is required");
        Objects.requireNonNull(key, "key is required");
        if (condition.test(value)) {
            builder.queryParam(key, value);
        }
    }
}
