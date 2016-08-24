package ch.ethz.coss.nervousnet.vm;

import ch.ethz.coss.nervousnet.lib.LibConstants;

public class NervousnetVMConstants {

    public static final int STATE_PAUSED = 0;
    public static final int STATE_RUNNING = 1;

    /******************
     * Preferences
     ****************/
    public final static String SENSOR_PREFS = "SensorPreferences";
    public final static String SENSOR_FREQ = "SensorFrequencies";
    public final static String SERVICE_PREFS = "ServicePreferences";
    public final static String UPLOAD_PREFS = "UploadPreferences";

    public final static int REQUEST_ENABLE_BT = 0;


    public static long[] sensor_ids = {LibConstants.SENSOR_ACCELEROMETER,
            LibConstants.SENSOR_BATTERY,
            LibConstants.SENSOR_GYROSCOPE,
            LibConstants.SENSOR_LOCATION,
            LibConstants.SENSOR_LIGHT,
            LibConstants.SENSOR_NOISE,
            LibConstants.SENSOR_NOTIFICATION,
            LibConstants.SENSOR_PROXIMITY,
            LibConstants.SENSOR_TRAFFIC
    };

    public static String[] sensor_labels = {"Accelerometer", "Battery", "Gyroscope",
            "Location", "Light", "Noise", "Notification", "Proximity", "Network Traffic"};

//	public static long[] sensor_ids = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
//
//	public static String[] sensor_labels = { "Accelerometer", "Battery", "Beacons", "Connectivity", "Gyroscope",
//			"Humidity", "Location", "Light", "Magnetic", "Noise", "Pressure", "Proximity", "Temperature" };

    public static String[] sensor_freq_labels = {"Off", "High", "Medium", "Low"};

    public static int[][] sensor_freq_constants = {{-1, 60000, 120000, 300000}, {-1, 60000, 120000, 300000},
            {-1, 60000, 120000, 300000}, {-1, 60000, 120000, 300000}, {-1, 60000, 120000, 300000}, {-1, 60000, 120000, 300000},
            {-1, 60000, 120000, 300000}, {-1, 60000, 120000, 300000}, {-1, 60000, 120000, 300000}
    };

    public static byte SENSOR_STATE_NOT_AVAILABLE = -2;
    public static byte SENSOR_STATE_AVAILABLE_PERMISSION_DENIED = -1;
    public static byte SENSOR_STATE_AVAILABLE_BUT_OFF = 0;
    public static byte SENSOR_STATE_AVAILABLE_DELAY_HIGH = 1;
    public static byte SENSOR_STATE_AVAILABLE_DELAY_MED = 2;
    public static byte SENSOR_STATE_AVAILABLE_DELAY_LOW = 3;

}
