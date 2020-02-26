package eu.xenit.alfresco.restapi.client.spi.query;

import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PaginationQueryParameters implements QueryParameters {

    private int skipCount = 0;
    private int maxItems = 100;
    private List<String> orderBy;
    private String where;

    @Override
    public Params queryParameters() {
        Params ret = new Params();
        ret.put("skipCount", Integer.toString(skipCount));
        ret.put("maxItems", Integer.toString(maxItems));
        ret.putIfNonEmpty("orderBy", orderBy);
        ret.putIfNonEmpty("where", where);
        return ret;
    }
}
