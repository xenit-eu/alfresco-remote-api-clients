package eu.xenit.alfresco.webscripst.client.ditto;

import eu.xenit.alfresco.webscripts.client.spi.NodeLocatorClient;
import eu.xenit.testing.ditto.api.AlfrescoDataSet;
import eu.xenit.testing.ditto.api.NodeView;
import eu.xenit.testing.ditto.api.model.Node;

import java.util.HashMap;
import java.util.List;
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
    public String get(String locatorName, Map<String, List<String>> params) {

        if (Locator.COMPANY_HOME.getValue().equals(locatorName)) {
            return this.nodeView.getCompanyHome().map(n -> n.getNodeRef().toString()).orElse(null);
        }

        String msg = String.format("Locator %s is not supported", locatorName);
        throw new RuntimeException(msg);
    }

}
