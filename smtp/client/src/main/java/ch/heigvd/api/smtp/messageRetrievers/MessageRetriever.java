package ch.heigvd.api.smtp.messageRetrievers;

import java.util.List;

/**
 * Interface used to define the way messages are chosen from message folder
 */
public interface MessageRetriever {
    public List<String> getMessage();
}
