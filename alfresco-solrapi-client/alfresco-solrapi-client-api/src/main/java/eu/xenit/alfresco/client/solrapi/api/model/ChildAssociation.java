package eu.xenit.alfresco.client.solrapi.api.model;

import lombok.NonNull;
import lombok.Value;

@Value
public class ChildAssociation {

    /**
     * The node reference of the parent of the association.
     *
     * This is null for a root node.
     */
    private String parentRef;

    /**
     * The node reference of the child of the association. This is never null.
     */
    @NonNull
    private String childRef;

    /**
     * The qualified name of the association type,
     */
    private String assocTypeQName;

    /**
     * The qualified name of the parent-child association.
     */
    private String childQName;

    /**
     * True for primary associations.
     */
    private boolean isPrimary;

    /**
     * Enables support for post-creation ordering. This is commonly used. Defaults to -1.
     */
    private int index;
}

