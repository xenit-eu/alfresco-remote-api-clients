package eu.xenit.alfresco.restapi.client.spring;

import eu.xenit.alfresco.restapi.client.spi.VersionsRestApiClient;
import eu.xenit.alfresco.restapi.client.tests.VersionsRestApiIntegrationTests;

public class VersionsRestApiSpringIntegrationTests extends RestApiIntegrationTests
        implements VersionsRestApiIntegrationTests {

    @Override
    public VersionsRestApiClient versionsRestApiClient() {
        return new VersionsRestApiSpringClient(alfrescoRestProperties());
    }
}
