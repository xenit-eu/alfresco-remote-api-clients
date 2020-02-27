package eu.xenit.alfresco.restapi.client.spi.model.exceptions;

public class ConstraintViolatedException extends ApiException {

    public ConstraintViolatedException() {
    }

    public ConstraintViolatedException(String message) {
        super(message);
    }

    public ConstraintViolatedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConstraintViolatedException(Throwable cause) {
        super(cause);
    }
}
