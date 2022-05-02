package ch.heigvd.api.smtp;

import java.util.*;

public class Mail {
    private String emailFrom = null;
    private List<String> emailsTo = new ArrayList<>();
    private List<String> data = new ArrayList<>();

    public Boolean addTo(String email) {
        // TODO: Check email validity (Cf exemple in RFC)
        // Allow to send many emails to the same dest
        email = Utils.extractEmail(email);
        if(email == null) return false;
        emailsTo.add(email);
        return true;
    }

    public boolean hasFrom() {return emailFrom != null && !emailFrom.equals("");}
    public boolean hasRecipients() {return emailsTo != null && !emailsTo.isEmpty();}

    public Boolean addFrom(String email) {
        if(emailFrom != null) return false;
        emailFrom = email;
        return true;
    }

    public Boolean addData(String data) {
        this.data.add(data);
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

    public Map<String, Set<String>> rcptByDomains() {
        Map<String, Set<String>> groups = new HashMap<>();
        for(String email: emailsTo) {
            String domain = Utils.extractDomain(email);
            if(domain == null) continue;
            groups.putIfAbsent(domain, new HashSet<>());
            groups.get(domain).add(email);
        }
        return groups;
    }

    public void sendEmails() {
        Map<String, Set<String>> groups = rcptByDomains();
        groups.forEach((String domain, Set<String> emailsto) -> {
            List<String> servers = Utils.getMailServer(domain);
            // Try each possible server
            for(String server: servers) {
                if(Utils.sendEmails(server, emailFrom, new ArrayList<>(emailsto), data)) {
                    return;
                }
            }
        });
    }
}
