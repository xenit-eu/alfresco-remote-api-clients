package eu.xenit.alfresco.solrapi.client.spring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import eu.xenit.alfresco.client.api.exception.ResourceNotFoundException;
import eu.xenit.alfresco.restapi.client.spi.Constants;
import eu.xenit.alfresco.restapi.client.spi.NodesRestApiClient;
import eu.xenit.alfresco.restapi.client.spi.model.NodeCreateBody;
import eu.xenit.alfresco.restapi.client.spi.model.NodeEntry;
import eu.xenit.alfresco.restapi.client.spi.query.CreateNodeQueryParameters;
import eu.xenit.alfresco.restapi.client.spring.AlfrescoRestProperties;
import eu.xenit.alfresco.restapi.client.spring.NodesRestApiSpringClient;
import eu.xenit.alfresco.solrapi.client.tests.GetMetadataNoLiveNodeExistsReproduction;
import eu.xenit.alfresco.webscripts.client.spi.ApiMetadataClient;
import eu.xenit.alfresco.webscripts.client.spi.ApiVersionClient;
import eu.xenit.alfresco.webscripts.client.spi.ApiVersionClient.Version;
import eu.xenit.alfresco.webscripts.client.spring.ApiMetadataSpringClient;
import eu.xenit.alfresco.webscripts.client.spring.ApiVersionSpringClient;
import eu.xenit.alfresco.webscripts.client.spring.model.AlfrescoProperties;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;

public class SpringClientNoLiveNodeExistsReproduction extends SolrApiSpringClientIntegrationTest
        implements GetMetadataNoLiveNodeExistsReproduction {

    private NodesRestApiClient nodesRestApiClient;
    private ApiMetadataClient apiMetadataClient;
    private ApiVersionClient apiVersionClient;

    @BeforeEach
    void setup() {
        nodesRestApiClient = new NodesRestApiSpringClient(alfrescoRestProperties());
        apiMetadataClient = new ApiMetadataSpringClient(alfrescoProperties());
        apiVersionClient = new ApiVersionSpringClient(alfrescoProperties());
    }

    @Override
    public String createVersionableNode(String name) {
        NodeCreateBody nodeCreateBody = new NodeCreateBody(name, "cm:content").withAspect("cm:versionable");

        NodeEntry created = nodesRestApiClient.createChild(Constants.Node.ROOT, nodeCreateBody,
                new CreateNodeQueryParameters().setAutoRename(true));
        assertThat(created).isNotNull().extracting(NodeEntry::getEntry).isNotNull();

        return created.getEntry().getId();
    }

    @Override
    public List<Long> getVersionNodeIds(String liveNodeUuid) {
        return apiVersionClient.getVersions("workspace://SpacesStore/" + liveNodeUuid).stream()
                .map(Version::getNodeRef)
                .map(nodeRef -> nodeRef.replace("versionStore://", "workspace://"))
                .map(nodeRef -> apiMetadataClient.get(nodeRef))
                .map(metadata -> metadata.getProperties().get("{http://www.alfresco.org/model/system/1.0}node-dbid"))
                .map(object -> (int) object)
                .map(Integer::toUnsignedLong)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteNode(String liveNodeUuid) {
        nodesRestApiClient.delete(liveNodeUuid);
        assertThrows(ResourceNotFoundException.class, () -> nodesRestApiClient.get(liveNodeUuid));
    }

    private AlfrescoRestProperties alfrescoRestProperties() {
        return AlfrescoRestProperties.builder()
                .host(alfrescoHost())
                .port(alfrescoPort())
                .build();
    }

    private AlfrescoProperties alfrescoProperties() {
        return new AlfrescoProperties()
                .setUrl("http://" + alfrescoHost() + ":" + alfrescoPort() + "/alfresco/");
    }

    private static String alfrescoHost() {
        return System.getProperty("alfresco.host", "localhost");
    }

    private static Integer alfrescoPort() {
        return Integer.parseInt(System.getProperty("alfresco.tcp.8080", "8080"));
    }
}
