package eu.xenit.alfresco.webscripst.client.ditto;

import eu.xenit.alfresco.webscripts.client.spi.NodeLocatorClient;
import eu.xenit.testing.ditto.api.AlfrescoDataSet;
import eu.xenit.testing.ditto.api.NodeView;
import eu.xenit.testing.ditto.api.model.Node;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class NodeLocatorDittoClient implements NodeLocatorClient {

    private final NodeView nodeView;

    public NodeLocatorDittoClient(AlfrescoDataSet dataSet) {
        this(dataSet.getNodeView());
    }

    public NodeLocatorDittoClient(NodeView nodeView) {
        this.nodeView = nodeView;
    }

    @Override
    public String get(String locatorName) {
        return get(locatorName, new HashMap<>());
    }

    @Override
    public String get(String locatorName, Map<String, String> params) {
        if(Objects.equals(locatorName, Locator.COMPANY_HOME.getValue())) {
            Optional<Node> dittoNode = this.nodeView.stream()
                                                    .filter(this::filterCompanyHome)
                                                    .findFirst();
            return dittoNode.map(node -> node.getNodeRef().toString()).orElse(null);
        }
        return null;
    }

    private boolean filterCompanyHome(Node node) {
        return node.getName().equals("Company Home")
            && node.getNodeId() == 13L;
    }
}
