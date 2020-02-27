package eu.xenit.alfresco.restapi.client.spi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import java.util.Set;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Path {

    private List<Element> elements;
    private String name;
    private Boolean isComplete;

    @Data
    @Accessors(chain = true)
    public static class Element {

        private String id;
        private String name;
        private String nodeType;
        private Set<String> aspectNames;

    }

}
