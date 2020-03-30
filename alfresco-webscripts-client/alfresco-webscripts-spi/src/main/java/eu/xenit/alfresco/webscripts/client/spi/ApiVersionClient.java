package eu.xenit.alfresco.webscripts.client.spi;

import java.util.List;
import lombok.Data;

/**
 * See: ${host}/alfresco/service/index/package/org/alfresco/repository/version
 */
public interface ApiVersionClient {

    List<Version> getVersions(String nodeRef);

    @Data
    class Version {

        String nodeRef;
        String name;
        String label;
        String description;
        String createdDate;
        String createdDateISO;
        Creator creator;
    }

    @Data
    class Creator {

        String userName;
        String firstName;
        String lastName;
    }

}
