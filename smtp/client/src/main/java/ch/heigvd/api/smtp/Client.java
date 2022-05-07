package ch.heigvd.api.smtp;

import jdk.jshell.execution.Util;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utilities to communicate with a SMTP server
 */
public class Client {
    private final static Logger LOG = Logger.getLogger(Client.class.getName());

    /**
     * The fonction takes the content of the header (without the header declaration itself)
     * and encode it for SMTP UTF-8 support
     * @param headerText the content of the header
     * @return the content encoded
     */
    public static String UTFHeader(String headerText) {
        if (headerText == null)
            return null;
        String encodedText = Base64.getEncoder().encodeToString(headerText.getBytes());
        return "=?utf-8?B?" + encodedText + "?=";
    }

    /**
     * Takes the whole data and makes it SMTP UTF-8 compatible
     * @param data line by line representation of the DATA command content (without the closing dot)
     * @return the data with edited header to support UTF-8 on SMTP protocol
     */
    public static List<String> UTFData(List<String> data) {
        List<String> result = new ArrayList<>();
        boolean hasContentTypeHeader = false;
        Iterator<String> it = data.iterator();
        while(it.hasNext()) {
            String line = it.next();
            if(line.startsWith("From:") || line.startsWith("To:")) {
                // Do nothing
            } else if (line.startsWith("Subject:")) {
                line = "Subject:" + UTFHeader(Utils.substring(line, ("Subject:").length()));
            } else if (line.startsWith("Content-Type:")) {
                hasContentTypeHeader = true;
            } else {
                if (!hasContentTypeHeader) {
                    result.add("Content-Type: charset=UTF-8");
                }
                if(!line.equals("")) {
                    result.add("");
                }
                result.add(line);
                break;
            }
            result.add(line);
        }

        while(it.hasNext()) {
            result.add(it.next());
        }
        return result;
    }

    /**
     * Method used to read a line from a bufferedReader
     * 
     * @param input
     *            the BufferedReader from which to read
     * @return the string read from input
     * @throws IOException
     *             if nothing has been read from input
     */
    private static String readLineOrThrow(BufferedReader input) throws IOException {
        String line = input.readLine();
        if (line == null)
            throw new IOException("NO OUTPUT");
        LOG.info(line);
        return line;
    }

    /**
     * Method used to read a line from a BufferedReader looking for a specific line
     * start string
     * 
     * @param input
     *            the BufferedReader from which to read
     * @param start
     *            the string by which the line should start
     * @return the string read from input
     * @throws IOException
     *             if nothing has been read from input or if the line does not start
     *             with the specified string
     */
    private static String readLineOrThrow(BufferedReader input, String start) throws IOException {
        String line = input.readLine();
        if (line == null)
            throw new IOException("NO OUTPUT");
        if (!line.startsWith(start))
            throw new IOException("An error occured: " + line);
        LOG.info(line);
        return line;
    }

    /**
     * Method used to send a message with the data as string
     * 
     * @param server
     *            the ip address of the server
     * @param port
     *            the tcp port of the server
     * @param from
     *            the email address used as sender
     * @param to
     *            the email addresses used as recipients
     * @param data
     *            the data as string
     * @return true if message sending succeeded
     */
    public static boolean sendEmails(String server, int port, String from, List<String> to, String data) {
        return sendEmails(server, port, from, to, Arrays.asList(data.split("\n")));
    }

    /**
     * Method used to send a message with the data as list of strings
     * 
     * @param server
     *            the ip address of the server
     * @param port
     *            the tcp port of the server
     * @param from
     *            the email address used as sender
     * @param to
     *            the email addresses used as recipients
     * @param data
     *            the data as list of string
     * @return true if message sending succeeded
     */
    public static boolean sendEmails(String server, int port, String from, List<String> to, List<String> data) {
        try (
                Socket socket = new Socket(server, port);
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream()) {
            LOG.info("Sending to server: " + server);
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

            PrintWriter out = new CRLFPrintWriter(
                    new BufferedWriter(
                        new OutputStreamWriter(
                            outputStream,
                            StandardCharsets.UTF_8.toString()
                        )
                    )
            );
            String line = null;

            readLineOrThrow(in, "220"); // Server announcement

            out.println("EHLO SERVER");
            out.flush();

            // Deal with options
            checkOptions(in);
            out.println("MAIL FROM:" + from);
            out.flush();
            readLineOrThrow(in, "250");
            for (String email : to) {
                out.println("RCPT TO:" + email);
                out.flush();
                readLineOrThrow(in, "250");
            }
            out.println("DATA");
            out.flush();
            readLineOrThrow(in, "354");
            data = UTFData(data);
            for (String dataLine : data) {
                if (dataLine.startsWith("."))
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

    /**
     * Method used to send a separate mail per recipient
     * 
     * @param server
     *            the ip address of the server
     * @param port
     *            the tcp port of the server
     * @param from
     *            the email address used as sender
     * @param to
     *            the email addresses used as recipients
     * @param data
     *            the data as list of string
     * @return true if message sending succeeded
     */
    public static boolean sendEmailsSeparately(String server, int port, String from, List<String> to, String data) {
        return sendEmails(server, port, from, to, Arrays.asList(data.split("\n")));
    }

    /**
     * Method used to send a separate mail per recipient
     * 
     * @param server
     *            the ip address of the server
     * @param port
     *            the tcp port of the server
     * @param from
     *            the email address used as sender
     * @param to
     *            the email addresses used as recipients
     * @param data
     *            the data as list of string
     * @return true if message sending succeeded
     */
    public static boolean sendEmailsSeparately(String server, int port, String from, List<String> to,
            List<String> data) {
        boolean result = true;
        for (String email : to) {
            result &= sendEmails(server, port, from, Arrays.asList(email), data);
        }
        return result;
    }

    /**
     * Method used to check server options
     * 
     * @param input
     *            the BufferedReader we want to read from
     * @return the last line read from input
     * @throws Exception
     *             if an unpexpected line was read from input
     */
    private static String checkOptions(BufferedReader input) throws Exception {
        String line = null;

        // Deal with options
        while ((line = readLineOrThrow(input, "250")).startsWith("250-")) {
            if (line.contains("8BITMIME")) {
                LOG.info("Server supports 8BITMIME extension (UTF-8)");
            }
        }
        if (!line.startsWith("250 ")) {
            LOG.info(line);
            throw new Exception("Error while sending");
        }
        if (line.contains("8BITMIME")) {
            LOG.info("Server supports 8BITMIME extension (UTF-8)");
        }
        return line;
    }

}
