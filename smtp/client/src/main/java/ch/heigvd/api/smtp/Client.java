package ch.heigvd.api.smtp;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Vector;

public class Client {

    private CampaignManager campaignManager;
    private Vector<Vector<String>> groups;
    private String fileEmail;
    private String pathMessages;

    private String srvIP;
    private int srvPort;
    private BufferedWriter writer;
    private BufferedReader reader;


    public Client(String srvIP, int srvPort, String fileEmail) throws IOException {
        this.srvIP = srvIP;
        this.srvPort = srvPort;
        this.fileEmail = fileEmail;
        init();
    }

    public Client(String srvIP, int srvPort, String fileEmail, String pathMessages) throws IOException {
        this(srvIP, srvPort, fileEmail);
        this.pathMessages = pathMessages;
        init();
    }

    private void init() throws IOException {
        if (this.pathMessages == null)
            this.campaignManager = new CampaignManager(this.fileEmail);
        else
            this.campaignManager = new CampaignManager(this.fileEmail, this.pathMessages);

        Socket socket = new Socket(srvIP, srvPort);
        this.writer = new BufferedWriter(
                new OutputStreamWriter(
                        socket.getOutputStream(), StandardCharsets.UTF_8
                )
        );

        this.reader = new BufferedReader(
                new InputStreamReader(
                        socket.getInputStream(), StandardCharsets.UTF_8
                )
        );
    }

    public void runCampaign() {

    }

    private void sendEmail(String from, Vector<String> tos, String subject, String body) {

        try {

            // initiate dialog with server
            writer.write("EHLO plop.com\r\n");
            writer.flush();

            // options
            Vector<String> options = new Vector<>();
            String line = reader.readLine();
            if (!line.startsWith("2")) {
                System.out.println(line);
                throw new RuntimeException("Error in EHLO");
            }

            while ((line = reader.readLine()).startsWith("250-")) {
                options.add(line);
            }

            if (line.startsWith("250 ")) {
                options.add(line);
            } else {
                System.out.println(line);
                throw new RuntimeException("Error in EHLO");
            }

            // set MAIL FROM
            writer.write(String.format("MAIL FROM: <%s>\r\n", from));
            writer.flush();
            if (!reader.readLine().toUpperCase().startsWith("250 OK")) {
                System.out.println(line);
                throw new RuntimeException("Error in MAIL FROM");
            }

            // set RCPT TO
            for (String to : tos) {
                writer.write(String.format("RCPT TO: <%s>\r\n", to));
                writer.flush();
                if (!reader.readLine().toUpperCase().startsWith("250 OK")) {
                    System.out.println(line);
                    throw new RuntimeException("Error in RCPT TO");
                }
            }

            // set DATA
            // Base64.Encoder enc = Base64.getEncoder();
            writer.write("DATA\r\n");
            writer.write(String.format("Subject:=?utf-8?Q?\"%s\"?=\r\n\r\n", subject));
            writer.write(String.format("=?utf-8?Q?\"%s\"?=\r\n", body));
            writer.write(".\r\n");
            writer.flush();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setMailFrom() {

    }

    private void setRcptTo() {

    }

    private void setData() {

    }

    public static void main(String[] args) throws IOException {
        Client client = new Client("localhost", 2500, "");
        client.sendEmail("src@plip.com", new Vector<>(List.of(new String[]{"dst@klonk.ch"})),
                "ààé à", "été");
    }
}