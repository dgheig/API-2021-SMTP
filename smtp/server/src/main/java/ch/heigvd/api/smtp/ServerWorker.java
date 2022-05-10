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

    private final Server server;
    private final static Logger LOG = Logger.getLogger(ServerWorker.class.getName());
    private Socket socket;
    private Boolean isRunning = true;

    private Mail mail;
    private String clientNAme = null;
    private String line;
    private BufferedReader in;
    private PrintWriter out;

    private List<String> options = new ArrayList<>(Arrays.asList(
            "8BITMIME",
            // "SIZE",
            // "DSN",
            "HELP"));

    private void sendOptions() {
        Iterator<String> it = options.iterator();
        if (!it.hasNext())
            return;

        String value = it.next();
        while (true) {
            if (it.hasNext()) {
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
     * @param clientSocket
     *            connected to worker
     */
    public ServerWorker(Server server, Socket clientSocket) {
        // Log output on a single line
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");
        this.server = server;
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
     * @param clientSocket
     *            with the connection with the individual client.
     */
    private void handleClient(Socket clientSocket) {
        try (
                InputStream inputStream = clientSocket.getInputStream();
                OutputStream outputStream = clientSocket.getOutputStream();) {
            // Init "global per instance" variables
            mail = new Mail();
            in = new BufferedReader(new InputStreamReader(inputStream));
            out = new PrintWriter(outputStream);

            // Starting exchanges

            out.println("220 " + server.getNAME() + " Simple Mail Transfer Service Ready");
            out.flush();
            while (isRunning) {
                line = in.readLine();
                if (line == null)
                    return;
                dispatch();

            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
        }

    }

    private void dispatch() throws IOException {
        // Commands without order
        if (line.startsWith("HELP")) {
            HELP();
        } else if (line.startsWith("RSET")) {
            RSET();
        } else if (line.startsWith("NOOP")) {
            NOOP();
        } else if (line.startsWith("QUIT")) {
            QUIT();
        } else if (line.startsWith("VRFY")) {
            VRFY();
        }
        // Commands that must come in order
        else if (line.startsWith("EHLO")) {
            EHLO();
        } else if (line.startsWith("HELO")) {
            HELO();
        } else if (clientNAme == null) {
            BadSequenceOfCommands();
        } else if (line.startsWith("MAIL")) {
            MAIL();
        } else if (!mail.hasFrom()) {
            BadSequenceOfCommands();
        } else if (line.startsWith("RCPT")) {
            RCPT();
        } else if (!mail.hasRecipients()) {
            BadSequenceOfCommands();
        } else if (line.startsWith("DATA")) {
            DATA();
        } else { // Invalid code

        }
    }

    private void BadSequenceOfCommands() {
        out.println("503 Bad sequence of commands");
        out.flush();
    }

    private void EHLO() {
        clientNAme = Utils.substring(line, 5);
        /*
         * if (clientNAme != null) {
         * out.println("221 " + server.getNAME() + " greets " + clientNAme);
         * out.flush();
         * } // Send error otherwise if no name?
         */
        sendOptions();
    }

    private void HELO() {
        EHLO();
    }

    private void MAIL() {
        if (line.startsWith("MAIL FROM:")) {
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
        if (line.startsWith("RCPT TO:")) {
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
        if (!line.equals("DATA")) {
            // parameters are not allowed and considered as error
            out.println("ERROR"); // TODO: use correct code
            out.flush();
            return;
        }
        out.println("354 End data with <CR><LF>.<CR><LF>");
        out.flush();
        StringBuilder body = new StringBuilder();
        while (true) {
            line = in.readLine();
            if (line == null)
                return;
            if (line.equals("."))
                break;
            if (line.startsWith(".")) {
                line = line.substring(1);
            }
            // LOG.info(Utils.unescape(line));
            body.append(line);
            body.append("\n");
        }
        // Remove extra "\n"
        if (body.length() > 0) {
            body.deleteCharAt(body.length() - 1);
        }
        mail.addData(body.toString());
        mail.sendEmails();
        out.println("250 OK");
        out.flush();
    }

    private void RSET() {
        // RSET does not greet the user again, just clear all stored content and buffers
        // TODO ?
        if (!line.equals("RSET")) {
            // parameters are not allowed and considered as error
            out.println("ERROR"); // TODO: use correct code
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
        if (!line.equals("QUIT")) {
            // parameters are not allowed and considered as error
            out.println("ERROR"); // TODO: use correct code
            out.flush();
            return;
        }
        isRunning = false;
        out.println("221 " + server.getNAME() + "Goodbye " + clientNAme + ", closing connection");
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
