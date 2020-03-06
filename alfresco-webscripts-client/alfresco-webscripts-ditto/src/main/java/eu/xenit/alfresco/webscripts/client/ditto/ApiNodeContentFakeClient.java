package eu.xenit.alfresco.webscripts.client.ditto;

import eu.xenit.alfresco.webscripts.client.spi.ApiNodeContentClient;
import eu.xenit.testing.ditto.api.AlfrescoDataSet;
import eu.xenit.testing.ditto.api.ContentView;
import eu.xenit.testing.ditto.api.NodeView;
import eu.xenit.testing.ditto.api.model.ContentData;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.Map.Entry;
import org.apache.commons.io.IOUtils;

public class ApiNodeContentFakeClient implements ApiNodeContentClient {

    private final NodeView nodeView;
    private final ContentView contentView;

    public ApiNodeContentFakeClient(AlfrescoDataSet dataSet) {
        this(dataSet.getNodeView(), dataSet.getContentView());
    }

    public ApiNodeContentFakeClient(NodeView nodeView, ContentView contentView) {
        this.nodeView = nodeView;
        this.contentView = contentView;
    }

    @Override
    public boolean hasContent(String nodeRef) {
        if (!this.nodeView.getNode(nodeRef).isPresent()) {
            return false;
        }
        return this.nodeView.getNode(nodeRef).get()
                .getProperties().getContentData().isPresent();
    }

    @Override
    public void getContent(String nodeRef, String contentProperty, OutputStream outputStream) {
        final String finalContentProp = (contentProperty == null) ? "cm:content" : contentProperty;

        if (finalContentProp.contains("{")) {
            throw new IllegalArgumentException("Only short QNames supported: " + contentProperty);
        }

        if (!this.nodeView.getNode(nodeRef).isPresent()) {
            throw new RuntimeException("Node not available: " + nodeRef);
        }

        ContentData value = (ContentData) this.nodeView.getNode(nodeRef).get().getProperties().stream()
                .filter(entry -> finalContentProp.equals(entry.getKey().toPrefixString()))
                .map(Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "Property '" + contentProperty + "' not found for node: " + nodeRef));

        try (InputStream inputStream = contentView.getContent(value.getContentUrl())
                .orElseThrow(() -> new IllegalStateException("Node " + nodeRef + " does not exist"))) {
            IOUtils.copy(inputStream, outputStream);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
