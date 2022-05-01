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
        if(email == null) return false;

        emailsTo.add(email);
        return true;
    }

    public Boolean addFrom(String email) {
        if(emailFrom != null) return false;
        emailFrom = email;
        return true;
    }

    public Boolean addData(String data) {
        if(this.data != null) return false;
        this.data = data;
        return true;
    }

    public String toString() {
        StringBuilder content = new StringBuilder();
        content.append("Email from: ");
        content.append(emailFrom == null ? "UNKNOWN" : emailFrom);
        content.append("\n");
        content.append("RCPT TO:\n");
        for(String email: emailsTo) {
            content.append("\t" + email + "\n");
        }
        content.append("Data:\n");
        content.append(data == null ? "<EMPTY>" : data);
        content.append("\n");

        return content.toString();
    }
}
