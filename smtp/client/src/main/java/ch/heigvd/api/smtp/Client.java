package ch.heigvd.api.smtp;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
    private final static Logger LOG = Logger.getLogger(Client.class.getName());
    public static void main(String[] args) {
        System.out.println("Hello world!");
    }

    private static String readLineOrThrow(BufferedReader input) throws IOException {
        String line = input.readLine();
        if(line == null)
            throw new IOException("NO OUTPUT");
        LOG.info(line);
        return line;
    }
    private static String readLineOrThrow(BufferedReader input, String start) throws IOException {
        String line = input.readLine();
        if(line == null)
            throw new IOException("NO OUTPUT");
        if(!line.startsWith(start))
            throw new IOException("An error occured: " + line);
        LOG.info(line);
        return line;
    }
    public static boolean sendEmails(String server, int port, String from, List<String> to, String data) {
        return sendEmails(server, port, from, to, Arrays.asList(data.split("\n")));
    }
    public static boolean sendEmailsSeparately(String server, int port, String from, List<String> to, String data) {
        return sendEmails(server, port, from, to, Arrays.asList(data.split("\n")));
    }
    public static boolean sendEmailsSeparately(String server, int port, String from, List<String> to, List<String> data) {
        boolean result = true;
        for(String email: to) {
            result &= sendEmails(server, port, from, Arrays.asList(email), data);
        }
        return result;
    }
    public static boolean sendEmails(String server, int port, String from, List<String> to, List<String> data) {
        try(
                Socket socket = new Socket(server, port);
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();
        ) {
            LOG.info("Sending to server: " + server);
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            PrintWriter out = new CRLFPrintWriter(outputStream);
            String line = null;

            readLineOrThrow(in, "220");  // Server announcement

            out.println("EHLO SERVER");
            out.flush();

            // Deal with options
            while((line = readLineOrThrow(in, "250")).startsWith("250-"));
            if(!line.startsWith("250 ")) {
                LOG.info(line);
                throw new Exception("Error while sending");
            }
            out.println("MAIL FROM:" + from);
            out.flush();
            readLineOrThrow(in, "250");
            for(String email: to) {
                out.println("RCPT TO:" + email);
                out.flush();
                readLineOrThrow(in, "250");
            }
            out.println("DATA");
            out.flush();
            readLineOrThrow(in, "354");
            for(String dataLine: data) {
                if(dataLine.startsWith("."))
                    dataLine = "." + dataLine;
                out.println(dataLine);
            }
            out.println(".");
            out.flush();
            readLineOrThrow(in);
            out.println("QUIT");
            out.flush();
            readLineOrThrow(in, "221");

        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
            return false;
        }
        return true;
    }
}