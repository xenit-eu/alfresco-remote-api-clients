package eu.xenit.alfresco.webscripst.client.ditto;

import eu.xenit.alfresco.webscripts.client.spi.NodeLocatorClient;
import eu.xenit.testing.ditto.api.AlfrescoDataSet;
import eu.xenit.testing.ditto.api.NodeView;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodeLocatorFakeClient implements NodeLocatorClient {

    private final NodeView nodeView;

    public NodeLocatorFakeClient(AlfrescoDataSet dataSet) {
        this(dataSet.getNodeView());
    }

    public NodeLocatorFakeClient(NodeView nodeView) {
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
