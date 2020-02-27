package eu.xenit.alfresco.restapi.client.spi.query;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DeleteNodeQueryParameters implements QueryParameters {

    private boolean permanent = false;

    @Override
    public Params queryParameters() {
        return new Params().putIfNonEmpty("permanent", Boolean.toString(permanent));
    }

}
