package eu.xenit.alfresco.restapi.client.spi.model;

import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
public class NodeList {

    private NodeChildAssociations list;

    @Data
    @Accessors(chain = true)
    public static class NodeChildAssociations {

        private Pagination pagination;
        private List<NodeEntry> entries;
        private NodeEntry source;

    }

}
