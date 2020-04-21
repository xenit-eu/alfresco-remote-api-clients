package eu.xenit.alfresco.client.exception;

public class HttpStatusException extends AlfrescoClientException {

    public HttpStatusException(StatusCode statusCode, String statusText) {
        super(getMessage(statusCode, statusText));
    }

    public HttpStatusException(StatusCode statusCode, String statusText, Throwable cause) {
        super(getMessage(statusCode, statusText), cause);
    }

    private static String getMessage(StatusCode statusCode, String statusText) {
        if (statusText == null || statusText.trim().isEmpty()) {
            statusText = statusCode.getReasonPhrase();
        }
        return statusCode.value() + " " + statusText;
    }
}
