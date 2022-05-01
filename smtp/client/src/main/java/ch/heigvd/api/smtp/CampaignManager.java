package ch.heigvd.api.smtp;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CampaignManager {

    /* Private variables */
    private String _emailList;
    private String _messageList;
    private String _ressourcesPath = ".\\smtp\\client\\rsc\\";
    private final int MIN_GROUP_MEMBERS = 3;
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-+]+(.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$";

    /* Constructor */
    public CampaignManager(String emailList, String messageList){
        this._emailList = _ressourcesPath + emailList;
        this._messageList = _ressourcesPath + messageList;
    }

    /* Method to count lines in a file */
    private int getLineCount(String file) {
        int lineCount = 0;

        try (FileReader email_fr = new FileReader(file, StandardCharsets.UTF_8);
             BufferedReader email_br = new BufferedReader(email_fr))
        {
            while((email_br.readLine()) != null) {
                lineCount++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return lineCount;
    }

    /* Method to create mailing groups */
    public Vector<Vector<String>> getMailingGroups(int numberOfGroups){

        /* Get number of email addresses in file */
        int nbEmailAddresses = getLineCount(this._emailList);
        int maxGroupsPossible = nbEmailAddresses / MIN_GROUP_MEMBERS;

        /* If less than 2, cannot start campaign, at least one sender and two recievers requiered */
        if (nbEmailAddresses < 3){
            System.out.println("CAMPAIGN MANAGER::Not enough email addresses to start campaign. At least 3 required");
            return null;
        }

        /* Check that the number of requested groups can be created with the email addresses in file */
        if (numberOfGroups > maxGroupsPossible){
            System.out.println("CAMPAIGN MANAGER::Not enough email address to make " + numberOfGroups + " groups.");
            System.out.println("CAMPAIGN MANAGER::Continue using " + maxGroupsPossible + " groups ? [y/n]");

            /* Read user confirmation */
            Scanner scanner = new Scanner(System.in);
            String input ="";
            Boolean yesno = true;

            while (yesno){

                input = scanner.nextLine();

                switch(input){
                    case "y":
                    case "Y":
                        numberOfGroups = maxGroupsPossible;
                        yesno = false;
                        break;
                    case "n":
                    case "N":
                        return null;
                    default:
                        break;
                }
            }
        }


        Vector<Vector<String>> groups = new Vector<>(numberOfGroups);
        Vector<String> emails = new Vector<>(nbEmailAddresses);
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher;

        /* Read email addresses from file */
        try (FileReader email_fr = new FileReader(this._emailList, StandardCharsets.UTF_8);
             BufferedReader email_br = new BufferedReader(email_fr))
        {

            String emailAddress = "";


            while (email_br.ready()){
                /* Read an email address from file */
                emailAddress = email_br.readLine();
                /* Check that email address is valid */
                matcher = pattern.matcher(emailAddress);
                if (!matcher.matches()){
                    System.out.println("CAMPAIGN MANAGER::Invalid email address found in file: " + emailAddress + ". Exiting...");
                    return null;
                }

                /* Add element to vector containing all email addresses */
                emails.add(emailAddress);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        /* Create groups */
        int e = 0;
        int groupsRemaining = numberOfGroups;
        for (int g = 0; g < numberOfGroups; g++){
            groups.add(new Vector<>());
            while (e < nbEmailAddresses){
                groups.elementAt(g).add(emails.elementAt(e));
                e++;
                if (e % MIN_GROUP_MEMBERS == 0 && groupsRemaining > 1){
                    groupsRemaining--;
                    break;
                }
            }


        }

        return groups;

    }


    public static void main(String[] args) {
        System.out.println("CAMPAIGN MANAGER::How can I help you ?");

        CampaignManager cm = new CampaignManager("emails.txt", "messages.txt");

        Vector<Vector<String>> groups = cm.getMailingGroups(2);

        for (int i = 0; i < groups.size(); i++){
            for (int j = 0; j < groups.elementAt(i).size(); j++){
                System.out.println(i + ":" + j + "=" + groups.elementAt(i).get(j));
            }
        }


    }
}