package ch.ethz.coss.nervousnet.vm.configuration;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;

import ch.ethz.coss.nervousnet.vm.sensors.AndroidSensor;

/**
 * Intention of this class is to hold configuration values for a physical sensor,
 * such as Accelerometer, Light sensor, Battery, etc., in attributes for fast
 * and easy access to the values.
 */
public class BasicSensorConfiguration extends GeneralSensorConfiguration {

    /**
     * This attribute holds type of a sensor as specified in documentation
     * for {@link android.hardware.Sensor}
     */
    private int androidSensorType;
    /**
     * This attribute specifies positions of the sensor values to be selected.
     * Follow the documentation {@link android.hardware.SensorEvent}.
     */
    private int[] androidParametersPositions;
    /**
     * List of sampling rates.
     */
    private ArrayList<Long> samplingRates;
    /**
     * This attribute contains information of the state of the sensor. If
     * it is 0, then the sampling rate is set to -1. If it is positive
     * integer, then it represents an index in samplingRates and the value
     * at that position is actual sampling rate. The indexing starts counting
     * with 1 and not 0 as the value 0 is reserved as described before.
     */
    private int state;
    /**
     * This is name of the class in {@link ch.ethz.coss.nervousnet.vm.sensors}
     */
    private String sensorListenerName;

    /**
     * @param sensorID unique sensor identifier
     * @param sensorName sensor name
     * @param androidSensorType type of a sensor as specified in documentation
     * for {@link android.hardware.Sensor}
     * @param parametersNames parameter names of the sensor
     * @param parametersTypes types of the sensor in Java notation
     * @param androidParametersPositions specifies positions of the sensor values to be selected.
     * Follow the documentation {@link android.hardware.SensorEvent}.
     * @param samplingRates list of possible sampling rates
     * @param state contains information of the state of the sensor. If
     * it is 0, then the sampling rate is set to -1. If it is positive
     * integer, then it represents an index in samplingRates and the value
     * at that position is actual sampling rate. The indexing starts counting
     * with 1 and not 0 as the value 0 is reserved as described before.
     */
    public BasicSensorConfiguration(int sensorID, String sensorName, int androidSensorType,
                                    ArrayList<String> parametersNames,
                                    ArrayList<String> parametersTypes,
                                    int[] androidParametersPositions,
                                    ArrayList<Long> samplingRates,
                                    int state) {
        super(sensorID, sensorName, parametersNames, parametersTypes);
        this.androidSensorType = androidSensorType;
        this.androidParametersPositions = androidParametersPositions;
        this.samplingRates = samplingRates;
        this.sensorListenerName = AndroidSensor.class.getSimpleName();
        setState(state);
    }

    /**
     * @param sensorID unique sensor identifier
     * @param sensorName sensor name
     * for {@link android.hardware.Sensor}
     * @param parametersNames parameter names of the sensor
     * @param parametersTypes types of the sensor in Java notation
     * Follow the documentation {@link android.hardware.SensorEvent}.
     * @param samplingRates list of possible sampling rates
     * @param state contains information of the state of the sensor. If
     * it is 0, then the sampling rate is set to -1. If it is positive
     * integer, then it represents an index in samplingRates and the value
     * at that position is actual sampling rate. The indexing starts counting
     * with 1 and not 0 as the value 0 is reserved as described before.
     */
    public BasicSensorConfiguration(int sensorID, String sensorName, ArrayList<String> parametersNames,
                                    ArrayList<String> parametersTypes,
                                    String wrapperName,
                                    ArrayList<Long> samplingRates,
                                    int state) {
        super(sensorID, sensorName, parametersNames, parametersTypes);
        this.samplingRates = samplingRates;
        this.sensorListenerName = wrapperName;
        setState(state);
    }


    /**
     * @param sensorID unique sensor identifier
     * @param sensorName sensor name
     * for {@link android.hardware.Sensor}
     * @param parametersNames parameter names of the sensor
     * @param parametersTypes types of the sensor in Java notation
     * Follow the documentation {@link android.hardware.SensorEvent}.
     */
    public BasicSensorConfiguration(int sensorID, String sensorName, ArrayList<String> parametersNames,
                                    ArrayList<String> parametersTypes) {
        super(sensorID, sensorName, parametersNames, parametersTypes);
    }

    /**
     * @return The name of the class from
     * {@link ch.ethz.coss.nervousnet.vm.sensors} that handles the sensor.
     */
    public String getWrapperName() {
        return sensorListenerName;
    }

    /**
     * @return Type of the sensor as specified in documentation
     * for {@link android.hardware.Sensor}.
     */
    public int getAndroidSensorType() {
        return androidSensorType;
    }

    /**
     *
     * @return Selected positions of the sensor values.
     * Follow the documentation {@link android.hardware.SensorEvent} to see the values.
     */
    public int[] getAndroidParametersPositions() {
        return androidParametersPositions;
    }

    /**
     *
     * @return Actual sampling rate.
     */
    public long getActualSamplingRate() {
        if (state <= 0)
            return -1;
        else
            return samplingRates.get(state-1);
    }

    public void setState(int state){
        this.state = state;
    }

    /**
     *
     * @return List of possible sampling rates.
     */
    public ArrayList<Long> getSamplingRates() {
        return samplingRates;
    }

    /**
    * @return State.
     */
    public int getState() {
        return state;
    }

    @Override
    public String toString() {
        return "ConfigurationClass{" +
                "sensorName='" + sensorName + '\'' +
                ", androidSensorType=" + androidSensorType +
                ", parametersNames=" + TextUtils.join(", ", parametersNames) +
                ", parametersTypes=" + TextUtils.join(", ", parametersTypes) +
                ", androidParametersPositions=" + Arrays.toString(androidParametersPositions) +
                ", state=" + state +
                '}';
    }
}
