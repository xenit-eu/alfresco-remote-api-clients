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

    @Override
    public Params queryParameters() {
        return new Params()
                .putIfNonEmpty("skipCount", Integer.toString(skipCount))
                .putIfNonEmpty("maxItems", Integer.toString(maxItems))
                .putIfNonEmpty("orderBy", orderBy);
    }
}
