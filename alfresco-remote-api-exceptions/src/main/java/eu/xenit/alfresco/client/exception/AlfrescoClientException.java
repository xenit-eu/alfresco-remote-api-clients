package eu.xenit.alfresco.client.exception;

public class AlfrescoClientException extends RuntimeException {

    public AlfrescoClientException(String message) {
        super(message);
    }

    public AlfrescoClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
