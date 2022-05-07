package ch.heigvd.api.smtp.senderSelectors;

import ch.heigvd.api.smtp.Utils;

import java.util.List;
import java.util.Random;

/**
 * Interactively ask the user for the sender to user
 */
public class AskSenderSelector implements SenderSelector {

    @Override
    public String getSender(List<String> emails) {
        int index = Utils.interactiveChoiceIndex(
                emails,
                "Choose the sender"
        );
        String sender = emails.remove(index);
        return sender;
    }
}