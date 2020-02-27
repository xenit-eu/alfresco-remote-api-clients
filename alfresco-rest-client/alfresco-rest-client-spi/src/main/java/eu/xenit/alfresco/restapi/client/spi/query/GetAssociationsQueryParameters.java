package eu.xenit.alfresco.restapi.client.spi.query;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class GetAssociationsQueryParameters extends GetNodeQueryParameters {

    private String where;

    public GetAssociationsQueryParameters filterOnAssocType(String assocType) {
        this.where = "(assocType='" + assocType + "')";
        return this;
    }

    @Override
    public Params queryParameters() {
        Params ret = super.queryParameters();
        ret.putIfNonEmpty("where", where);
        return ret;
    }
}
