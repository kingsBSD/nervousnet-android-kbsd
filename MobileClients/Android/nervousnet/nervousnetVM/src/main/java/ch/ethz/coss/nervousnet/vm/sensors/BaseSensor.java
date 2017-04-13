package ch.ethz.coss.nervousnet.vm.sensors;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import ch.ethz.coss.nervousnet.lib.SensorReading;
import ch.ethz.coss.nervousnet.vm.configuration.BasicSensorConfiguration;
import ch.ethz.coss.nervousnet.vm.database.NervousnetDBManager;

/**
 * BaseSensor is an abstract class which task is to hide implementational details of specific
 * physical or other sensors and make sensor listening scalable. All sensor listeners should
 * extend this class and implement methods {@link #startListener()} and {@link #stopListener()}
 * and when a new value from a sensor is obtained, SeneorReading should be created and
 * method {@link #push(SensorReading)} should be called which takes care that the SensorReading
 * gets stored into a database or sent to some other parts. Currently, SensorReading is just
 * stored into a database.
 * TODO: Remove context from the constructor and database storage. Storage should be managed
 * by NervousnetVM
 */
public abstract class BaseSensor {

    private static final String LOG_TAG = BaseSensor.class.getSimpleName();

    // Database handler
    private NervousnetDBManager databaseHandler;
    // Sensor configuration
    protected BasicSensorConfiguration configuration;

    // Just a shortcut of configuration above and maybe more intuitive
    // representation
    protected long sensorID;
    protected String sensorName;
    protected ArrayList<String> paramNames;

    // In order to reduce the amount of SensorReadings to be stored into a database,
    // sampling rate is specified in the configuration. This variable is only temporary
    // variable which stores timestamp by which no SensorReading will be stored. Then, the
    // first SensorReading that arrives, is stored and nextSampling is updated to the next
    // timestamp.
    private long nextSampling = 0;


    /**
     *
     * @param context Service context
     * @param conf Sensor configuration
     */
    public BaseSensor(Context context, BasicSensorConfiguration conf){
        this.databaseHandler = NervousnetDBManager.getInstance(context);
        this.configuration = conf;
        this.sensorID = conf.getSensorID();
        this.sensorName = conf.getSensorName();
        this.paramNames = conf.getParametersNames();
        this.databaseHandler.createTableIfNotExists(conf);
    }


    /**
     * Method for accepting all new sensor readings from subclasses. It takes care of passing new
     * readings further for storing.
     */
    public void push(SensorReading reading){
        if (reading.getTimestampEpoch() >= nextSampling && configuration.getActualSamplingRate() >= 0) {
            Log.d(LOG_TAG, reading.toString());
            nextSampling = reading.getTimestampEpoch() + configuration.getActualSamplingRate();
            databaseHandler.store(reading);
        }
    }

    /**
     * Start sensor reading.
     */
    public void start(){
        stopListener();
        this.databaseHandler.createTableIfNotExists(configuration);
        startListener();
    }

    /**
     * Stop sensor reading
     */
    public void stop(){
        stopListener();
    }

    /**
     * Abstract method to register a sensor.
     */
    protected abstract boolean startListener();

    /**
     * Abstract method to unregister a sensor.
     */
    protected abstract boolean stopListener();

}
