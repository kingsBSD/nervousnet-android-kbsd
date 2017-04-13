package ch.ethz.coss.nervousnet.vm.configuration;

import android.text.TextUtils;

import java.util.ArrayList;

/**
 * GeneralSensorConfiguration describes mandatory fields for a sensor.
 */
public class GeneralSensorConfiguration {

    protected long sensorID;
    protected String sensorName;
    protected ArrayList<String> parametersNames;
    protected ArrayList<String> parametersTypes;
    protected int dimensions;

    public GeneralSensorConfiguration(long sensorID, String sensorName, ArrayList<String> parametersNames,
                                      ArrayList<String> parametersTypes) {
        this.sensorID = sensorID;
        this.sensorName = sensorName;
        this.parametersNames = parametersNames;
        this.parametersTypes = parametersTypes;
        this.dimensions = parametersNames.size();
    }

    public long getSensorID() {
        return sensorID;
    }

    public String getSensorName() {
        return sensorName;
    }

    public ArrayList<String> getParametersNames() {
        return parametersNames;
    }

    public ArrayList<String> getParametersTypes() {
        return parametersTypes;
    }

    public int getDimension() {
        return dimensions;
    }
    @Override
    public String toString() {
        return "ConfigurationClass{" +
                "sensorName='" + sensorName + '\'' +
                ", parametersNames=" + TextUtils.join(", ", parametersNames) +
                ", parametersTypes=" + TextUtils.join(", ", parametersTypes) +
                '}';
    }
}
