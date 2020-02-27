package eu.xenit.alfresco.restapi.client.spi.model;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TargetAssociation {

    private String targetId;
    private String assocType;

}
