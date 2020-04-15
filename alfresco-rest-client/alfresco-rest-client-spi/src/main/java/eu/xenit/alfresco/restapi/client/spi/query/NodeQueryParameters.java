package eu.xenit.alfresco.restapi.client.spi.query;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class NodeQueryParameters<SELF extends NodeQueryParameters<SELF>> implements QueryParameters {

    List<Include> includes = new ArrayList<>();

    List<String> fields = new ArrayList<>();

    @SuppressWarnings("unchecked")
    protected SELF self() {
        return (SELF) this;
    }

    @Override
    public Params queryParameters() {
        Params params = new Params();
        params.putIfNonEmpty("include", includes.stream().map(Include::getKey).collect(Collectors.toList()));
        params.putIfNonEmpty("relativePath", fields);
        return params;
    }

    public SELF withInclude(Include include) {
        includes.add(include);
        return self();
    }

    public SELF withAllIncludes() {
        for (Include value : Include.values()) {
            withInclude(value);
        }
        return self();
    }

    public enum Include {
        ALLOWED_OPERATIONS("allowableOperations"),
        ASPECT_NAMES("aspectNames"),
        ASSOCIATION("association"),
        IS_LINK("isLink"),
        IS_FAVORITE("isFavorite"),
        IS_LOCKED("isLocked"),
        PATH("path"),
        PROPERTIES("properties"),
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
