package ch.heigvd.api.smtp.emailsRetrievers;

import java.util.List;

/**
 * Inteface used to define the method used for retrieving email addresses from somewhere
 */
public interface EmailsRetriever {
    List<String> getEmails();
}
