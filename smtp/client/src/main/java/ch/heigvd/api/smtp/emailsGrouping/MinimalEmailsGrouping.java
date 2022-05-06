package ch.heigvd.api.smtp.emailsGrouping;

import java.util.ArrayList;
import java.util.List;

/**
 * Class implementing the EmailsGrouping interface.
 * This class defines a method to create a user defined number of groups from a file containing email addresses
 */
public class MinimalEmailsGrouping implements EmailsGrouping {
    final private int nbPerGroup;

    public MinimalEmailsGrouping(int nbPerGroup) {
        this.nbPerGroup = nbPerGroup;
    }

    @Override
    public List<List<String>> group(List<String> emails) {
        List<List<String>> groups = new ArrayList<>();

        int count = 0;
        List<String> current = null;
        for (String email : emails) {
            if (count % nbPerGroup == 0) {
                // Check if there is enough for another group
                if (emails.size() - count >= nbPerGroup) {
                    current = new ArrayList<>();
                    groups.add(current);
                }
            }
            current.add(email);
            ++count;
        }
        return groups;
    }
}
