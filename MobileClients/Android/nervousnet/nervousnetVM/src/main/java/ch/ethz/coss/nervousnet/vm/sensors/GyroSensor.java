/*******************************************************************************
 * *     Nervousnet - a distributed middleware software for social sensing.
 * *      It is responsible for collecting and managing data in a fully de-centralised fashion
 * *
 * *     Copyright (C) 2016 ETH Zürich, COSS
 * *
 * *     This file is part of Nervousnet Framework
 * *
 * *     Nervousnet is free software: you can redistribute it and/or modify
 * *     it under the terms of the GNU General Public License as published by
 * *     the Free Software Foundation, either version 3 of the License, or
 * *     (at your option) any later version.
 * *
 * *     Nervousnet is distributed in the hope that it will be useful,
 * *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 * *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * *     GNU General Public License for more details.
 * *
 * *     You should have received a copy of the GNU General Public License
 * *     along with NervousNet. If not, see <http://www.gnu.org/licenses/>.
 * *
 * *
 * * 	Contributors:
 * * 	Prasad Pulikal - prasad.pulikal@gess.ethz.ch  -  Initial API and implementation
 *******************************************************************************/
package ch.ethz.coss.nervousnet.vm.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import ch.ethz.coss.nervousnet.lib.GyroReading;
import ch.ethz.coss.nervousnet.vm.NNLog;
import ch.ethz.coss.nervousnet.vm.NervousnetVMConstants;

public class GyroSensor extends BaseSensor implements SensorEventListener {

    private static final String LOG_TAG = GyroSensor.class.getSimpleName();
    private SensorManager sensorManager;

    public GyroSensor(SensorManager sensorManager, byte sensorState) {
        this.sensorState = sensorState;
        this.sensorManager = sensorManager;
    }

    @Override
    public boolean start() {

        if (sensorState == NervousnetVMConstants.SENSOR_STATE_NOT_AVAILABLE) {
            NNLog.d(LOG_TAG, "Cancelled Starting Gyroscope sensor as Sensor is not available.");
            return false;
        } else if (sensorState == NervousnetVMConstants.SENSOR_STATE_AVAILABLE_PERMISSION_DENIED) {
            NNLog.d(LOG_TAG, "Cancelled Starting Gyroscope sensor as permission denied by user.");
            return false;
        } else if (sensorState == NervousnetVMConstants.SENSOR_STATE_AVAILABLE_BUT_OFF) {
            NNLog.d(LOG_TAG, "Cancelled starting Gyroscope sensor as Sensor state is switched off.");
            return false;
        }

        NNLog.d(LOG_TAG, "Starting Gyroscope sensor with state = " + sensorState);

        boolean flag = sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_FASTEST);
        // NervousnetVMConstants.sensor_freq_constants[4][sensorState -1]);

        NNLog.d(LOG_TAG, "Started Gyroscope sensor with successflag = " + flag);
        return true;
    }

    @Override
    public boolean stopAndRestart(byte state) {

        if (state == NervousnetVMConstants.SENSOR_STATE_NOT_AVAILABLE) {
            NNLog.d(LOG_TAG, "Cancelled Starting Gyroscope sensor as Sensor is not available.");
            return false;
        } else if (state == NervousnetVMConstants.SENSOR_STATE_AVAILABLE_PERMISSION_DENIED) {
            NNLog.d(LOG_TAG, "Cancelled Starting Gyroscope sensor as permission denied by user.");
            return false;
        } else if (state == NervousnetVMConstants.SENSOR_STATE_AVAILABLE_BUT_OFF) {
            setSensorState(state);
            NNLog.d(LOG_TAG, "Cancelled starting Gyroscope sensor as Sensor state is switched off.");
            return false;
        }

        stop(false);

        setSensorState(state);
        NNLog.d(LOG_TAG, "Restarting Gyroscope sensor with state = " + sensorState);

        start();
        return true;
    }

    @Override
    public boolean stop(boolean changeStateFlag) {
        if (sensorState == NervousnetVMConstants.SENSOR_STATE_NOT_AVAILABLE) {
            NNLog.d(LOG_TAG, "Cancelled stop Gyroscope sensor as Sensor state is not available ");
            return false;
        } else if (sensorState == NervousnetVMConstants.SENSOR_STATE_AVAILABLE_PERMISSION_DENIED) {
            NNLog.d(LOG_TAG, "Cancelled stop Gyroscope sensor as permission denied by user.");
            return false;
        } else if (sensorState == NervousnetVMConstants.SENSOR_STATE_AVAILABLE_BUT_OFF) {
            NNLog.d(LOG_TAG, "Cancelled stop Gyroscope sensor as Sensor state is switched off ");
            return false;
        }
        sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE));
        setSensorState(NervousnetVMConstants.SENSOR_STATE_AVAILABLE_BUT_OFF);

        NNLog.d(LOG_TAG, "Stopped Gyroscope sensor with state = " + sensorState);

        this.reading = null;
        return true;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        NNLog.d(LOG_TAG, "X = " + event.values[0]);
        reading = new GyroReading(System.currentTimeMillis()
                + (event.timestamp - System.nanoTime()) / 1000000L, event.values);
        if (reading != null)
            dataReady(reading);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}