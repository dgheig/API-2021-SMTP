package ch.heigvd.api.smtp;

import ch.heigvd.api.smtp.emailsGrouping.MinimalEmailsGrouping;
import ch.heigvd.api.smtp.emailsRetrievers.TxtFileParsor;
import ch.heigvd.api.smtp.messageRetrievers.AskMessageFile;
import ch.heigvd.api.smtp.messageRetrievers.RandomMessage;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.util.concurrent.Callable;

@Command(name = "SMTPClientAttack", mixinStandardHelpOptions = true, version = "SMTPClientAttack 1.0", description = "Spam your friends")
public class Main implements Callable<Integer> {

    @Parameters(index = "0", description = "Campaign Config file")
    private File configFile;

    @Override
    public Integer call() throws Exception { // your business logic goes here...
        Config config = Config.get(configFile);
        CampaignManager cm = new CampaignManager(
                new MinimalEmailsGrouping(3),
                new TxtFileParsor(config.getEmailsFile()),
                new AskMessageFile(config.getMessageFolder()));
        cm.start(config.getServer(), config.getPort());
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
