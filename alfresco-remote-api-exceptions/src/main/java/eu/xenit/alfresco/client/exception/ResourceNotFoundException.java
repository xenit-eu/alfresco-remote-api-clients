package eu.xenit.alfresco.client.exception;

public class ResourceNotFoundException extends HttpStatusException {

    public ResourceNotFoundException(String resourceType, String resourceId) {
        super(StatusCode.NOT_FOUND, getMessage(resourceType, resourceId));
    }

    public ResourceNotFoundException(String resourceId) {
        super(StatusCode.NOT_FOUND, getMessage(null, resourceId));
    }

    private static String getMessage(String resourceType, String resourceId) {
        if (resourceType == null || resourceType.trim().isEmpty()) {
            resourceType = "Resource";
        }
        return resourceType + " with ID " + resourceId + " not found";
    }
}
