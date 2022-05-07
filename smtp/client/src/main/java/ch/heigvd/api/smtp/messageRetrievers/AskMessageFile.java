package ch.heigvd.api.smtp.messageRetrievers;

import ch.heigvd.api.smtp.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Class implementing the MessageRetriever interface
 * This class is used to let the user choose which email should be sent to a group
 */
public class AskMessageFile implements MessageRetriever{
    private File messageFolder;

    /**
     * Constructor using a File
     * @param messageFolder The File object representing the folder containing the messages
     */
    public AskMessageFile(File messageFolder) {
        this.messageFolder = messageFolder;
    }

    /**
     * Constructor using String
     * @param messageFolder The path of the folder containing messages as String
     */
    public AskMessageFile(String messageFolder) {
        this(new File(messageFolder));
    }

    /**
     * The class specific implementation of the getMessage method defined by the implemented interface
     * @return
     */
    @Override
    public List<String> getMessage() {
        return getMessageAsUtf8();
    }

    /**
     * @brief getMessages list available messages in message folder
     */
    private File[] getMessageFiles(){
        File files[] = messageFolder.listFiles();

        if (files == null){
            System.out.println(
                    "CAMPAIGN MANAGER::getMessages() - Directory '" +
                    messageFolder.getAbsolutePath() +
                    "' does not exist or cannot be read"
            );
            return null;
        }

        if (files.length == 0){
            System.out.println("CAMPAIGN MANAGER::getMessages() - No files found in message folder " +
                    messageFolder.getAbsolutePath());
            return null;
        }
        return files;

    }

    /**
     * Method used to getMessageAsUtf8 get the contents of a message file as UTF-8 string
     * @return a list of strings where each item is a line of the message
     */
    private List<String> getMessageAsUtf8() {
        File files[] = getMessageFiles();
        File file = Utils.interactiveChoice(
                files,
                "Choose the message file"
        );
        if(file == null) {
            return null;
        }
        List<String> data = new ArrayList<>();
        String line = null;

        try (FileReader fr = new FileReader(file, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(fr))
        {
            while ((line = br.readLine()) != null) {
                data.add(line);
            }

        } catch (Exception e){
            System.out.println("getMessageAsUtf8() - " + e);
        }

        return data;

    }
}
