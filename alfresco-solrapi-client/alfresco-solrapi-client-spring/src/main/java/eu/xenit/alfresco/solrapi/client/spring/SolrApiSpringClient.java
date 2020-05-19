package eu.xenit.alfresco.solrapi.client.spring;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.xenit.alfresco.client.exception.HttpStatusException;
import eu.xenit.alfresco.client.exception.StatusCode;
import eu.xenit.alfresco.solrapi.client.spi.SolrApiClient;
import eu.xenit.alfresco.solrapi.client.spi.dto.Acl;
import eu.xenit.alfresco.solrapi.client.spi.dto.AclChangeSetList;
import eu.xenit.alfresco.solrapi.client.spi.dto.AclList;
import eu.xenit.alfresco.solrapi.client.spi.dto.AclReaders;
import eu.xenit.alfresco.solrapi.client.spi.dto.AclReadersList;
import eu.xenit.alfresco.solrapi.client.spi.dto.AlfrescoModel;
import eu.xenit.alfresco.solrapi.client.spi.dto.AlfrescoModelDiff;
import eu.xenit.alfresco.solrapi.client.spi.dto.GetTextContentResponse;
import eu.xenit.alfresco.solrapi.client.spi.dto.GetTextContentResponse.SolrApiContentStatus;
import eu.xenit.alfresco.solrapi.client.spi.dto.SolrNode;
import eu.xenit.alfresco.solrapi.client.spi.dto.SolrNodeList;
import eu.xenit.alfresco.solrapi.client.spi.dto.SolrNodeMetaData;
import eu.xenit.alfresco.solrapi.client.spi.dto.SolrNodeMetadataList;
import eu.xenit.alfresco.solrapi.client.spi.dto.SolrTransactions;
import eu.xenit.alfresco.solrapi.client.spi.query.AclReadersQueryParameters;
import eu.xenit.alfresco.solrapi.client.spi.query.AclsQueryParameters;
import eu.xenit.alfresco.solrapi.client.spi.query.NodeMetaDataQueryParameters;
import eu.xenit.alfresco.solrapi.client.spi.query.NodesQueryParameters;
import eu.xenit.alfresco.solrapi.client.spring.http.InsecureSslHttpComponentsClientHttpRequestFactory;
import eu.xenit.alfresco.solrapi.client.spring.http.SolrRequestFactory;
import eu.xenit.alfresco.solrapi.client.spring.model.HttpProperties;
import eu.xenit.alfresco.solrapi.client.spring.model.SolrApiProperties;
import eu.xenit.alfresco.solrapi.client.spring.model.SolrSslProperties;
import java.io.IOException;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.Assert;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
public class SolrApiSpringClient implements SolrApiClient {

    private final RestTemplate restTemplate;
    private final String url;

    public SolrApiSpringClient(SolrApiProperties solrApiProperties, SolrSslProperties solrSslProperties)
            throws GeneralSecurityException, IOException {
        this(solrApiProperties, new SolrRequestFactory(solrSslProperties));
    }

    public SolrApiSpringClient(SolrApiProperties solrProperties) {
        this(solrProperties, createHttpRequestFactor(solrProperties.getHttp()));
    }

    public SolrApiSpringClient(SolrApiProperties solrProperties, ClientHttpRequestFactory requestFactory) {
        this(solrProperties, buildRestTemplate(requestFactory));
    }

    public SolrApiSpringClient(SolrApiProperties solrProperties, RestTemplate restTemplate) {
        this.url = UriComponentsBuilder.fromHttpUrl(solrProperties.getUrl())
                .path("/service/api/solr")
                .toUriString();

        // experimental
        restTemplate.getInterceptors().add(new LogAsCurlRequestsInterceptor());

        this.restTemplate = restTemplate;
    }

    private static ClientHttpRequestFactory createHttpRequestFactor(HttpProperties httpProperties) {
        HttpComponentsClientHttpRequestFactory ret = httpProperties.isInsecureSsl() ?
                new InsecureSslHttpComponentsClientHttpRequestFactory() :
                new HttpComponentsClientHttpRequestFactory();

        ret.setReadTimeout(httpProperties.getTimeout().getSocket());
        ret.setConnectTimeout(httpProperties.getTimeout().getConnect());
        ret.setConnectionRequestTimeout(httpProperties.getTimeout().getConnectionRequest());

        return ret;
    }

    /**
     * Build a RestTemplate, but side-step all features that use classpath-detection. That gives superfluous errors when
     * used in environments with a special classloader (e.g. Fusion connector)
     */
    private static RestTemplate buildRestTemplate(ClientHttpRequestFactory requestFactory) {
        RestTemplate client = new RestTemplate(Arrays.asList(
                new MappingJackson2HttpMessageConverter(new ObjectMapper()),
                new ResourceHttpMessageConverter())
        );
        client.setRequestFactory(requestFactory);
        return client;
    }

