package eu.xenit.alfresco.restapi.client.spring;

import eu.xenit.alfresco.restapi.client.spi.NodesRestApiClient;
import eu.xenit.alfresco.restapi.client.spi.model.NodeCreateBody;
import eu.xenit.alfresco.restapi.client.spi.model.NodeEntry;
import eu.xenit.alfresco.restapi.client.spi.model.NodeList;
import eu.xenit.alfresco.restapi.client.spi.model.TargetAssociation;
import eu.xenit.alfresco.restapi.client.spi.model.TargetAssociationEntry;
import eu.xenit.alfresco.restapi.client.spi.model.exceptions.ApiException;
import eu.xenit.alfresco.restapi.client.spi.model.exceptions.ConstraintViolatedException;
import eu.xenit.alfresco.restapi.client.spi.model.exceptions.InvalidArgumentException;
import eu.xenit.alfresco.restapi.client.spi.model.exceptions.NotFoundException;
import eu.xenit.alfresco.restapi.client.spi.model.exceptions.PermissionDeniedException;
import eu.xenit.alfresco.restapi.client.spi.query.CreateNodeQueryParameters;
import eu.xenit.alfresco.restapi.client.spi.query.DeleteNodeQueryParameters;
import eu.xenit.alfresco.restapi.client.spi.query.DeleteTargetQueryParameters;
import eu.xenit.alfresco.restapi.client.spi.query.FilterQueryParameters;
import eu.xenit.alfresco.restapi.client.spi.query.GetNodeQueryParameters;
import eu.xenit.alfresco.restapi.client.spi.query.NodeQueryParameters;
import eu.xenit.alfresco.restapi.client.spi.query.PaginationQueryParameters;
import eu.xenit.alfresco.restapi.client.spi.query.QueryParameters;
import java.net.URI;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpClientErrorException.BadRequest;
import org.springframework.web.client.HttpClientErrorException.Conflict;
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
    public NodeList getChildren(String nodeId, PaginationQueryParameters paginationQueryParameters,
            FilterQueryParameters filterQueryParameters, NodeQueryParameters nodeQueryParameters) {
        URI uri = children(nodeId, paginationQueryParameters, filterQueryParameters, nodeQueryParameters);

        RequestEntity<Void> requestEntity = RequestEntity.get(uri).build();
        return execute(nodeId, requestEntity, NodeList.class);
    }

    @Override
    public NodeEntry createChild(String nodeId, NodeCreateBody nodeCreateBody,
            CreateNodeQueryParameters queryParameters) {
        RequestEntity<NodeCreateBody> requestEntity = RequestEntity.post(children(nodeId, queryParameters))
                .body(nodeCreateBody);

        return execute(nodeId, requestEntity, NodeEntry.class);
    }

    @Override
    public NodeList getSources(String nodeId, FilterQueryParameters filterQueryParameters,
            NodeQueryParameters nodeQueryParameters) {
        RequestEntity<Void> requestEntity = RequestEntity
                .get(uri(nodeId, "/sources", filterQueryParameters, nodeQueryParameters)).build();
        return execute(nodeId, requestEntity, NodeList.class);
    }

    @Override
    public NodeList getTargets(String nodeId, FilterQueryParameters filterQueryParameters,
            NodeQueryParameters nodeQueryParameters) {
        RequestEntity<Void> requestEntity = RequestEntity
                .get(targets(nodeId, filterQueryParameters, nodeQueryParameters)).build();
        return execute(nodeId, requestEntity, NodeList.class);
    }

    @Override
    public TargetAssociationEntry createTargetAssociation(String nodeId, TargetAssociation targetAssociation) {
        RequestEntity<TargetAssociation> requestEntity = RequestEntity.post(targets(nodeId))
                .body(targetAssociation);
        return execute(nodeId, requestEntity, TargetAssociationEntry.class);
    }

    @Override
    public void deleteTargetAssociation(String nodeId, String targetId, DeleteTargetQueryParameters queryParameters) {
        URI uri = uri(nodeId, "/targets/" + targetId, queryParameters);

        RequestEntity<Void> requestEntity = RequestEntity.delete(uri).build();
        execute(nodeId, requestEntity);
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
        } catch (Conflict e) {
            throw new ConstraintViolatedException(e);
        } catch (HttpClientErrorException e) {
            throw new ApiException(e);
        }
    }

    private URI children(String nodeId, QueryParameters... queryParameters) {
        return uri(nodeId, "/children", queryParameters);
    }

    private URI targets(String nodeId, QueryParameters... queryParameters) {
        return uri(nodeId, "/targets", queryParameters);
    }

    private URI uri(String nodeId, String path, QueryParameters... queryParameters) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url).path("/" + nodeId + path);
        if (queryParameters != null) {
            for (QueryParameters queryParameter : queryParameters) {
                withQueryParameters(uriBuilder, queryParameter);
            }
        }
        return uriBuilder.build().toUri();
    }


}
