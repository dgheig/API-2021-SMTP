package ch.heigvd.api.smtp;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Calculator worker implementation
 */
public class ServerWorker implements Runnable {

    private final static String SERVER_NAME = "foo.com";
    private final static Logger LOG = Logger.getLogger(ServerWorker.class.getName());
    private Socket socket;
    private Boolean isRunning = true;
    private Mail mail;
    private String line;
    private BufferedReader in;
    private PrintWriter out;

    private List<String> options = new ArrayList<>(Arrays.asList(
            // "8BITMIME",
            // "SIZE",
            "DSN",
            "HELP"
    ));

    private void sendOptions() {
        Iterator<String> it = options.iterator();
        if(!it.hasNext()) return;

        String value = it.next();
        while(true) {
            if(it.hasNext()) {
                out.println("250-" + value);
                value = it.next();
            } else {
                out.println("250 " + value);
                break;
            }
        }
        out.flush();
    }

    /**
     * Instantiation of a new worker mapped to a socket
     *
     * @param clientSocket connected to worker
     */
    public ServerWorker(Socket clientSocket) {
        // Log output on a single line
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");
        socket = clientSocket;

    }

    /**
     * Run method of the thread.
     */
    @Override
    public void run() {
        handleClient(this.socket);
    }


    /**
     * Handle a single client connection: receive commands and send back the result.
     *
     * @param clientSocket with the connection with the individual client.
     */
    private void handleClient(Socket clientSocket) {
        try(
                InputStream inputStream = clientSocket.getInputStream();
                OutputStream outputStream = clientSocket.getOutputStream();
        ) {
            // Init "global per instance" variables
            mail = new Mail();
            in = new BufferedReader(new InputStreamReader(inputStream));
            out = new PrintWriter(outputStream);

            // Starting exchanges

            out.println("220 " + SERVER_NAME + " Simple Mail Transfer Service Ready");
            out.flush();
            while(isRunning) {
                line = in.readLine();
                if(line == null)
                    return;
                dispatch();

            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
            /*in = null;
            out = null;*/
        }

    }

    private void dispatch() throws IOException {
        if(line.startsWith("EHLO")) {
            EHLO();
        }
        else if(line.startsWith("HELO")) {
            HELO();
        }
        else if(line.startsWith("MAIL")) {
            MAIL();
        }
        else if(line.startsWith("RCPT")) {
            RCPT();
        }
        else if(line.startsWith("DATA")) {
            DATA();
        }
        else if(line.startsWith("RSET")) {
            RSET();
        }
        else if(line.startsWith("NOOP")) {
            NOOP();
        }
        else if(line.startsWith("QUIT")) {
            QUIT();
        }
        else if(line.startsWith("VRFY")) {
            VRFY();
        }
        else if(line.startsWith("HELP")) {
            HELP();
        }
        else {  // Invalid code

        }
    }

    private void EHLO() {
        String name = Utils.substring(line, 5);
        if(name != null) {
            out.println(SERVER_NAME + " greets " + name);
            out.flush();
        }
        sendOptions();
    }
    private void HELO() {
        EHLO();
    }
    private void MAIL() {
        if(line.startsWith("MAIL FROM:")) {
            String email = Utils.options(line, "MAIL FROM:");
            LOG.info("Received: " + email);
            mail.addFrom(email);

            out.println("250 OK");
            out.flush();
        } else {
            // TODO: Invalid command
        }
    }
    private void RCPT() {
        if(line.startsWith("RCPT TO:")) {
            String email = Utils.options(line, "RCPT TO:");
            LOG.info("Received: " + email);
            mail.addTo(email);

            out.println("250 OK");
            out.flush();
        } else {
            // TODO: Invalid command
        }
    }
    private void DATA() throws IOException {
        if(!line.equals("DATA")) {
            // parameters are not allowed and considered as error
            out.println("ERROR");  // TODO: use correct code
            out.flush();
            return;
        }
        StringBuilder body = new StringBuilder();
        while(true) {
            line = in.readLine();
            if(line == null)
                return;
            if(line.equals("."))  // TODO: Is it okay?
                break;
            // LOG.info(Utils.unescape(line));
            body.append(line);
            body.append("\n");
        }
        // Remove extra "\n"
        if(body.length() > 0) {
            body.deleteCharAt(body.length() - 1);
        }
        mail.addData(body.toString());
        out.println("250 OK");
        out.flush();
    }
    private void RSET() {
        // RSET does not greet the user again, just clear all stored content and buffers
        // TODO ?
        if(!line.equals("RSET")) {
            // parameters are not allowed and considered as error
            out.println("ERROR");  // TODO: use correct code
            out.flush();
            return;
        }
        mail = new Mail();
        out.println("250 OK");
        out.flush();
    }
    private void NOOP() {
        // It does nothing
        out.println("250 OK");
        out.flush();
    }
    private void QUIT() {
        if(!line.equals("QUIT")) {
            // parameters are not allowed and considered as error
            out.println("ERROR");  // TODO: use correct code
            out.flush();
            return;
        }
        isRunning = false;
        out.println("250 OK");
        out.flush();
    }
    private void HELP() {
        // TODO ?
        out.println(mail.toString());
        out.println("250 OK");
        out.flush();
    }
    private void VRFY() {
        // TODO
        out.println("250 OK");
        out.flush();
    }

}