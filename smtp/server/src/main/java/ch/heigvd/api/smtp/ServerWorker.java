package ch.heigvd.api.smtp;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Calculator worker implementation
 */
public class ServerWorker implements Runnable {

    private final static Logger LOG = Logger.getLogger(ServerWorker.class.getName());
    private Socket socket;

    private List<String> options = new ArrayList<>(String[]{
            "foo.com greets bar.com",
            "8BITMIME",
            "SIZE",
            "DSN",
            "HELP"
    });

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
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            PrintWriter out = new PrintWriter(outputStream);
            out.println("220 foo.com Simple Mail Transfer Service Ready");
            out.flush();
            while(true) {
                String line = in.readLine();
                if(line == null)
                    return;
                out.println("You said: " + line);
                out.flush();

            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
        }

    }
}