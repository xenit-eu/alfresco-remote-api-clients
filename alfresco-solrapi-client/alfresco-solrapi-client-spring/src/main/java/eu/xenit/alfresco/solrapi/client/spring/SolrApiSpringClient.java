package eu.xenit.alfresco.solrapi.client.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.xenit.alfresco.solrapi.client.spi.SolrApiClient;
import eu.xenit.alfresco.solrapi.client.spi.dto.Acl;
import eu.xenit.alfresco.solrapi.client.spi.dto.AclChangeSet;
import eu.xenit.alfresco.solrapi.client.spi.dto.AclChangeSetList;
import eu.xenit.alfresco.solrapi.client.spi.dto.AclList;
import eu.xenit.alfresco.solrapi.client.spi.dto.AclReaders;
import eu.xenit.alfresco.solrapi.client.spi.dto.AclReadersList;
import eu.xenit.alfresco.solrapi.client.spi.dto.AlfrescoModel;
import eu.xenit.alfresco.solrapi.client.spi.dto.AlfrescoModelDiff;
import eu.xenit.alfresco.solrapi.client.spi.dto.SolrNode;
import eu.xenit.alfresco.solrapi.client.spi.dto.SolrNodeList;
import eu.xenit.alfresco.solrapi.client.spi.dto.SolrNodeMetaData;
import eu.xenit.alfresco.solrapi.client.spi.dto.SolrNodeMetadataList;
import eu.xenit.alfresco.solrapi.client.spi.dto.SolrTransactions;
import eu.xenit.alfresco.solrapi.client.spi.query.AclReadersQueryParameters;
import eu.xenit.alfresco.solrapi.client.spi.query.AclsQueryParameters;
import eu.xenit.alfresco.solrapi.client.spi.query.NodeMetaDataQueryParameters;
import eu.xenit.alfresco.solrapi.client.spi.query.NodesQueryParameters;
import java.io.IOException;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.Assert;
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

    /**
     * Build a RestTemplate, but side-step all features that use classpath-detection. That gives superfluous errors when
     * used in environments with a special classloader (e.g. Fusion connector)
     */
    private static RestTemplate buildRestTemplate(ClientHttpRequestFactory requestFactory) {
        RestTemplate client = new RestTemplate(Collections.singletonList(
                new MappingJackson2HttpMessageConverter(new ObjectMapper()))
        );
        client.setRequestFactory(requestFactory);
        return client;
    }


    @Override
    public List<AclChangeSet> getAclChangeSets(Long fromId, Long fromTime, int maxResults)
    {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url).path("/aclchangesets");
        conditionalQueryParam(uriBuilder, "fromId", fromId, Objects::nonNull);
        conditionalQueryParam(uriBuilder, "fromTime", fromTime, Objects::nonNull);
        conditionalQueryParam(uriBuilder, "maxResults", maxResults, val ->
                val != Integer.valueOf(0) && val != Integer.valueOf(Integer.MAX_VALUE));

        AclChangeSetList response = restTemplate.getForObject(uriBuilder.toUriString(), AclChangeSetList.class);
        return response.getAclChangeSets();
    }

    @Override
    public List<Acl> getAcls(AclsQueryParameters parameters) {
        URI uri = UriComponentsBuilder.fromHttpUrl(url).path("/acls").build().toUri();

        HttpEntity<AclsQueryParameters> request = new HttpEntity<AclsQueryParameters>(parameters, defaultHttpHeaders());
        ResponseEntity<AclList> result = restTemplate.exchange(uri, HttpMethod
                .POST, request, AclList.class);

        Assert.isTrue(result.getStatusCodeValue() == 200, "HTTP "+result.getStatusCodeValue());

        AclList aclList = result.getBody();
        Assert.notNull(aclList, "Response for getAcls(" + parameters + ") should not be null");
        return aclList.getAcls();
    }

    @Override
    public List<AclReaders> getAclReaders(AclReadersQueryParameters parameters) {
        URI uri = UriComponentsBuilder.fromHttpUrl(url).path("/aclsReaders").build().toUri();

        HttpEntity<AclReadersQueryParameters> request = new HttpEntity<AclReadersQueryParameters>(parameters, defaultHttpHeaders());
        ResponseEntity<AclReadersList> result = restTemplate.exchange(uri, HttpMethod
                .POST, request, AclReadersList.class);

        Assert.isTrue(result.getStatusCodeValue() == 200, "HTTP "+result.getStatusCodeValue());

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

        return restTemplate.getForObject(uriBuilder.toUriString(), SolrTransactions.class);
    }

    @Override
    public List<SolrNode> getNodes(NodesQueryParameters parameters) {
        URI uri = UriComponentsBuilder.fromHttpUrl(url).path("/nodes").build().toUri();

        HttpEntity<NodesQueryParameters> request = new HttpEntity<>(parameters, defaultHttpHeaders());
        ResponseEntity<SolrNodeList> result = restTemplate.exchange(uri, HttpMethod
                .POST, request, SolrNodeList.class);
//
        Assert.isTrue(result.getStatusCodeValue() == 200, "HTTP " + result.getStatusCodeValue());
//        Assert.notNull(result.getBody(), "Response for getNodes(" + params + ") should not be null");

        SolrNodeList solrNodeList = result.getBody();
        Assert.notNull(solrNodeList, "Response for getNodes(" + parameters + ") should not be null");
        return solrNodeList.getNodes();
    }

    @Override
    public List<SolrNodeMetaData> getNodesMetaData(NodeMetaDataQueryParameters params) {
        URI uri = UriComponentsBuilder.fromHttpUrl(url).path("/metadata").build().toUri();

        HttpEntity<NodeMetaDataQueryParameters> request = new HttpEntity<>(params, defaultHttpHeaders());
        ResponseEntity<SolrNodeMetadataList> result = restTemplate.exchange(uri, HttpMethod
                .POST, request, SolrNodeMetadataList.class);

        Assert.isTrue(result.getStatusCodeValue() == 200, "HTTP " + result.getStatusCodeValue());
        Assert.notNull(result.getBody(), "Response for getNodes(" + params + ") should not be null");
        return result.getBody().getNodes();
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

    private static void conditionalQueryParam(UriComponentsBuilder builder, String key, Object value,
            Predicate<Object> condition) {
        Objects.requireNonNull(builder, "builder is required");
        Objects.requireNonNull(key, "key is required");
        if (condition.test(value)) {
            builder.queryParam(key, value);
        }
    }

    private static HttpHeaders defaultHttpHeaders() {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return requestHeaders;
    }
}
