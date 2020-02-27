package eu.xenit.alfresco.restapi.client.spi.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Node {

    private String id;
    private String name;
    private String nodeType;
    private Boolean isFolder;
    private Boolean isFile;
    private Boolean isLocked;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Date modifiedAt;
    private UserInfo modifiedByUser;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Date createdAt;
    private UserInfo createdByUser;
    private String parentId;
    private Boolean isLink;
    private Boolean isFavorite;
    private Content content;
    private Set<String> aspectNames;
    private Map<String, Object> properties;
    private List<String> allowedOperations;
    private Path path;
    private Permissions permissions;

}
