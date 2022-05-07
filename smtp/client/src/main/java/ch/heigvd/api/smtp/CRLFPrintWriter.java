package ch.heigvd.api.smtp;

import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Class extending the PrintWriter class
 * This class is used to use the CRLF line feed when using println function
 */
public class CRLFPrintWriter extends PrintWriter {
    /**
     * Constructor
     * @param out the ouputstream to which to write
     */
    CRLFPrintWriter(OutputStream out) {
        super(out);
    }

    /**
     * Overridden println method adding custom line ending
     * @param x the string to which to append the line ending
     */
    @Override
    public void println(String x) {
        super.print(x + "\r\n");
    }
}
