package eu.xenit.alfresco.client.exception;

import lombok.Getter;

public class HttpStatusException extends AlfrescoClientException {

    @Getter
    private final StatusCode statusCode;

    public HttpStatusException(StatusCode statusCode, String statusText) {
        super(getMessage(statusCode, statusText));
        this.statusCode = statusCode;
    }

    public HttpStatusException(StatusCode statusCode, String statusText, Throwable cause) {
        super(getMessage(statusCode, statusText), cause);
        this.statusCode = statusCode;
    }

    private static String getMessage(StatusCode statusCode, String statusText) {
        if (statusText == null || statusText.trim().isEmpty()) {
            statusText = statusCode.getReasonPhrase();
        }
        return statusCode.value() + " " + statusText;
    }
}
