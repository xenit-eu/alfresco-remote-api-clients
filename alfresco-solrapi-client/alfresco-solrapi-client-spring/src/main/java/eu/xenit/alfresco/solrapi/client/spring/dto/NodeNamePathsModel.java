package eu.xenit.alfresco.solrapi.client.spring.dto;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NodeNamePathsModel {

    List<String> namePath;

    public NodeNamePathsModel(Stream<String> namePaths) {
        this(namePaths.collect(Collectors.toList()));
    }

}
