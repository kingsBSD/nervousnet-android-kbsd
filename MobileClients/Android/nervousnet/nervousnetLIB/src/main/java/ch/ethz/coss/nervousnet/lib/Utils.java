package ch.ethz.coss.nervousnet.lib;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.net.ConnectivityManager;

import java.util.Hashtable;

public class Utils {

    private static Hashtable<Integer, ErrorReading> hErrorReadings = new Hashtable<Integer, ErrorReading>() {{
        put(101, new ErrorReading(new String[]{"101", "Nervousnet is switched off."}));
        put(102, new ErrorReading(new String[]{"102", "Security Exception - Cannot bind to nervousnet HUB service. Missing or denied Permission. 'ch.ethz.coss.nervousnet.hub.BIND_PERM'"}));
        put(103, new ErrorReading(new String[]{"103", "Unkown Exception - Unable to bind to service."}));
        put(104, new ErrorReading(new String[]{"104", "Remote Exception - in onServiceConnected."}));
        put(201, new ErrorReading(new String[]{"201", "Sensor unavailable."}));
        put(202, new ErrorReading(new String[]{"202", "Sensor permission denied by user."}));
        put(203, new ErrorReading(new String[]{"203", "Sensor is switched off."}));
        put(204, new ErrorReading(new String[]{"204", "Sensor returned a null object."}));
        put(301, new ErrorReading(new String[]{"301", "getReading Callback Exception occured"}));

    }};

    public static void displayAlert(Context context, String title, String message, String posButtonTitle,
                                    OnClickListener posOnClickListener, String negButtonTitle, OnClickListener negOnClickListener) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set title
        alertDialogBuilder.setTitle(title);

        // set dialog message
        alertDialogBuilder.setMessage(message).setCancelable(false).setPositiveButton(posButtonTitle,
                posOnClickListener);
        if (negButtonTitle != null) {
            alertDialogBuilder.setNegativeButton(negButtonTitle, negOnClickListener);
        }

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public static String getConnectivityTypeString(int networkType) {

        switch (networkType) {
            case ConnectivityManager.TYPE_MOBILE:
                return "Mobile";
            case ConnectivityManager.TYPE_WIFI:
                return "WiFi";
            case ConnectivityManager.TYPE_BLUETOOTH:
                return "Bluetooth";
            default:
                return "Other";
        }

    }

    public static ErrorReading getErrorReading(int errorCode) {

        return hErrorReadings.get(errorCode);
    }
}
