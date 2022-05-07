package ch.heigvd.api.smtp.emailsGrouping;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomGrouping implements EmailsGrouping {
    private String takeOne(List<String> emails) {
        if(emails == null || emails.size() == 0)
            return null;
        int index = new Random().nextInt(emails.size());
        String email = emails.remove(index);
        return email;
    }
    @Override
    public List<List<String>> group(List<String> emails, int nbOfGroups) {
        List<List<String>> groups = new ArrayList<>();
        for(int i = 0; i < nbOfGroups; ++i) {
            groups.add(new ArrayList<>());
        }
        int index = 0;
        while(emails.size() > 0) {
            groups.get(index).add(takeOne(emails));
            index = (index + 1) % nbOfGroups;
        }

        return groups;
    }
}