    private static void conditionalQueryParam(UriComponentsBuilder builder, String key, Object value,
            Predicate<Object> condition) {
        Objects.requireNonNull(builder, "builder is required");
        Objects.requireNonNull(key, "key is required");
        if (condition.test(value)) {
            builder.queryParam(key, value);
        }
    }

    protected static HttpHeaders defaultHttpHeaders() {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return requestHeaders;
    }

    @Override
    public AclChangeSetList getAclChangeSets(Long fromId, Long fromTime, int maxResults) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url).path("/aclchangesets");
        conditionalQueryParam(uriBuilder, "fromId", fromId, Objects::nonNull);
        conditionalQueryParam(uriBuilder, "fromTime", fromTime, Objects::nonNull);
        conditionalQueryParam(uriBuilder, "maxResults", maxResults, val ->
                val != Integer.valueOf(0) && val != Integer.valueOf(Integer.MAX_VALUE));

        ResponseEntity<AclChangeSetList> response =
                execute(uriBuilder.build().toUri(), HttpMethod.GET, AclChangeSetList.class);
        return response.getBody();
    }

    @Override
    public List<Acl> getAcls(AclsQueryParameters parameters) {
        URI uri = UriComponentsBuilder.fromHttpUrl(url).path("/acls").build().toUri();

        HttpEntity<AclsQueryParameters> request = new HttpEntity<AclsQueryParameters>(parameters, defaultHttpHeaders());
        ResponseEntity<AclList> result = execute(uri, HttpMethod.POST, request, AclList.class);

        Assert.isTrue(result.getStatusCodeValue() == 200, "HTTP " + result.getStatusCodeValue());

        AclList aclList = result.getBody();
        Assert.notNull(aclList, "Response for getAcls(" + parameters + ") should not be null");
        return aclList.getAcls();
    }

    @Override
    public List<AclReaders> getAclReaders(AclReadersQueryParameters parameters) {
        URI uri = UriComponentsBuilder.fromHttpUrl(url).path("/aclsReaders").build().toUri();

        HttpEntity<AclReadersQueryParameters> request = new HttpEntity<>(parameters,
                defaultHttpHeaders());
        ResponseEntity<AclReadersList> result = execute(uri, HttpMethod.POST, request, AclReadersList.class);

        Assert.isTrue(result.getStatusCodeValue() == 200, "HTTP " + result.getStatusCodeValue());

        AclReadersList aclReadersList = result.getBody();
        Assert.notNull(aclReadersList, "Response for getAclsReaders(" + parameters + ") should not be null");
        return aclReadersList.getAclsReaders();
    }

    @Override
    public SolrTransactions getTransactions(Long fromCommitTime, Long minTxnId, Long toCommitTime, Long maxTxnId,
            int maxResults) {

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url).path("/transactions");
        conditionalQueryParam(uriBuilder, "fromCommitTime", fromCommitTime, Objects::nonNull);
        conditionalQueryParam(uriBuilder, "minTxnId", minTxnId, Objects::nonNull);
        conditionalQueryParam(uriBuilder, "toCommitTime", toCommitTime, Objects::nonNull);
        conditionalQueryParam(uriBuilder, "maxTxnId", maxTxnId, Objects::nonNull);
        conditionalQueryParam(uriBuilder, "maxResults", maxResults, val ->
                val != Integer.valueOf(0) && val != Integer.valueOf(Integer.MAX_VALUE));

        ResponseEntity<SolrTransactions> response
                = execute(uriBuilder.build().toUri(), HttpMethod.GET, SolrTransactions.class);
        return response.getBody();
    }

    @Override
    public List<SolrNode> getNodes(NodesQueryParameters parameters) {
        URI uri = UriComponentsBuilder.fromHttpUrl(url).path("/nodes").build().toUri();

        HttpEntity<NodesQueryParameters> request = new HttpEntity<>(parameters, defaultHttpHeaders());
        ResponseEntity<SolrNodeList> result =
                execute(uri, HttpMethod.POST, request, SolrNodeList.class);

        Assert.isTrue(result.getStatusCodeValue() == 200, "HTTP " + result.getStatusCodeValue());

        SolrNodeList solrNodeList = result.getBody();
        Assert.notNull(solrNodeList, "Response for getNodes(" + parameters + ") should not be null");
        return solrNodeList.getNodes();
    }

    @Override
    public List<SolrNodeMetaData> getNodesMetaData(NodeMetaDataQueryParameters params) {
        URI uri = UriComponentsBuilder.fromHttpUrl(url).path("/metadata").build().toUri();

        HttpEntity<NodeMetaDataQueryParameters> request = new HttpEntity<>(params, defaultHttpHeaders());
        ResponseEntity<SolrNodeMetadataList> result =
                execute(uri, HttpMethod.POST, request, SolrNodeMetadataList.class);

        Assert.isTrue(result.getStatusCodeValue() == 200, "HTTP " + result.getStatusCodeValue());
        Assert.notNull(result.getBody(), "Response for getNodes(" + params + ") should not be null");
        return result.getBody().getNodes();
    }

    private final ObjectMapper exceptionResponseObjectMapper = new ObjectMapper();

    protected <R> ResponseEntity<R> execute(URI uri, HttpMethod httpMethod, Class<R> responseClass) {
        return execute(uri, httpMethod, HttpEntity.EMPTY, responseClass);
    }

    protected <T, R> ResponseEntity<R> execute(URI uri, HttpMethod httpMethod, HttpEntity<T> entity,
            Class<R> responseClass) {
        try {
            return restTemplate.exchange(uri, httpMethod, entity, responseClass);
        } catch (HttpStatusCodeException e) {
            final String message = tryToExtractMessage(e.getResponseBodyAsString());
            throw new HttpStatusException(StatusCode.valueOf(e.getRawStatusCode()), message, e);
        }
    }

    private String tryToExtractMessage(String responseBody) {
        try {
            SolrApiExceptionResponse response =
                    exceptionResponseObjectMapper.readValue(responseBody, SolrApiExceptionResponse.class);
            return response.getMessage();
        } catch (Exception e) {
            log.warn("Failed to extract message from response: {}", responseBody, e);
            return null;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    private static class SolrApiExceptionResponse {

        private String message;

    }

    @Override
    public GetTextContentResponse getTextContent(Long nodeId, String propertyQName) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url).path("/textContent");
        conditionalQueryParam(uriBuilder, "nodeId", nodeId, Objects::nonNull);
        conditionalQueryParam(uriBuilder, "propertyQName", propertyQName, Objects::nonNull);

        ResponseEntity<Resource> response = execute(uriBuilder.build().toUri(), HttpMethod.GET, Resource.class);
        return getGetTextContentResponse(response);
    }

    private GetTextContentResponse getGetTextContentResponse(ResponseEntity<Resource> exchange) {
        GetTextContentResponse content = new GetTextContentResponse();
        try {
            if (exchange.getBody() != null) {
                content.setContent(exchange.getBody().getInputStream());
            }
            if (exchange.getHeaders().getContentType() != null
                    && exchange.getHeaders().getContentType().getCharset() != null) {
                content.setContentEncoding(exchange.getHeaders().getContentType().getCharset().displayName());
            }
            List<String> transformStatus = exchange.getHeaders().get("X-Alfresco-transformStatus");
            if (transformStatus != null && !transformStatus.isEmpty()) {
                content.setTransformStatusStr(transformStatus.get(0));
            }
            List<String> transformDuration = exchange.getHeaders().get("X-Alfresco-transformDuration");
            if (transformDuration != null) {
                content.setTransformDuration(
                        transformDuration.get(0) != null ? Long.valueOf(transformDuration.get(0)) : null);
            }
            List<String> transformStatusException = exchange.getHeaders().get("X-Alfresco-transformException");
            if (transformStatusException != null && !transformStatusException.isEmpty()) {
                content.setTransformException(transformStatusException.get(0));
            }
            content.setStatus(getStatus(exchange.getStatusCode(), transformStatus));
        } catch (IOException e) {
            log.error("Cannot get a GetTextContentResponse object: " + e.getLocalizedMessage());
        }
        return content;
    }

    private SolrApiContentStatus getStatus(HttpStatus status, List<String> transformStatusStr) {
        SolrApiContentStatus solrApiContentStatus;
        if (status == HttpStatus.NOT_MODIFIED) {
            return SolrApiContentStatus.NOT_MODIFIED;
        } else if (status == HttpStatus.INTERNAL_SERVER_ERROR) {
            return SolrApiContentStatus.GENERAL_FAILURE;
        } else if (status == HttpStatus.OK) {
            return SolrApiContentStatus.OK;
        } else if (status == HttpStatus.NO_CONTENT) {
            if (transformStatusStr == null || transformStatusStr.isEmpty()) {
                return SolrApiContentStatus.UNKNOWN;
            } else {
                if (transformStatusStr.get(0).equals("noTransform")) {
                    return SolrApiContentStatus.NO_TRANSFORM;
                } else if (transformStatusStr.get(0).equals("transformFailed")) {
                    return SolrApiContentStatus.TRANSFORM_FAILED;
                } else {
                    return SolrApiContentStatus.UNKNOWN;
                }
            }
        }
        return null;
    }

    @Override
    public AlfrescoModel getModel(String coreName, String modelName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<AlfrescoModelDiff> getModelsDiff(String coreName, List<AlfrescoModel> currentModels) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() {

    }
}
