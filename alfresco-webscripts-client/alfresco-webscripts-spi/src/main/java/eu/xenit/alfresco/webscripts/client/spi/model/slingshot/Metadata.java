package eu.xenit.alfresco.webscripts.client.spi.model.slingshot;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class Metadata {

    private String nodeRef;
    private NameContainer qnamePath;
    private NameContainer name;
    private String parentNodeRef;
    private NameContainer type;
    private String id;
    private List<NameContainer> aspects;
    private List<Property> properties;
    private List<Parent> children;
    private List<Parent> parents;
    private List<Association> assocs;
    private List<Association> sourceAssocs;
    private Permissions permissions;


    @Data
    @AllArgsConstructor
    public static class NameContainer {
        String name;
        String prefixedName;
    }

    @Data
    @AllArgsConstructor
    public static class Property {
        NameContainer name;
        ValueContainer value;
        NameContainer type;
        boolean multiple;
        boolean residual;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValueContainer {
        String dataType;
        String value;
        boolean isContent;
        boolean isNodeRef;
        boolean isNullValue;
    }

    @Data
    public static class Parent {
        NameContainer name;
        String nodeRef;
        NameContainer type;
        NameContainer assocType;
        boolean primary;
        int index;
    }

    @Data
    @AllArgsConstructor
    public static class Association {
        NameContainer type; // child type | target type
        String sourceRef;
        String targetRef;
        NameContainer assocType; // association type
    }

    @Data
    public class Permissions {
        List<Permission> entries;
        List<Permission> masks;
        boolean inherit;
        String owner;
    }

    @Data
    public class Permission {
        String permission;
        String authority;
        String rel;
    }
}
