package eu.xenit.alfresco.restapi.client.spi.query;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class GetNodeQueryParameters extends NodeQueryParameters {

    String relativePath;

    @Override
    public Params queryParameters() {
        return super.queryParameters().putIfNonEmpty("relativePath", relativePath);
    }
}
