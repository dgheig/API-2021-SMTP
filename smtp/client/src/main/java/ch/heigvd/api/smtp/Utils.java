package ch.heigvd.api.smtp;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class used to store utility methods used in program
 */
public class Utils {
    private final static Pattern EMAIL_REGEX = Pattern.compile("^[_A-Za-z0-9-+]+(.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$");

    public static String substring(String str, int index) {
        if(str.length() > index)
            return str.substring(index);
        return "";
    }

    /**
     * Method to validate an email address
     * @param email the email address to validate
     * @return true if the email address is valid, false otherwise
     */
    public static boolean validateEmail(String email) {
        Matcher matcher = EMAIL_REGEX.matcher(email);
        return matcher.find();
    }

    /**
     * Method used to ask user for confirmation
     * @param message The message displayed to user
     * @return true if user accepted, false otherwise
     */
    public static boolean askConfirmation(String message) {

        /* Read user confirmation */
        Scanner scanner = new Scanner(System.in);
        while (true){
            System.out.println(message + " [y/n]");
            switch(scanner.nextLine()){
                case "y":
                case "Y":
                    return true;
                case "n":
                case "N":
                    return false;
            }
            System.out.println("Invalid input");
        }
    }
}
