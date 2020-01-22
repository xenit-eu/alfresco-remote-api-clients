package eu.xenit.alfresco.webscriptsapi.client.spi;

import eu.xenit.alfresco.webscriptsapi.client.spi.dto.Metadata;

public interface AlfrescoWebscriptsApiClient {
    Metadata getMetadata(String nodeRef);
}
