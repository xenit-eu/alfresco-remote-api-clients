package eu.xenit.alfresco.restapi.client.spring;

import eu.xenit.alfresco.restapi.client.spi.NodesRestApiClient;
import eu.xenit.alfresco.restapi.client.tests.NodesRestApiIntegrationTests;

public class NodesRestApiSpringIntegrationTests extends RestApiIntegrationTests
        implements NodesRestApiIntegrationTests {

    @Override
    public NodesRestApiClient nodesRestApiClient() {
        return new NodesRestApiSpringClient(alfrescoRestProperties());
    }
}
