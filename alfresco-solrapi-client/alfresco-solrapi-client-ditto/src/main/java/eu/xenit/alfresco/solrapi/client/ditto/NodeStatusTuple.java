package eu.xenit.alfresco.solrapi.client.ditto;

import eu.xenit.alfresco.client.solrapi.api.model.SolrNode;
import eu.xenit.testing.ditto.api.model.Node;
import lombok.Data;

@Data
class NodeStatusTuple {

    final Node node;
    final String status;
    final long txnId;

    SolrNode getSolrNode() {
        return new SolrNode(
                node.getNodeId(), node.getNodeRef().toString(),
                txnId, status, "", -1, "");
    }
}
