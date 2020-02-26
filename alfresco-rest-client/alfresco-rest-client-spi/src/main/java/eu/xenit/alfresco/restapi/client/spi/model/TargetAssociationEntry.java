package eu.xenit.alfresco.restapi.client.spi.model;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TargetAssociationEntry {

    private TargetAssociation entry;

}
