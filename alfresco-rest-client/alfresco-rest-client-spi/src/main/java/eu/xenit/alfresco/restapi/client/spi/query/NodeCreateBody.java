package eu.xenit.alfresco.restapi.client.spi.query;

import eu.xenit.alfresco.restapi.client.spi.model.Permissions;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class NodeCreateBody {

    private final String name;
    private final String nodeType;
    private Set<String> aspectNames = new HashSet<>();
    private Map<String, Object> properties = new HashMap<>();
    private Permissions permissions;
    private String relativePath;

    public NodeCreateBody withAspect(String aspect) {
        aspectNames.add(aspect);
        return this;
    }

    public NodeCreateBody withProperty(String key, Object value) {
        properties.put(key, value);
        return this;
    }

    // TODO association
    // TODO secondaryChildren
    // TODO targets

}
