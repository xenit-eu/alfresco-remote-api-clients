package eu.xenit.alfresco.restapi.client.spi.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Content {

    private String mimeType;
    private String mimeTypeName;
    private Long sizeInBytes;
    private String encoding;

}
