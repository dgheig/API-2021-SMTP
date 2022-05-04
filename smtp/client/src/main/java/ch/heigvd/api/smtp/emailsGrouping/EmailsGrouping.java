package ch.heigvd.api.smtp.emailsGrouping;

import java.util.List;

public interface EmailsGrouping {
    List<List<String>> group(List<String> emails);
}
