package ch.heigvd.api.smtp;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    private final static Pattern EMAIL_REGEX = Pattern.compile("^[_A-Za-z0-9-+]+(.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$");

    public static boolean validateEmail(String email) {
        Matcher matcher = EMAIL_REGEX.matcher(email);
        return matcher.find();
    }
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
