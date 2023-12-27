package cits.consumer;

public class RetryableMessageException extends RuntimeException {
    public RetryableMessageException(final String message) {
        super(message);
    }
}
