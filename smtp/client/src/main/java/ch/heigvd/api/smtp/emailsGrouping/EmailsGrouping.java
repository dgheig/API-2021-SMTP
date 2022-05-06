package ch.heigvd.api.smtp.emailsGrouping;

import java.util.List;

/**
 * Interface used to define the structure of mailing groups
 */
public interface EmailsGrouping {
    List<List<String>> group(List<String> emails);
}
