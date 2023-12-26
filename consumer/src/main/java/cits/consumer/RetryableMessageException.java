package cits.consumer;

import air.ClientMessage;

public class RetryableMessageException extends RuntimeException {
    public RetryableMessageException(final String message) {
        super(message);
    }
}
