package eu.xenit.alfresco.solrapi.client.tests.ditto;

import eu.xenit.testing.ditto.api.data.ContentModel.Content;
import eu.xenit.testing.ditto.api.model.Node;
import eu.xenit.testing.ditto.api.model.NodeProperties;
import eu.xenit.testing.ditto.api.model.NodeReference;
import eu.xenit.testing.ditto.api.model.QName;
import eu.xenit.testing.ditto.internal.DefaultNodeProperties;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
public class TestNode implements Node {

    private final long nodeId;
    @Accessors(chain = true)
    private long txnId;
    private final QName type;

    private NodeReference nodeRef = NodeReference.newNodeRef();

    private NodeProperties properties = new DefaultNodeProperties();
    private Set<QName> aspects;

    @Override
    public String getName() {
        String name = (String) this.properties.get(Content.NAME);
        return name != null ? name : nodeRef.getUuid();
    }

    public TestNode withProperty(QName key, Serializable value) {
        this.getProperties().put(key, value);
        return this;
    }

    public TestNode withAspects(QName... aspects) {
        this.aspects = new HashSet<>(Arrays.asList(aspects));
        return this;
    }
}
