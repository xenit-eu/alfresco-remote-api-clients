package eu.xenit.alfresco.restapi.client.spi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Permissions {

    private Boolean isInheritanceEnabled;
    private List<Inherited> inherited;
    private List<LocallySet> locallySet;
    private List<String> settable;

    @Data
    @Accessors(chain = true)
    public static class Inherited {

        private String authorityId;
        private String name;
        private AccessStatus accessStatus;
    }

    @Data
    @Accessors(chain = true)
    public static class LocallySet {

        private String authorityId;
        private String name;
        private AccessStatus accessStatus;
    }

    public enum AccessStatus {
        ALLOWED,
        DENIED
    }

}
