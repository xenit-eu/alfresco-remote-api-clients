package eu.xenit.alfresco.webscripts.client.spi;

import java.util.List;
import lombok.Data;

public interface SlingshotClient {

    Metadata get(String nodeRef);

    @Data
    class Metadata {
        String nodeRef;
        NameContainer qnamePath;
        NameContainer name;
        String parentNodeRef;
        NameContainer type;
        String id;
        List<NameContainer> aspects;
        List<Property> properties;
        List<Parent> children;
        List<Parent> parents;
        List<Association> assocs;
        List<Association> sourceAssocs;
        Permissions permissions;
    }

    @Data
    class NameContainer {
        String name;
        String prefixedName;
    }

    @Data
    class Property {
        NameContainer name;
        ValueContainer value;
        NameContainer type;
        boolean multiple;
        boolean residual;
    }

    @Data
    class ValueContainer {
        String dataType;
        String value;
        boolean isContent;
        boolean isNodeRef;
        boolean isNullValue;
    }

    @Data
    class Parent {
        NameContainer name;
        String nodeRef;
        NameContainer type;
        NameContainer assocType;
        boolean primary;
    }

    @Data
    class Association {
        NameContainer type;
        String sourceRef;
        String targetRef;
        NameContainer assocType;
    }

    @Data
    class Permissions {
        List<Permission> entries;
        List<Permission> masks;
        boolean inherit;
        String owner;
    }

    @Data
    class Permission {
        String permission;
        String authority;
        String rel;
    }

}
