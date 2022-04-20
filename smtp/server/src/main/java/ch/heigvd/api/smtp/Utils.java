package ch.heigvd.api.smtp;

import org.apache.commons.text.StringEscapeUtils;

public class Utils {
    public static String substring(String str, int index) {
        if(str.length() > index)
            return str.substring(index);
        return "";
    }
    public static String options(String expression, String command) {
        if(expression.startsWith(command))
            return substring(expression, command.length());
        return null;
    }

    public static String unescape(String str) {
        return StringEscapeUtils.escapeJava(str);
    }
}
