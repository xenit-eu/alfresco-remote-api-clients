package eu.xenit.alfresco.solrapi.client.spring;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.xenit.alfresco.solrapi.client.spi.Acl;
import eu.xenit.alfresco.solrapi.client.spi.AclChangeSet;
import eu.xenit.alfresco.solrapi.client.spi.AclChangeSets;
import eu.xenit.alfresco.solrapi.client.spi.AclReaders;
import eu.xenit.alfresco.solrapi.client.spi.AlfrescoModel;
import eu.xenit.alfresco.solrapi.client.spi.AlfrescoModelDiff;
import eu.xenit.alfresco.solrapi.client.spi.SolrNode;
import eu.xenit.alfresco.solrapi.client.spi.SolrNodeList;
import eu.xenit.alfresco.solrapi.client.spi.SolrNodeMetaData;
import eu.xenit.alfresco.solrapi.client.spi.NodeMetaDataQueryParameters;
import eu.xenit.alfresco.solrapi.client.spi.SolrNodeMetadataList;
import eu.xenit.alfresco.solrapi.client.spi.NodesQueryParameters;
import eu.xenit.alfresco.solrapi.client.spi.SolrApiClient;
import eu.xenit.alfresco.solrapi.client.spi.SolrTransactions;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
public class SolrAPIClientImpl implements SolrApiClient {

    private final String url;
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    public SolrAPIClientImpl(String url, ClientHttpRequestFactory requestFactory) {
        this(url,
                new RestTemplateBuilder()
                        .requestFactory(() -> requestFactory)
                        .build());
    }

    public SolrAPIClientImpl(String url, RestTemplate restTemplate) {
        this.url = url;
        this.restTemplate = restTemplate;
    }

    private static HttpEntity<String> generateJsonHttpEntity(String query) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", "application/json");
        return new HttpEntity<>(query, httpHeaders);
    }

