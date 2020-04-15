package eu.xenit.alfresco.restapi.client.spi.query;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class CreateNodeQueryParameters extends NodeQueryParameters<CreateNodeQueryParameters> {

    private Boolean autoRename;

    public CreateNodeQueryParameters setAutoRename(Boolean autoRename) {
        this.autoRename = autoRename;
        return self();
    }

    @Override
    public Params queryParameters() {
        Params ret = super.queryParameters();
        if (autoRename != null) {
            ret.putIfNonEmpty("autoRename", autoRename.toString());
        }
        return ret;
    }
}
