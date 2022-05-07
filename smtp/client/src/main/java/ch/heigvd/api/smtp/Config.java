package ch.heigvd.api.smtp;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

/**
 * Class used to store the information read from a configuration file
 */
public class Config {
    private String server;
    private int port = 25;
    private String emailsFile;
    private String messageFolder;
    // private int nbOfGroups = 3;

    /**
     * Method used to read and store the information from the configuration file
     * @param file the File object of the configuration file
     * @return the loaded configuration or null if failure
     */
    public static Config get(File file) throws Exception {
        if(!file.exists()) {
            throw new Exception("File '" + file.getAbsolutePath() + "' does not exists.");
        }
        Config config = new ObjectMapper().readValue(file, Config.class);
        return config;
    }

    private File getFile(String filename) throws Exception {
        File file = new File(filename);
        if(!file.exists()) {
            throw new Exception("File '" + file.getAbsolutePath() + "' does not exists.");
        }
        return file;
    }

    public String getServer() {
        return server;
    }
    public File getEmailsFile() throws Exception {
        return getFile(emailsFile);
    }
    public File getMessageFolder() throws Exception {
        return getFile(messageFolder);
    }
    public int getPort() {
        return port;
    }
    /* public int getNbOfGroups() {
        return nbOfGroups;
    }*/
}
