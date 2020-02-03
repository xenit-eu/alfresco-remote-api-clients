package eu.xenit.alfresco.solrapi.client.tests.ditto;

import eu.xenit.testing.ditto.api.data.ContentModel.Content;
import eu.xenit.testing.ditto.api.model.Node;
import eu.xenit.testing.ditto.api.model.NodeReference;
import eu.xenit.testing.ditto.api.model.ParentChildAssoc;
import eu.xenit.testing.ditto.api.model.ParentChildNodeCollection;
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

    private DefaultNodeProperties properties = new DefaultNodeProperties();
    private Set<QName> aspects;

    private ParentChildAssoc primaryParentAssoc;
    private QName qName;

    @Override
    public String getName() {
        return this.properties.get(Content.NAME).map(Object::toString).orElseGet(() -> nodeRef.getUuid());
    }

    @Override
    public ParentChildNodeCollection getChildNodeCollection() {
        return null;
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
