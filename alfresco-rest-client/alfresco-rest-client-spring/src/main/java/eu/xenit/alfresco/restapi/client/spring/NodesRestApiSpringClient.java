package eu.xenit.alfresco.restapi.client.spring;

import eu.xenit.alfresco.restapi.client.spi.NodesRestApiClient;
import eu.xenit.alfresco.restapi.client.spi.model.NodeChildAssociationsList;
import eu.xenit.alfresco.restapi.client.spi.model.NodeEntry;
import eu.xenit.alfresco.restapi.client.spi.model.exceptions.ApiException;
import eu.xenit.alfresco.restapi.client.spi.model.exceptions.InvalidArgumentException;
import eu.xenit.alfresco.restapi.client.spi.model.exceptions.NotFoundException;
import eu.xenit.alfresco.restapi.client.spi.model.exceptions.PermissionDeniedException;
import eu.xenit.alfresco.restapi.client.spi.query.CreateNodeQueryParameters;
import eu.xenit.alfresco.restapi.client.spi.query.DeleteNodeQueryParameters;
import eu.xenit.alfresco.restapi.client.spi.query.GetNodeQueryParameters;
import eu.xenit.alfresco.restapi.client.spi.query.NodeCreateBody;
import eu.xenit.alfresco.restapi.client.spi.query.PaginationQueryParameters;
import java.net.URI;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpClientErrorException.BadRequest;
import org.springframework.web.client.HttpClientErrorException.Forbidden;
import org.springframework.web.client.HttpClientErrorException.NotFound;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;
import org.springframework.web.util.UriComponentsBuilder;

public class NodesRestApiSpringClient extends RestApiSpringClient implements NodesRestApiClient {

    public NodesRestApiSpringClient(AlfrescoRestProperties alfrescoRestProperties) {
        super(alfrescoRestProperties);
    }

    @Override
    protected String getApiBasePath() {
        return "/nodes";
    }

    @Override
    public void delete(String nodeId, DeleteNodeQueryParameters queryParameters) {
        URI uri = withQueryParameters(UriComponentsBuilder
                .fromHttpUrl(url).path("/" + nodeId), queryParameters)
                .build().toUri();

        RequestEntity<Void> requestEntity = RequestEntity.delete(uri).build();
        execute(nodeId, requestEntity);
    }

    @Override
    public NodeEntry get(String nodeId, GetNodeQueryParameters queryParameters) {
        URI uri = withQueryParameters(UriComponentsBuilder
                .fromHttpUrl(url).path("/" + nodeId), queryParameters)
                .build().toUri();

        RequestEntity<Void> requestEntity = RequestEntity.get(uri).build();
        return execute(nodeId, requestEntity, NodeEntry.class);
    }

    @Override
    public NodeChildAssociationsList getChildren(String nodeId, PaginationQueryParameters paginationQueryParameters,
            GetNodeQueryParameters nodeQueryParameters) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url).path("/" + nodeId + "/children");
        withQueryParameters(uriBuilder, paginationQueryParameters);
        withQueryParameters(uriBuilder, nodeQueryParameters);

        RequestEntity<Void> requestEntity = RequestEntity.get(uriBuilder.build().toUri()).build();
        return execute(nodeId, requestEntity, NodeChildAssociationsList.class);
    }

    @Override
    public NodeEntry createChild(String nodeId, NodeCreateBody nodeCreateBody,
            CreateNodeQueryParameters queryParameters) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url).path("/" + nodeId + "/children");
        withQueryParameters(uriBuilder, queryParameters);

        RequestEntity<NodeCreateBody> requestEntity =
                RequestEntity.post(uriBuilder.build().toUri()).body(nodeCreateBody);

        return execute(nodeId, requestEntity, NodeEntry.class);
    }

    private <T> void execute(String nodeId, RequestEntity<T> requestEntity) {
        execute(nodeId, requestEntity, Void.class);
    }

    private <T, R> R execute(String nodeId, RequestEntity<T> requestEntity, Class<R> responseClass) {

        try {
            ResponseEntity<R> responseEntity = restTemplate.exchange(requestEntity, responseClass);
            return responseEntity.getBody();
        } catch (BadRequest e) {
            throw new InvalidArgumentException(nodeId);
        } catch (Unauthorized | Forbidden e) {
            throw new PermissionDeniedException(nodeId);
        } catch (NotFound e) {
            throw new NotFoundException(nodeId);
        } catch (HttpClientErrorException e) {
            throw new ApiException(e);
        }
    }


}
