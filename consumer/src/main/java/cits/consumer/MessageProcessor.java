package cits.consumer;

import air.ClientMessage;

public class MessageProcessor {

    public void processMessage(ClientMessage clientMessage) {
        long id = clientMessage.getId();
        String message = String.format("Client %s MessageId %s", clientMessage.getClient(), clientMessage.getId());
        if (id % 4 == 0) {
            System.out.println("Retry: " + message);
            throw new RetryableMessageException(message);
        }
    }

}
