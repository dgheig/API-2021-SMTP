package ch.heigvd.api.smtp;

import java.io.OutputStream;
import java.io.PrintWriter;

public class CRLFPrintWriter extends PrintWriter {
    CRLFPrintWriter(OutputStream out) {
        super(out);
    }

    @Override
    public void println(String x) {
        super.print(x + "\r\n");
    }
}
