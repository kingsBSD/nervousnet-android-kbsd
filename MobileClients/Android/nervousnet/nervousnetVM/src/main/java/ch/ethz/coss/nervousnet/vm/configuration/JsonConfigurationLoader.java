package ch.ethz.coss.nervousnet.vm.configuration;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * JsonConfigurationLoader loads configuration file from the assets.
 * TODO: Should be improved in terms of default values and method structure.
 */
public class JsonConfigurationLoader {

    private String CONF_FILE_NAME = "sensors_configuration.json";
    private static final String LOG_TAG = JsonConfigurationLoader.class.getSimpleName();
    private Context context;

    protected JsonConfigurationLoader(Context context) {
        this.context = context;
    }

    /**
     * Load sensor configurations from configuration file.
     * @return List of sensor configurations.
     */
    protected ArrayList<BasicSensorConfiguration> load() {
        String line,total = "";
        try
        {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open(CONF_FILE_NAME)));
            try {
                while ((line = reader.readLine()) != null)
                    total+=line;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        catch (Exception e){
            Log.d(LOG_TAG, "ERROR " + e.getMessage());
        }
        return load(total);
    }

    /**
     * Load sensor configurations from a json string.
     * @param strJson Json string with sensor configurations.
     * @return List of sensor configurations.
     */
    protected static ArrayList<BasicSensorConfiguration> load(String strJson){
        ArrayList<BasicSensorConfiguration> list = new ArrayList();
        try {
            JSONArray sensorConfList = (new JSONObject(strJson)).getJSONArray("sensors_configurations");
            for (int i = 0; i < sensorConfList.length(); i++) {
                JSONObject sensorConf = sensorConfList.getJSONObject(i);
                BasicSensorConfiguration confClass = null;
                // MANDATORY
                int sensorID = sensorConf.getInt("sensorID");
                String sensorName = sensorConf.getString("sensorName");
                ArrayList<String> paramNames = convertToArr(sensorConf.getJSONArray("parametersNames"));
                ArrayList<String> paramTypes = convertToArr(sensorConf.getJSONArray("parametersTypes"));
                int state = 0;
                if (sensorConf.has("initialState"))
                state = sensorConf.getInt("initialState");
                ArrayList<Long> samplingRates = new ArrayList<Long>();

                if (sensorConf.has("samplingRates"))
                samplingRates=    convertToArrLong(sensorConf.getJSONArray("samplingRates"));
                // OPTIONAL
                // This one is for simple android sensors
                if (sensorConf.has("androidSensorType") && sensorConf.has("androidParametersPositions")) {
                    int androidSensorType = sensorConf.getInt("androidSensorType");
                    int[] positions = convertToIntArr(sensorConf.getJSONArray("androidParametersPositions"));
                    confClass = new BasicSensorConfiguration(sensorID, sensorName, androidSensorType,
                            paramNames, paramTypes, positions, samplingRates, state);
                }
                // This one is for other sensors
                else if (sensorConf.has("wrapperName")) {
                    String wrapperName = sensorConf.getString("wrapperName");
                    confClass = new BasicSensorConfiguration(sensorID, sensorName,
                            paramNames, paramTypes, wrapperName, samplingRates, state);
                } else {
                    confClass = new BasicSensorConfiguration(sensorID, sensorName,
                            paramNames, paramTypes);
                }
                list.add(confClass);
                Log.d(LOG_TAG, confClass.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return list;
    }


    /**
     * Convert JSONArray into ArrayList
     * @param jList
     * @return
     */
    private static ArrayList<String> convertToArr(JSONArray jList){
        int len = jList.length();
        ArrayList<String> arr = new ArrayList();
        for (int i = 0; i < len; i++){
            try {
                arr.add(jList.getString(i));
            } catch (JSONException e) {
                arr.add( null );
                e.printStackTrace();
            }
        }
        return arr;
    }


    /**
     * Convert JSONArray of long values into ArrayList of long values.
     * @param jList
     * @return
     */
    private static ArrayList<Long> convertToArrLong(JSONArray jList){
        int len = jList.length();
        ArrayList<Long> arr = new ArrayList();
        for (int i = 0; i < len; i++){
            try {
                arr.add(jList.getLong(i));
            } catch (JSONException e) {
                arr.add( null );
                e.printStackTrace();
            }
        }
        return arr;
    }


    /**
     * Convert JSONArray list of int values into ArryaList of int values.
     * @param jList
     * @return
     */
    private static int[] convertToIntArr(JSONArray jList){
        int len = jList.length();
        int[] list = new int[len];
        for (int i = 0; i < len; i++){
            try {
                list[i] = jList.getInt(i);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        return list;
    }

}


