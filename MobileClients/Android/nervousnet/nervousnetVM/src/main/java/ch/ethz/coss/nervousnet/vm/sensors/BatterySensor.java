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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import ch.ethz.coss.nervousnet.lib.BatteryReading;
import ch.ethz.coss.nervousnet.vm.NNLog;
import ch.ethz.coss.nervousnet.vm.NervousnetVMConstants;

public class BatterySensor extends BaseSensor {
    private static final String LOG_TAG = BatterySensor.class.getSimpleName();
    BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        int scale = -1;
        int level = -1;
        int voltage = -1;
        int temp = -1;

        @Override
        public void onReceive(Context context, Intent batteryStatus) {
            reading = extractBatteryData(batteryStatus);
            dataReady(reading);
            NNLog.d(LOG_TAG, "Received broadcast - " + (((BatteryReading) reading).getPercent()));
            NNLog.d(LOG_TAG, "level is " + level + "/" + scale + ", temp is " + temp + ", voltage is " + voltage);
        }

    };
    private Context context;

    public BatterySensor(Context context, byte sensorState) {
        this.context = context;
        this.sensorState = sensorState;
    }

    public void readBattery() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        reading = extractBatteryData(batteryStatus);
        if (reading != null)
            dataReady(reading);

    }

    private BatteryReading extractBatteryData(Intent batteryStatus) {
        int temp = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
        int volt = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
        byte health = (byte) batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING
                || status == BatteryManager.BATTERY_STATUS_FULL;
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = (chargePlug == BatteryManager.BATTERY_PLUGGED_USB);
        boolean acCharge = (chargePlug == BatteryManager.BATTERY_PLUGGED_AC);
        float batteryPct = level / (float) scale;
        String technology = batteryStatus.getExtras().getString(BatteryManager.EXTRA_TECHNOLOGY);

        reading = new BatteryReading(System.currentTimeMillis(), batteryPct, isCharging, usbCharge, acCharge, temp,
                volt, health, technology);
        return (BatteryReading) reading;
    }

    @Override
    public boolean start() {

        if (sensorState == NervousnetVMConstants.SENSOR_STATE_NOT_AVAILABLE) {
            NNLog.d(LOG_TAG, "Cancelled Starting Battery sensor as Sensor is not available.");
            return false;
        } else if (sensorState == NervousnetVMConstants.SENSOR_STATE_AVAILABLE_PERMISSION_DENIED) {
            NNLog.d(LOG_TAG, "Cancelled Starting Battery sensor as permission denied by user.");
            return false;
        } else if (sensorState == NervousnetVMConstants.SENSOR_STATE_AVAILABLE_BUT_OFF) {
            NNLog.d(LOG_TAG, "Cancelled starting Battery sensor as Sensor state is switched off.");
            return false;
        }

        NNLog.d(LOG_TAG, "Starting Battery sensor with state = " + sensorState);
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(batteryReceiver, ifilter);
        reading = extractBatteryData(batteryStatus);
        dataReady(reading);
        NNLog.d(LOG_TAG, "Stopped Battery sensor ");

        return true;
    }

    @Override
    public boolean stopAndRestart(byte state) {
        if (state == NervousnetVMConstants.SENSOR_STATE_NOT_AVAILABLE) {
            NNLog.d(LOG_TAG, "Cancelled Starting battery sensor as Sensor is not available.");
            return false;
        } else if (state == NervousnetVMConstants.SENSOR_STATE_AVAILABLE_PERMISSION_DENIED) {
            NNLog.d(LOG_TAG, "Cancelled Starting battery sensor as permission denied by user.");
            return false;
        } else if (state == NervousnetVMConstants.SENSOR_STATE_AVAILABLE_BUT_OFF) {
            setSensorState(state);
            NNLog.d(LOG_TAG, "Cancelled starting battery sensor as Sensor state is switched off.");
            return false;
        }

        stop(false);
        setSensorState(state);
        NNLog.d(LOG_TAG, "Restarting battery sensor with state = " + sensorState);
        start();
        return true;
    }

    @Override
    public boolean stop(boolean changeStateFlag) {

        if (sensorState == NervousnetVMConstants.SENSOR_STATE_NOT_AVAILABLE) {
            NNLog.d(LOG_TAG, "Cancelled stop battery sensor as Sensor state is not available ");
            return false;
        } else if (sensorState == NervousnetVMConstants.SENSOR_STATE_AVAILABLE_PERMISSION_DENIED) {
            NNLog.d(LOG_TAG, "Cancelled stop battery sensor as permission denied by user.");
            return false;
        } else if (sensorState == NervousnetVMConstants.SENSOR_STATE_AVAILABLE_BUT_OFF) {
            NNLog.d(LOG_TAG, "Cancelled stop battery sensor as Sensor state is switched off ");
            return false;
        }

        try {
            context.unregisterReceiver(batteryReceiver);
        } catch (IllegalArgumentException e) {
            NNLog.d(LOG_TAG, "Exception trying to close battery sensor");
            e.printStackTrace();
        }
        setSensorState(NervousnetVMConstants.SENSOR_STATE_AVAILABLE_BUT_OFF);
        this.reading = null;
        return true;
    }

}