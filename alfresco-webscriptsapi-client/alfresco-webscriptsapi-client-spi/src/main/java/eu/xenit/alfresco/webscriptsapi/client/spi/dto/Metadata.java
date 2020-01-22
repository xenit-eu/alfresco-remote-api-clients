package eu.xenit.alfresco.webscriptsapi.client.spi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Metadata {
    private String nodeRef;
    private String type;
    private String mimetype;
    private Properties properties;
    private List<String> aspects;
}
