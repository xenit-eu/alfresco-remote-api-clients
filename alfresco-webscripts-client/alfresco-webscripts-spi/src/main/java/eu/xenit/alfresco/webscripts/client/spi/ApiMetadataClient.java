package eu.xenit.alfresco.webscripts.client.spi;

import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Data;

/**
 * See: ${host}/alfresco/service/index/package/org/alfresco/repository/metadata
 */
public interface ApiMetadataClient {

    Metadata get(String nodeRef);

    List<BulkMetadata> get(List<String> nodeRefs);

    @Data
    class Metadata {

        String nodeRef;
        Set<String> aspects;
        String mimetype;
        String type;
        Map<String, Object> properties;
    }

    @Data
    class BulkMetadata {

        String nodeRef;
        String parentNodeRef;
        String type;
        String shortType;
        String typeTitle;
        String name;
        String title;
        String mimeType;
        Boolean hasWritePermission;
        Boolean hasDeletePermission;

        String error;
        String errorCode;
        String errorText;
    }

}
