package eu.xenit.alfresco.restapi.client.spi.model;

import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TargetAssociationList {

    private TargetAssociations list;

    @Data
    @Accessors(chain = true)
    public static class TargetAssociations {

        private Pagination pagination;
        private List<TargetAssociationEntry> entries;

    }

}
