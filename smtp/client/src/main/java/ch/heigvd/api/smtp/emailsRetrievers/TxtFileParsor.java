package ch.heigvd.api.smtp.emailsRetrievers;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Class implementing the EmailsRetriever interface
 * This class is used to parse a text file containing email addresses
 */
public class TxtFileParsor implements EmailsRetriever {
    private final File file;
    List<String> emails = null;

    /**
     * Constructor using a string file name
     * @param file The name of the file containing email addresses
     */
    public TxtFileParsor(String file) {
        this(new File(file));
    }

    /**
     * Constructor using a File
     * @param file The File object containing email addresses
     */
    public TxtFileParsor(File file) {
        this.file = file;
    }

    /**
     * Get email addresses from file
     * @return list of strings, where each item in list is an email address
     */
    @Override
    public List<String> getEmails() {
        List<String> emails = new ArrayList<>();
        String line = null;
        // Initialize the buffered reader used to read file
        try(
                BufferedReader reader = new BufferedReader(new FileReader(file, Charset.forName("utf-8")))
        ) {
            // Read the whole file line by line and add email addresses to the emails list
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
