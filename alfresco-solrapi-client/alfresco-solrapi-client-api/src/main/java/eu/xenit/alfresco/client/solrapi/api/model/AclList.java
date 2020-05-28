package eu.xenit.alfresco.client.solrapi.api.model;

import java.util.List;
import lombok.Data;
import lombok.Value;

@Value
public class AclList {

    private List<Acl> acls;
}

