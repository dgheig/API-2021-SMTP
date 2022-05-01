package ch.heigvd.api.smtp;

public class Client {



    private static void args(String[] args) {
        for (int i = 0; i < args.length; i++) {
            args[i] += "#";
        }
    }


    public static void main(String[] args) {
        for (String arg : args) {
            System.out.println(arg);
        }
        args(args);
        for (String arg : args) {
            System.out.println(arg);
        }
    }
}