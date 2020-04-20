package eu.xenit.alfresco.restapi.client.spi.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetNodeQueryParameters extends NodeQueryParameters<GetNodeQueryParameters> {

    String relativePath;

    public GetNodeQueryParameters setRelativePath(String relativePath) {
        this.relativePath = relativePath;
        return self();
    }

    @Override
    public Params queryParameters() {
        return super.queryParameters().putIfNonEmpty("relativePath", relativePath);
    }
}
