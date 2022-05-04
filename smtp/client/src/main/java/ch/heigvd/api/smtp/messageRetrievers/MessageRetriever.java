package ch.heigvd.api.smtp.messageRetrievers;

import java.util.List;

public interface MessageRetriever {
    public List<String> getMessage();
}
