package eu.xenit.alfresco.solrapi.client.tests.ditto;

import eu.xenit.testing.ditto.api.model.Node;
import eu.xenit.testing.ditto.api.model.Transaction;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TestTransaction implements Transaction {

    private final long id;

    private String changeId;
    private long version;
    private long serverId;
    private long commitTimeMs;

    Set<Node> updated = new LinkedHashSet<>();
    Set<Node> deleted = new LinkedHashSet<>();

    public TestTransaction addUpdated(Node node) {
        this.updated.add(node);
        return this;
    }

    public TestTransaction addDeleted(Node node) {
        this.deleted.add(node);
        return this;
    }


}
