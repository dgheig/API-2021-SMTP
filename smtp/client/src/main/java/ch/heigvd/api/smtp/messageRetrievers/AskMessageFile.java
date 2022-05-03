package ch.heigvd.api.smtp.messageRetrievers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AskMessageFile implements MessageRetriever{
    private File messageFolder;
    public AskMessageFile(File messageFolder) {
        this.messageFolder = messageFolder;
    }
    public AskMessageFile(String messageFolder) {
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

    private File askFile() {
        File files[] = getMessageFiles();
        Scanner scanner = new Scanner(System.in);
        int index = -1;
        while(true) {
            System.out.println("CAMPAIGN MANAGER::getMessages() - Available messages, use given index to select");
            for (int i = 0; i < files.length; i++){
                System.out.println("[" + i + "] " + files[i] );
            }
            try {
                index = scanner.nextInt();
                if(index >= 0 && index < files.length)
                    return  files[index];
                else
                    System.out.println("Index out of range");
            } catch (Exception e) {
                // ...
            }
        }
    }

    /**
     * @brief getMessageAsUtf8 get the contents of a message file as UTF-8 string
     * @param index the index of the file, as shown in getMessages()
     * @return the UTF-8 strinf value of the message file selected by index
     */
    private List<String> getMessageAsUtf8() {
        File file = askFile();
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
