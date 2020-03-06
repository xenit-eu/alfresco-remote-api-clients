package eu.xenit.alfresco.webscripts.client.ditto;

import eu.xenit.alfresco.webscripts.client.spi.ApiNodeContentClient;
import eu.xenit.testing.ditto.api.AlfrescoDataSet;
import eu.xenit.testing.ditto.api.ContentView;
import eu.xenit.testing.ditto.api.NodeView;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
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
        if (!"cm:content".equals(contentProperty)) {
            throw new UnsupportedOperationException("Only cm:content supported at the moment");
        }

        if (!this.nodeView.getNode(nodeRef).isPresent() ||
                !this.nodeView.getNode(nodeRef).get().getProperties().getContentData().isPresent()) {
            throw new RuntimeException("Node not available or has no content: " + nodeRef);
        }

        try (InputStream inputStream = contentView.getContent(nodeRef)
                .orElseThrow(() -> new IllegalStateException("Node " + nodeRef + " does not exist"))) {
            IOUtils.copy(inputStream, outputStream);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
