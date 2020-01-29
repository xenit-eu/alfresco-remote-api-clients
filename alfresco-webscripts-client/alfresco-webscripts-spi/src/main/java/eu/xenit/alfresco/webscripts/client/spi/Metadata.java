package eu.xenit.alfresco.webscripts.client.spi;

import lombok.Data;
import java.util.Map;
import java.util.Set;

@Data
public class Metadata {
    String nodeRef;
    Set<String> aspects;
    String mimetype;
    String type;
    Map<String, String> properties;
}
