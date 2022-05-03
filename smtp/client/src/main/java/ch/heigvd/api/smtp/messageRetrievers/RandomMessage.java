package ch.heigvd.api.smtp.messageRetrievers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomMessage implements MessageRetriever{
    private File messageFolder;
    public RandomMessage(File messageFolder) {
        this.messageFolder = messageFolder;
    }
    public RandomMessage(String messageFolder) {
        this(new File(messageFolder));
    }

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
            System.out.println("CAMPAIGN MANAGER::getMessages() - Directory does not exist or cannot be read");
            return null;
        }

        if (files.length == 0){
            System.out.println("CAMPAIGN MANAGER::getMessages() - No files found in message folder");
            return null;
        }
        return files;

    }

    /**
     * @brief getMessageAsUtf8 get the contents of a message file as UTF-8 string
     * @param index the index of the file, as shown in getMessages()
     * @return the UTF-8 strinf value of the message file selected by index
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
            System.out.println("CAMPAIGN MANAGER::getMessageAsUtf8() - " + e);
        }

        return data;

    }
}
