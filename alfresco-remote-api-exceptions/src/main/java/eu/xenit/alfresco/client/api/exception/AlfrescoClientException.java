package eu.xenit.alfresco.client.api.exception;

public class AlfrescoClientException extends RuntimeException {

    public AlfrescoClientException(String message) {
        super(message);
    }

    public AlfrescoClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
