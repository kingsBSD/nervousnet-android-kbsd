package ch.ethz.coss.nervousnet.vm.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ch.ethz.coss.nervousnet.lib.SensorReading;
import ch.ethz.coss.nervousnet.vm.configuration.BasicSensorConfiguration;

/**
 * AndroidSensor is a listener for any sensor that is documented in {@link android.hardware.Sensor}.
 * This enables that for any new sensor in the documentation only configuration needs to be updated.
 */

public class AndroidSensor extends BaseSensor  implements SensorEventListener {

    private static final String LOG_TAG = AndroidSensor.class.getSimpleName();
    private SensorManager mSensorManager;
    private Sensor sensor;

    private int[] androidParametersPositions;

    // Locking
    protected Lock listenerMutex = new ReentrantLock();

    /**
     * Constructor for basic sensor specified in the documentation for Android Sensor class.
     */
    public AndroidSensor(Context context, BasicSensorConfiguration conf) {
        // Abstract class will take care of acquiring parameter names and
        // everything that is needed to initialize this listener
        super(context, conf);
        this.mSensorManager = (SensorManager)context.getSystemService(context.SENSOR_SERVICE);
        this.sensor = mSensorManager.getDefaultSensor(conf.getAndroidSensorType());
        this.androidParametersPositions = conf.getAndroidParametersPositions();
    }

    @Override
    protected boolean startListener() {
        Log.d(LOG_TAG, "Register sensor " + sensorName + " ...");
        listenerMutex.lock();
        mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
        listenerMutex.unlock();
        return true;
    }

    @Override
    protected boolean stopListener() {
        Log.d(LOG_TAG, "Unregister sensor " + sensorName);
        listenerMutex.lock();
        mSensorManager.unregisterListener(this, this.sensor);
        listenerMutex.unlock();
        return true;
    }


    /**
     * Method is called by SensorEventListener when an event occurs. Check
     * SensorEvenListener for more details.
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // Get timestamp for the sensor
        long timestamp = System.currentTimeMillis();
        // Get values
        ArrayList values = new ArrayList();
        for (int i = 0; i < androidParametersPositions.length; i++) {
            Object val = sensorEvent.values[androidParametersPositions[i]];
            values.add(val);
        }
        // Fill sensor reading
        SensorReading reading = new SensorReading(sensorID, sensorName, paramNames);
        reading.setTimestampEpoch(timestamp);
        reading.setValues(values);
        // Push reading
        push(reading);
        //Log.d("SENSOR", ""+reading.getSensorName() + " " + TextUtils.join(", ", values));
    }


    /**
     * Method is called by SensorEventListener. Check SensorEvenListener
     * for more details.
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
