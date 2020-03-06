package eu.xenit.alfresco.solrapi.client.spi.dto;

import java.util.List;
import lombok.Data;

@Data
public class AclList {

    private List<Acl> acls;
}

