package ch.heigvd.api.smtp.senderSelectors;

import java.util.List;
import java.util.Random;

/**
 * Take the sender randomly
 */
public class RandomSenderSelector implements SenderSelector {

    @Override
    public String getSender(List<String> emails) {
        int index = new Random().nextInt(emails.size());
        String sender = emails.remove(index);
        return sender;
    }
}