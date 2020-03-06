package eu.xenit.alfresco.webscripts.client.spi;

import java.io.OutputStream;

/**
 * Client for the Alfresco "${host}/alfresco/service/api/node/content/" endpoints
 * <p>
 * See: ${host}/alfresco/service/index/package/org/alfresco/content
 */
public interface ApiNodeContentClient {

    boolean hasContent(String nodeRef);

    default void getContent(String nodeRef, OutputStream outputStream) {
        getContent(nodeRef, null, outputStream);
    }

    void getContent(String nodeRef, String contentProperty, OutputStream outputStream);

}
