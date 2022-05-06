package ch.heigvd.api.smtp.emailsGrouping;

import java.util.ArrayList;
import java.util.List;

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
                count = 0;
            }
            current.add(email);
            ++count;
        }
        return groups;
    }
}
