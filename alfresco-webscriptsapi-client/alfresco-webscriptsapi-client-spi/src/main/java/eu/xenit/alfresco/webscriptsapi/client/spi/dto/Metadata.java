package eu.xenit.alfresco.webscriptsapi.client.spi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Metadata extends CustomPropertiesJsonPOJO {
    private String nodeRef;
    private String type;
    private String mimetype;
    @JsonProperty(value = "properties")
    private Properties metadataProperties;
    private List<String> aspects;
}
