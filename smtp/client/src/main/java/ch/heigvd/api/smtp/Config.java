package ch.heigvd.api.smtp;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;

public class Config {
    private String server;
    private int port = 25;
    private String emailsFile;
    private String messageFolder;
    private int victimCount;

    public static Config get(File file) {
        try {
            Config config = new ObjectMapper().readValue(file, Config.class);
            return config;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public String getServer() {
        return server;
    }
    public File getEmailsFile() {
        return new File(emailsFile);
    }
    public File getMessageFolder() {
        return new File(messageFolder);
    }
    public int getPort() {
        return port;
    }
    public int getVictimCount() {
        return victimCount;
    }
}
