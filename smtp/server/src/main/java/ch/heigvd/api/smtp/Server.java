package ch.heigvd.api.smtp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Calculator server implementation - multi-thread
 */
public class Server {

    private final static Logger LOG = Logger.getLogger(Server.class.getName());
    private final String NAME;
    private final int PORT; // 25;

    public Server() {
        this("anonymous.server", 25);
    }
    public Server(String name, int port) {
        this.NAME = name;
        this.PORT = port;
    }

    public String getNAME() {return NAME;}

    /**
     * Main function to start the server
     */
    public static void main(String[] args) {
        // Log output on a single line
        (new Server()).start();
    }

    /**
     * Start the server on a listening socket.
     */
    public void start() {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return;
        }
        while (true) {
            LOG.info("Waiting (blocking) for a new client...");
            try {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ServerWorker(this, clientSocket)).start();
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }
}
