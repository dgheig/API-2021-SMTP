package ch.heigvd.api.smtp.messageRetrievers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Class implementing the MessageRetriever interface
 * This class chooses a random message from message folder
 */
public class RandomMessage implements MessageRetriever{
    private File messageFolder;

    /**
     * Constructor using a File
     * @param messageFolder The File object representing the folder containing the messages
     */
    public RandomMessage(File messageFolder) {
        this.messageFolder = messageFolder;
    }

    /**
     * Constructor using String
     * @param messageFolder The path of the folder containing messages as String
     */
    public RandomMessage(String messageFolder) {
        this(new File(messageFolder));
    }

    /**
     * The class specific implementation of the getMessage method defined by the implemented interface
     * @return a list of strings where each item is a line of the message
     */
    @Override
    public List<String> getMessage() {
        return getMessageAsUtf8();
    }

    /**
     * Method used to list available messages in message folder
     * @return a File array where each item is a message file from message folder
     */
    private File[] getMessageFiles(){
        File files[] = messageFolder.listFiles();

        if (files == null){
            System.out.println("Directory does not exist or cannot be read");
            return null;
        }

        if (files.length == 0){
            System.out.println("No files found in message folder");
            return null;
        }
        return files;

    }

    /**
     * Method used to getMessageAsUtf8 get the contents of a message file as UTF-8 string
     * @return a list of strings where each item is a line of the message
     */
    private List<String> getMessageAsUtf8() {
        File[] files = getMessageFiles();
        int index = new Random().nextInt(files.length);
        File file = files[index];
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
