package eu.xenit.alfresco.webscripts.client.spi;

import eu.xenit.alfresco.webscripts.client.spi.model.slingshot.Metadata;

public interface SlingShotClient {

    Metadata get(String nodeRef);

}