//    @Override
//    public List<SolrTransactions> getSolrTransactions(long minTxnId, long maxTxnId) {
//        log.debug("Get SolrTransactions [{},{}[", minTxnId, maxTxnId);
//
//
//        String response = restTemplate.getForObject(
//                url + "transactions?minTxnId=" + minTxnId + "&maxTxnId=" + maxTxnId,
//                String.class);
//        try {
//            return mapper.readValue(response, SolrTransactionsList.class).getTransactions();
//        } catch (IOException e) {
//            log.error("Error trying to parse the solr response json to a List of SolrTransaction", e);
//            throw new UncheckedIOException("could not parse solr transaction json", e);
//        }
//    }
//
//    @Override
//    public List<SolrNode> getSolrNodesWithinTxnIds(List<Long> transactionIds) {
//        if (transactionIds.isEmpty()) {
//            return Collections.emptyList();
//        }
//
//        log.debug("Getting {} solr nodes", transactionIds.size());
//
//        ArrayNode txnIds = mapper.createArrayNode();
//        transactionIds.forEach(txnIds::add);
//
//        String query = mapper.createObjectNode()
//                .put("storeProtocol", "workspace")
//                .put("storeIdentifier", "SpacesStore")
//                .set("txnIds", txnIds)
//                .toString();
//
//        String response = restTemplate.postForObject(
//                url + "nodes",
//                generateJsonHttpEntity(query),
//                String.class
//        );
//
//        try {
//            return mapper.readValue(response, SolrNodeList.class).getNodes();
//        } catch (IOException e) {
//            log.error("Error trying to parse the solr response json to a List of SolrNodes", e);
//            throw new UncheckedIOException("could not parse solr nodes json", e);
//        }
//    }
//
//    @Override
//    public SolrNode getSingleSolrNode(long nodeId) {
//        log.debug("Get solr node {}", nodeId);
//        String query = mapper.createObjectNode()
//                .put("storeProtocol", "workspace")
//                .put("storeIdentifier", "SpacesStore")
//                .put("fromNodeId", nodeId)
//                .put("toNodeId", nodeId)
//                .toString();
//        String response = restTemplate.postForObject(
//                url + "nodes",
//                generateJsonHttpEntity(query),
//                String.class);
//
//        try {
//            return mapper.readValue(response, SolrNodeList.class).getNodes().get(0);
//        } catch (IOException e) {
//            log.error("Error trying to parse the solr response json to a single SolrNode", e);
//            throw new UncheckedIOException("could not parse solr node", e);
//        }
//    }
//
//    @Override
//    public MetadataList getMetadataList(long nodeId) {
//
//    }
//
//    @Override
//    public String getTextContent(long nodeId) {
//        log.debug("Get solr text content {}", nodeId);
//        return restTemplate.getForObject(
//                url + "textContent?nodeId="+nodeId,
//                String.class);
//
//    }
//
//    @Override
//    public long getMaxTxnId() {
//        log.debug("Get max transaction id");
//        String response = restTemplate.getForObject(
//                url + "transactions?minTxnId=1&maxTxnId=1",
//                String.class
//        );
//
//        try {
//            return mapper.readValue(response, JsonNode.class).get("maxTxnId").asLong();
//        } catch (IOException e) {
//            log.error("Error trying to parse the solr response json to an int representing maximum transaction id");
//            throw new UncheckedIOException("could not parse the solr transactions object", e);
//        }
//    }

    @Override
    public AclChangeSets getAclChangeSets(Long fromCommitTime, Long minAclChangeSetId, Long toCommitTime,
            Long maxAclChangeSetId, int maxResults) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Acl> getAcls(List<AclChangeSet> aclChangeSets, Long minAclId, int maxResults) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<AclReaders> getAclReaders(List<Acl> acls) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SolrTransactions getTransactions(Long fromCommitTime, Long minTxnId, Long toCommitTime, Long maxTxnId,
            int maxResults) {
        log.debug("Get SolrTransactions [{},{}[", minTxnId, maxTxnId);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url + "transactions");
        conditionalQueryParam(uriBuilder, "fromCommitTime", fromCommitTime, Objects::nonNull);
        conditionalQueryParam(uriBuilder, "minTxnId", minTxnId, Objects::nonNull);
        conditionalQueryParam(uriBuilder, "toCommitTime", toCommitTime, Objects::nonNull);
        conditionalQueryParam(uriBuilder, "maxTxnId", maxTxnId, Objects::nonNull);
        conditionalQueryParam(uriBuilder, "maxResults", maxResults, val ->
                val != null && val != Integer.valueOf(0) && val != Integer.valueOf(Integer.MAX_VALUE));

        String response = restTemplate.getForObject(uriBuilder.toUriString(), String.class);
        log.info("response -> {}", response);
        try {
            return mapper.readValue(response, SolrTransactions.class);
        } catch (IOException e) {
            throw new UncheckedIOException("could not parse solr transaction json", e);
        }
    }

    @Override
    public List<SolrNode> getNodes(NodesQueryParameters parameters) {
        log.debug("Get solr nodes {}", parameters);
        String query = mapper.createObjectNode()
                .put("storeProtocol", "workspace")
                .put("storeIdentifier", "SpacesStore")
                .put("fromNodeId", parameters.fromNodeId())
                .put("toNodeId", parameters.toNodeId())
                .toString();

        String response = restTemplate.postForObject(
                url + "nodes",
                generateJsonHttpEntity(query),
                String.class);

        try {
            return mapper.readValue(response, SolrNodeList.class).getNodes();
        } catch (IOException e) {
            throw new UncheckedIOException("could not parse solr node", e);
        }
    }

    @Override
    public List<SolrNodeMetaData> getNodesMetaData(NodeMetaDataQueryParameters params) {
        log.debug("getNodesMetaData({})", params);
//        String query = mapper.createObjectNode()
//                .putPOJO("nodeIds", "[\"" + nodeId + "\"]")
//                .toString();
        try {
            String body = mapper.writeValueAsString(params);
            SolrNodeMetadataList result = restTemplate.postForObject(
                    url + "metadata",
                    generateJsonHttpEntity(body),
                    SolrNodeMetadataList.class);

            return result.getNodes();
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }

//        );
//
//        try {
//            return mapper.readValue(response, MetadataList.class);
//        } catch (IOException e) {
//            log.error("Error trying to parse the solr response json to a Metadata object", e);
//            throw new UncheckedIOException("could not parse solr metadata json", e);
//        }
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

    private UriComponentsBuilder conditionalQueryParam(UriComponentsBuilder builder, String key, Object value, Predicate<Object> condition) {
        Objects.requireNonNull(builder, "builder is required");
        Objects.requireNonNull(key, "key is required");
        if (value != null) {
            builder.queryParam(key, value);
        }
        return builder;
    }
}
