package eu.xenit.alfresco.restapi.client.spi.query;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DeleteTargetQueryParameters implements QueryParameters {

    private String assocType;

    @Override
    public Params queryParameters() {
        return new Params().putIfNonEmpty("assocType", assocType);
    }
}
