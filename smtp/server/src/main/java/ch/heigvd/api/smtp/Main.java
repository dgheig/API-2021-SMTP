package ch.heigvd.api.smtp;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
// import picocli.CommandLine.Parameters;
import java.util.concurrent.Callable;

@Command(name = "SMTPServer", mixinStandardHelpOptions = true, version = "SMTPServer 1.0", description = "Simple SMTP server")
public class Main implements Callable<Integer> {
    @Option(names = { "-n", "--name" }, description = "name of the server to use")
    private String serverName = "anonymous";
    @Option(names = { "-p", "--port" }, description = "port to use")
    private Integer port = 25;
    /*
     * @Option(names = { "--server" }, description =
     * "Fixed server to forward emails to")
     * private String fixedServer;
     * 
     * @Option(names = {
     * "--server-port" }, description =
     * "Port of the fixed server to forward emails to (useless without --server option"
     * )
     * private Integer fixedServerPort = 25;
     */

    @Override
    public Integer call() throws Exception { // your business logic goes here...
        (new Server(serverName, port)).start();
        return 0;
    }

    public static void main(String[] args) {
        // Log output on a single line
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");
        System.setProperty("line.separator", "\r\n");
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }
}
