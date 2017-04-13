package ch.ethz.coss.nervousnet.vm;

import android.content.Context;
import android.util.Log;

public class NNLog {
    private static boolean isDebug = true;

    public static void init(Context context) {
//         String pName = context.getPackageName();
//        if (pName != null && pName.endsWith(".debug")) {
//            isDebug = true;
//        } else {
//            isDebug = false;
//        }
    }

    public static void d(String tag, String message) {
        if (BuildConfig.DEBUG || isDebug)
            Log.d(tag, message);
    }


}
