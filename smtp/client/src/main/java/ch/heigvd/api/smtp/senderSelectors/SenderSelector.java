package ch.heigvd.api.smtp.senderSelectors;

import java.util.List;

public interface SenderSelector {
    /**
     * Choose a sender in emails and remove it from emails
     * @param emails emails where to take the sender from. This will remove the sender from the list
     * @return the sender email
     */
    String getSender(List<String> emails);
}
