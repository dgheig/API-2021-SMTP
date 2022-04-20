package ch.heigvd.api.smtp;

import java.util.ArrayList;
import java.util.List;

public class Mail {
    private String emailFrom = null;
    private List<String> emailsTo = new ArrayList<>();
    private String data = null;

    public Boolean addTo(String email) {
        // TODO: Check email validity (Cf exemple in RFC)
        // Allow to send many emails to the same dest
        emailsTo.add(email);
        return true;
    }

    public Boolean addFrom(String email) {
        if(emailFrom != null) return false;

        return true;
    }

    public Boolean addData(String data) {
        if(this.data != null) return false;

        return true;
    }
}
