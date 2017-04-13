package ch.ethz.coss.nervousnet.vm.sensors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

import java.util.ArrayList;

import ch.ethz.coss.nervousnet.lib.SensorReading;
import ch.ethz.coss.nervousnet.vm.configuration.BasicSensorConfiguration;

public class BatterySensor extends BaseSensor {
    private static final String LOG_TAG = BatterySensor.class.getSimpleName();

    BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        int scale = -1;
        int level = -1;
        int voltage = -1;
        int temp = -1;

        @Override
        public void onReceive(Context context, Intent batteryStatus) {
            SensorReading reading = extractBatteryData(batteryStatus);
            push(reading);
            Log.d(LOG_TAG, "Received broadcast - " + reading);
            //Log.d(LOG_TAG, "level is " + level + "/" + scale + ", temp is " + temp + ", voltage is " + voltage);
        }

    };
    private Context context;

    public BatterySensor(Context context, BasicSensorConfiguration conf) {
        super(context, conf);
        this.context = context;
    }


    public void readBattery() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        SensorReading reading = extractBatteryData(batteryStatus);
        if (reading != null)
            push(reading);

    }

    private SensorReading extractBatteryData(Intent batteryStatus) {
        int temp = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
        int volt = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
        byte health = (byte) batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING
                || status == BatteryManager.BATTERY_STATUS_FULL;
        boolean usbCharge = (chargePlug == BatteryManager.BATTERY_PLUGGED_USB);
        boolean acCharge = (chargePlug == BatteryManager.BATTERY_PLUGGED_AC);
        String technology = batteryStatus.getExtras().getString(BatteryManager.EXTRA_TECHNOLOGY);
        float percent = level / (float) scale;

        SensorReading reading = new SensorReading(sensorID, sensorName, paramNames);
        reading.setTimestampEpoch(System.currentTimeMillis());
        ArrayList values = new ArrayList();
        values.add(temp);
        values.add(volt);
        values.add((int)health);
        values.add(level);
        values.add(scale);
        values.add(status);
        values.add(chargePlug);
        values.add(isCharging ? 1 : 0);
        values.add(usbCharge ? 1 : 0);
        values.add(acCharge ? 1 : 0);
        values.add(technology);
        values.add(percent);
        reading.setValues(values);

        return reading;
    }


    @Override
    public boolean startListener() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(batteryReceiver, ifilter);
        SensorReading reading = extractBatteryData(batteryStatus);
        push(reading);
        return true;
    }

    @Override
    public boolean stopListener() {
        try {
            context.unregisterReceiver(batteryReceiver);
            return true;
        } catch (IllegalArgumentException e) {
            //NNLog.d(LOG_TAG, "Exception trying to close battery sensor");
            //e.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
        return false;
    }

}