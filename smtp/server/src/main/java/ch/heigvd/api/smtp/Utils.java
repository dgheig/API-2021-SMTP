package ch.heigvd.api.smtp;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.text.StringEscapeUtils;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.xbill.DNS.*;
import org.xbill.DNS.lookup.LookupSession;

public class Utils {
    private final static Logger LOG = Logger.getLogger(Server.class.getName());
    private final static Pattern EMAIL_REGEX = Pattern.compile("^[_A-Za-z0-9-+]+(.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$");
    private final static Pattern EXTRACT_EMAIL = Pattern.compile("([_A-Za-z0-9-+]+(.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,}))");
    // private final static Pattern EMAIL_DOMAIN = Pattern.compile("(?<=@)([A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,}))");
    public static String substring(String str, int index) {
        if(str.length() > index)
            return str.substring(index);
        return "";
    }
    public static String options(String expression, String command) {
        if(expression.startsWith(command))
            return substring(expression, command.length());
        return null;
    }

    public static String unescape(String str) {
        return StringEscapeUtils.escapeJava(str);
    }

    public static String readLineOrThrow(InputStream input) throws IOException {
        return readLineOrThrow(new BufferedReader(new InputStreamReader(input)));
    }
    public static String readLineOrThrow(BufferedReader input) throws IOException {
        String line = input.readLine();
        if(line == null)
            throw new IOException("NO OUTPUT");
        LOG.info(line);
        return line;
    }
    public static String readLineOrThrow(BufferedReader input, String start) throws IOException {
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
    public static boolean sendEmails(String server, String from, List<String> to, List<String> data) {
        return sendEmails(server, 25, from, to, data);
    }
    public static boolean sendEmails(String server, String from, List<String> to, String data) {
        return sendEmails(server, 25, from, to, data);
    }
    public static String extractDomain(String email) {
        String cleaned_email = extractEmail(email);
        if(cleaned_email == null)
            return null;
        String domain = cleaned_email.split("@")[1];
        return domain;
    }
    public static String extractEmail(String email) {
        if(email == null) return null;
        Matcher matcher = EXTRACT_EMAIL.matcher(email);
        if(matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public static List<String> getMailServer(String domain) {
        List<Pair<Integer, String>> servers = new ArrayList<>();
        LookupSession s = LookupSession.defaultBuilder().build();
        try {
            Name mxLookup = Name.fromString(domain);
            s.lookupAsync(mxLookup, Type.MX)
                    .whenComplete(
                            (answers, ex) -> {
                                if (ex == null) {
                                    if (!answers.getRecords().isEmpty()) {
                                        for (Record rec : answers.getRecords()) {
                                            MXRecord mx = ((MXRecord) rec);
                                            servers.add(
                                                    new ImmutablePair<Integer, String>(
                                                        mx.getPriority(), mx.getTarget().toString()
                                                    )
                                            );
                                        }
                                    }
                                } else {
                                    ex.printStackTrace();
                                }
                            })
                    .toCompletableFuture()
                    .get();
        } catch (Exception e) {

        }
        servers.sort((p1, p2) -> {
            return p1.getLeft().compareTo(p2.getLeft());  // Reverse order
        });
        List<String> result = servers.stream().map((p) -> {return p.getRight();}).collect(Collectors.toList());
        return result;
    }

    /*public static boolean validateEmail(String email) {
        Matcher matcher = EMAIL_REGEX.matcher(email);
    }*/
}
