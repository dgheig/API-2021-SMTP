package ch.heigvd.api.smtp;

import ch.heigvd.api.smtp.emailsGrouping.EmailsGrouping;
import ch.heigvd.api.smtp.emailsGrouping.MinimalEmailsGrouping;
import ch.heigvd.api.smtp.emailsRetrievers.EmailsRetriever;
import ch.heigvd.api.smtp.emailsRetrievers.TxtFileParsor;
import ch.heigvd.api.smtp.messageRetrievers.AskMessageFile;
import ch.heigvd.api.smtp.messageRetrievers.MessageRetriever;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class CampaignManager {

    /* Private variables */
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-+]+(.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$";
    private final int MIN_GROUP_MEMBERS;
    private final EmailsGrouping emailsGrouping;
    private final EmailsRetriever emailsRetriever;
    private final MessageRetriever messageRetriever;

    /**
     * The constructor to use if the email folder is the default one
     *
     * @param emailList
     *            name of the file containing email addresses
     */
    public CampaignManager(
            EmailsGrouping emailsGrouping,
            EmailsRetriever emailsRetriever,
            MessageRetriever messageRetriever,
            int minGroupMembers) {
        super();
        this.emailsGrouping = emailsGrouping;
        this.emailsRetriever = emailsRetriever;
        this.messageRetriever = messageRetriever;
        this.MIN_GROUP_MEMBERS = minGroupMembers;
    }

    /**
     * The constructor to use if the email folder is the default one
     *
     * @param emailList
     *            name of the file containing email addresses
     */
    public CampaignManager(
            EmailsGrouping emailsGrouping,
            EmailsRetriever emailsRetriever,
            MessageRetriever messageRetriever) {
        this(
                emailsGrouping,
                emailsRetriever,
                messageRetriever,
                3);
    }

    /**
     * The constructor to use if the email folder is the default one
     *
     * @param emailList
     *            name of the file containing email addresses
     */
    public CampaignManager(String emailList, String messageFolder) {
        this(
                new MinimalEmailsGrouping(3),
                new TxtFileParsor(emailList),
                new AskMessageFile(messageFolder),
                3);
    }

    /**
     * The constructor to use to specify a custom folder for email location
     *
     * @param emailList
     *            name of the file containing email addresses
     * @param messageFolder
     *            path of the folder containing email messages
     */
    public CampaignManager(String emailList) {
        this(emailList, ".\\smtp\\client\\rsc\\messages\\");
    }

    /**
     * @brief getMailingGroups make mailing groups from email address file
     * @param numberOfGroups
     *            the number of groups we want to created
     * @return a vector of vector of email addresses. Each subvector represents a
     *         mailing group
     */
    public List<List<String>> getMailingGroups() throws Exception {
        List<String> emails = emailsRetriever.getEmails();
        for (String email : emails) {
            if (!Utils.validateEmail(email)) {
                throw new Exception("Invalid email found: " + email);
            }
        }
        /* Get number of email addresses in file */
        int nbEmailAddresses = emails.size();
        int maxGroupsPossible = nbEmailAddresses / MIN_GROUP_MEMBERS;

        /*
         * If less than 2, cannot start campaign, at least one sender and two recievers
         * requiered
         */
        if (nbEmailAddresses < MIN_GROUP_MEMBERS) {
            throw new Exception(
                    "Not enough email addresses to start campaign. At least " + MIN_GROUP_MEMBERS + " required");
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("How many group do you want to make?");
        int numberOfGroups = scanner.nextInt();

        /*
         * Check that the number of requested groups can be created with the email
         * addresses in file
         */
        if (numberOfGroups > maxGroupsPossible) {
            System.out.println("CAMPAIGN MANAGER::getMailingGroups() - Not enough email address to make "
                    + numberOfGroups + " groups.");
            if (!Utils.askConfirmation(
                    "Continue using " + maxGroupsPossible + " groups ?")) {
                return null;
            }

        }
        List<List<String>> groups = emailsGrouping.group(emails);

        return groups;

    }

    public void start(String server, int port) {
        System.out.println("CAMPAIGN MANAGER::How can I help you ?");
        try {
            List<List<String>> groups = getMailingGroups();
            if (groups == null)
                return;
            for (List<String> recipients : groups) {
                List<String> message = messageRetriever.getMessage();
                int index = new Random().nextInt(recipients.size());
                String sender = recipients.remove(index);

                Client.sendEmails(server, port, sender, recipients, message);
            }
        } catch (Exception e) {
            System.out.println("An error occured: " + e.getMessage());
            System.out.println("Aborting");
        }
    }
}
