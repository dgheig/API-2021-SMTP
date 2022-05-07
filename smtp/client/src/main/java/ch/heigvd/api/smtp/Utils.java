package ch.heigvd.api.smtp;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class used to store utility methods used in the program
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

    public static <T> T interactiveChoice(List<T> values) {
        if(values == null || values.isEmpty()) {
            return null;
        }
        Scanner scanner = new Scanner(System.in);
        int index = interactiveChoiceIndex(values);
        if(index == -1)
            return null;
        return  values.get(index);
    }
    public static <T> int interactiveChoiceIndex(List<T> values) {
        int index = -1;
        if(values == null || values.isEmpty()) {
            return index;
        }
        Scanner scanner = new Scanner(System.in);
        while(true) {
            System.out.println("Available messages, use given index to select");
            for (int i = 0; i < values.size(); i++){
                System.out.println("[" + i + "] " + values.get(i) );
            }
            try {
                index = scanner.nextInt();
                if(index >= 0 && index <  values.size())
                    return  index;
                else
                    System.out.println("Index out of range");
            } catch (Exception e) {
                System.out.println("Invalid input");
            }
        }
    }
    public static <T> T interactiveChoice(T[] values) {
        if(values == null || values.length == 0) {
            return null;
        }
        return interactiveChoice(Arrays.asList(values));
    }
    public static <T> int interactiveChoiceIndex(T[] values) {
        if(values == null || values.length == 0) {
            return -1;
        }
        return interactiveChoiceIndex(Arrays.asList(values));
    }
}
