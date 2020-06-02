package eu.xenit.alfresco.solrapi.client.spring.dto;

import java.util.List;
import lombok.Data;

@Data
public class AclListModel {

    private List<AclModel> acls;
}

