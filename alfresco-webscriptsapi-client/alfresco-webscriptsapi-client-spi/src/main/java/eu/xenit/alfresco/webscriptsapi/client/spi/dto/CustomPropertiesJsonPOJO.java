package eu.xenit.alfresco.webscriptsapi.client.spi.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomPropertiesJsonPOJO {
    /**
     * A {@link LinkedHashMap}<{@link String},{@link Serializable}> that contains custom model properties
     */
    private Map<String, Serializable> customProperties = new LinkedHashMap<>();

    @JsonAnySetter
    public CustomPropertiesJsonPOJO addCustomProperty(String key, Serializable value) {
        this.customProperties.put(key, value);
        return this;
    }
}
