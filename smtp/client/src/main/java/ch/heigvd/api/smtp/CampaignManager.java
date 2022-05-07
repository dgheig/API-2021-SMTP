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
    private final int MIN_GROUP_MEMBERS;
    private final EmailsGrouping emailsGrouping;
    private final EmailsRetriever emailsRetriever;
    private final MessageRetriever messageRetriever;

    /**
     * The constructor to use to define a custom number of users per group
     * @param emailsGrouping an instance of a class implementing the EmailsGrouping interface
     * @param emailsRetriever an instance of a class implementing the EmailsRetriever interface
     * @param messageRetriever an instance of a class implementing the MessageRetriever interface
     * @param minGroupMembers the minimum number of email addresses composing a group
     */
    public CampaignManager(
            EmailsGrouping emailsGrouping,
            EmailsRetriever emailsRetriever,
            MessageRetriever messageRetriever,
            int minGroupMembers) {
        this.emailsGrouping = emailsGrouping;
        this.emailsRetriever = emailsRetriever;
        this.messageRetriever = messageRetriever;
        this.MIN_GROUP_MEMBERS = minGroupMembers;
    }

    /**
     * The default constructor
     * @param emailsGrouping an instance of a class implementing the EmailsGrouping interface
     * @param emailsRetriever an instance of a class implementing the EmailsRetriever interface
     * @param messageRetriever an instance of a class implementing the MessageRetriever interface
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
     * Constructor used to specify a custom email address file and message folder
     * @param emailList the path of the email list file
     * @param messageFolder the path of the message folder
     */
    public CampaignManager(String emailList, String messageFolder) {
        this(
                new MinimalEmailsGrouping(3),
                new TxtFileParsor(emailList),
                new AskMessageFile(messageFolder),
                3);
    }

    /**
     * Constructor used for specifying a custom email list, but using the default message folder
     * @param emailList the path of the email list file
     */
    public CampaignManager(String emailList) {
        this(emailList, ".\\smtp\\client\\rsc\\messages\\");
    }

    /**
     * Method used to create multiple mailing groups
     * @return a list of list of strings, where the encapsulating list contains groups, and each groups and represented
     *         as a list of email addresses
     * @throws Exception Custom exception if there was an error while creating mailing groups
     */
    private List<List<String>> getMailingGroups() throws Exception {
        List<String> emails = emailsRetriever.getEmails();

        // Validate that all emails in file are valid
        for (String email : emails) {
            if (!Utils.validateEmail(email)) {
                throw new Exception("Invalid email found: " + email);
            }
        }

        // Get number of email addresses in file
        int nbEmailAddresses = emails.size();
        // Compute the maximum of groups that can be made with the number of email addresses
        int maxGroupsPossible = nbEmailAddresses / MIN_GROUP_MEMBERS;

        //If less than 3, cannot start campaign, at least one sender and two receivers required
        if (nbEmailAddresses < MIN_GROUP_MEMBERS) {
            throw new Exception(
                    "Not enough email addresses to start campaign. At least " + MIN_GROUP_MEMBERS + " required");
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("How many groups do you want to make?");
        int numberOfGroups = scanner.nextInt();

        // Check that the number of requested groups can be created with the email addresses in file
        if (numberOfGroups > maxGroupsPossible) {
            System.out.println("Not enough email address to make " + numberOfGroups + " groups.");
            // If not, propose maximum number of possible groups
            if (!Utils.askConfirmation(
                    "Continue using " + maxGroupsPossible + " groups ?")) {
                return null;
            }

        }
        // Build the return object from emailsGrouping implementing class
        List<List<String>> groups = emailsGrouping.group(emails);

        return groups;

    }


    /**
     * Method to run the mailing campaign
     * @param server the ip address of the server we want to send messages to
     * @param port the tcp port on the server on which to bind our socket
     */
    public void start(String server, int port) {

        try {
            // Build mailing groups
            List<List<String>> groups = getMailingGroups();
            if (groups == null)
                return;
            // For each mailing group, create sender, recipients and message
            for (List<String> recipients : groups) {
                List<String> message = messageRetriever.getMessage();
                if(message == null) {
                    System.out.println("An error occured when retrieving the message");
                    continue;
                }
                int index = new Random().nextInt(recipients.size());
                String sender = recipients.remove(index);
                // Send the message
                Client.sendEmails(server, port, sender, recipients, message);
            }
        } catch (Exception e) {
            System.out.println("An error occured: " + e.getMessage());
            System.out.println("Aborting");
        }
    }
}
