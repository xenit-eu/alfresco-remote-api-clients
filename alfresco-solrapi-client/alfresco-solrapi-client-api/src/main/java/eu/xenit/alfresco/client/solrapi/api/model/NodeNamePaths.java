package eu.xenit.alfresco.client.solrapi.api.model;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NodeNamePaths {

    List<String> namePath;

    public NodeNamePaths(Stream<String> namePaths) {
        this(namePaths.collect(Collectors.toList()));
    }

}
