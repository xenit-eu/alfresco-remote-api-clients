package eu.xenit.alfresco.api.client.spi;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Metadata {
    private String nodeRef;
    private String type;
    private String mimetype;
    private Properties properties;
    private List<String> aspects;
}
