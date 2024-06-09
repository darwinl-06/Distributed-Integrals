package implementation;
import Demo.Printer;
import Demo.PrinterCallbackPrx;
import com.zeroc.Ice.Current;

import javax.security.auth.callback.Callback;

public class PrinterImpl implements Demo.Printer {

    public String printString (String e, PrinterCallbackPrx callback, Current current) {
        return "";
    }

}
