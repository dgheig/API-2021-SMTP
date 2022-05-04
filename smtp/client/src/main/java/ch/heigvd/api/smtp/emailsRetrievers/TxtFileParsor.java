package ch.heigvd.api.smtp.emailsRetrievers;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class TxtFileParsor implements EmailsRetriever {
    private final File file;
    List<String> emails = null;
    public TxtFileParsor(String file) {
        this(new File(file));
    }
    public TxtFileParsor(File file) {
        this.file = file;
    }
    @Override
    public List<String> getEmails() {
        /*if(emails != null)  // Caching
            return emails;*/
        if(emails == null)
            emails = new ArrayList<>();
        List<String> emails = new ArrayList<>();
        String line = null;
        System.out.println("CAMPAIGN MANAGER::Constructor - Your email file is read here: " + file);
        try(
                BufferedReader reader = new BufferedReader(new FileReader(file, Charset.forName("utf-8")))
        ) {
            while(true) {
                line = reader.readLine();
                if(line == null)
                    break;
                emails.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return emails;
    }
}
