package main.java.ch.heigvd.api.smtp;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CampaignManager {

    /* Private variables */
    private final String _emailList;
    private final String _resourcesPath = ".\\smtp\\client\\rsc\\";
    private String _messageFolder = ".\\smtp\\client\\rsc\\messages\\";
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-+]+(.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$";
    private final int MIN_GROUP_MEMBERS = 3;

    /* Constructor */
    public CampaignManager(String emailList){
        this._emailList = _resourcesPath + emailList;
        System.out.println("CAMPAIGN MANAGER::Constructor - Your email file is read here: " + this._emailList);
        //System.out.println("CAMPAIGN MANAGER::Constructor - Your message folder is here: " + this._messageFolder);
    }

    /* Constructor with custom message folder path*/
    public CampaignManager(String emailList, String messageFolder){
        this._emailList = _resourcesPath + emailList;
        this._messageFolder = messageFolder;
        System.out.println("CAMPAIGN MANAGER::Constructor - Your email file is read here: " + this._emailList);
        System.out.println("CAMPAIGN MANAGER::Constructor - Your message folder is here: " + this._messageFolder);
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
            System.out.println("CAMPAIGN MANAGER::getLineCount() " + e);
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
            System.out.println("CAMPAIGN MANAGER::getMailingGroups() - Not enough email addresses to start campaign. At least 3 required");
            return null;
        }

        /* Check that the number of requested groups can be created with the email addresses in file */
        if (numberOfGroups > maxGroupsPossible){
            System.out.println("CAMPAIGN MANAGER::getMailingGroups() - Not enough email address to make " + numberOfGroups + " groups.");
            System.out.println("CAMPAIGN MANAGER::getMailingGroups() - Continue using " + maxGroupsPossible + " groups ? [y/n]");

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
                    System.out.println("CAMPAIGN MANAGER::getMailingGroups() - Invalid email address found in file: " + emailAddress + ". Exiting...");
                    return null;
                }

                /* Add element to vector containing all email addresses */
                emails.add(emailAddress);
            }

        } catch (IOException e) {
            System.out.println("CAMPAIGN MANAGER::getMailingGroups() - " + e);
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

    public void getMessages(){

        File messageDirectory = new File(this._messageFolder);
        String files[] = messageDirectory.list();

        if (files == null){
            System.out.println("CAMPAIGN MANAGER::getMessages() - Directory does not exist or cannot be read");
            return;
        }

        if (files.length == 0){
            System.out.println("CAMPAIGN MANAGER::getMessages() - No files found in message folder");
            return;
        }

        System.out.println("CAMPAIGN MANAGER::getMessages() - Available messages, use given index to select");
        for (int i = 0; i < files.length; i++){
            System.out.println("[" + i + "] " + files[i] );
        }

    }

    public String getMessageAsUtf8(int index) {
        File messageDirectory = new File(this._messageFolder);
        String files[] = messageDirectory.list();
        String file;
        StringBuilder sb = new StringBuilder();
        int c;

        if (index >= 0 && index < files.length) {
            file = files[index];
        } else {
            System.out.println("CAMPAIGN CampaignManager::getMessageAsUtf8 - Index out of range");
            return "";
        }

        try (FileReader fr = new FileReader(this._messageFolder + file, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(fr))
        {
            while ((c = br.read()) != -1){
                sb.append((char) c);
            }



        } catch (Exception e){
            System.out.println("CAMPAIGN MANAGER::getMessageAsUtf8() - " + e);
        }

        return String.valueOf(sb);

    }


    public static void main(String[] args) {
        System.out.println("CAMPAIGN MANAGER::How can I help you ?");

        CampaignManager cm = new CampaignManager("emails.txt");
        //CampaignManager cm2 = new CampaignManager("emails.txt", "C:\\Users\\yan61\\OneDrive\\Documents\\HEIG-VD\\4. ANNEE\\RES\\Labos\\labo04\\messages");

        //Vector<Vector<String>> groups = cm.getMailingGroups(2);
        /*
        for (int i = 0; i < groups.size(); i++){
            for (int j = 0; j < groups.elementAt(i).size(); j++){
                System.out.println(i + ":" + j + "=" + groups.elementAt(i).get(j));
            }
        }
        */

        cm.getMessages();
        String test = cm.getMessageAsUtf8(0);
        System.out.println(test);


    }
}