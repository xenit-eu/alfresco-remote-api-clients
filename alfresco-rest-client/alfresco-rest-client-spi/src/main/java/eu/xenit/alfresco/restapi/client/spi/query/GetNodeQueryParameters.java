package eu.xenit.alfresco.restapi.client.spi.query;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class GetNodeQueryParameters implements QueryParameters {

    List<Include> includes = new ArrayList<>();

    String relativePath;

    List<String> fields = new ArrayList<>();


    @Override
    public Params queryParameters() {
        Params params = new Params();
        params.putIfNonEmpty("include", includes.stream().map(Include::getKey).collect(Collectors.toList()));
        params.putIfNonEmpty("relativePath", relativePath);
        params.putIfNonEmpty("relativePath", fields);
        return params;
    }

    public GetNodeQueryParameters withInclude(Include include) {
        includes.add(include);
        return this;
    }

    public enum Include {
        ALLOWED_OPERATIONS("allowableOperations"),
        ASSOCIATION("association"),
        IS_LINK("isLink"),
        IS_FAVORITE("isFavorite"),
        IS_LOCKED("isLocked"),
        PATH("path"),
        PERMISSIONS("permissions");

        Include(String key) {
            this.key = key;
        }

        private final String key;

        public String getKey() {
            return key;
        }
    }
}
