package eu.xenit.alfresco.webscripts.client.spi;

import java.util.Map;
import java.util.Set;
import lombok.Data;

public interface ApiMetadataClient {

    Metadata get(String nodeRef);

    @Data
    class Metadata {
        String nodeRef;
        Set<String> aspects;
        String mimetype;
        String type;
        Map<String, String> properties;
    }
}
