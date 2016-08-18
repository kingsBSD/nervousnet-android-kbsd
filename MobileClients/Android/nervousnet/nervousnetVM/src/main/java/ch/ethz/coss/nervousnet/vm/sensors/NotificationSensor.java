package ch.ethz.coss.nervousnet.vm.sensors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import ch.ethz.coss.nervousnet.lib.NotificationReading;

/**
 * Created by grg on 16/08/16.
 */
public class NotificationSensor extends BaseSensor implements SensorEventListener {

    private Context context;

    public NotificationSensor(Context context, byte sensorState) {
        this.context = context;
        this.sensorState = sensorState;
        //int i = 1/0;
    }

    @Override
    public boolean start() {
        LocalBroadcastManager.getInstance(context).registerReceiver(notificationReceiver, new IntentFilter("nervousnet-notification-sensor-event"));
        return true;
    }

    @Override
    public boolean updateAndRestart(byte state) {
        return true;
    }

    @Override
    public boolean stop(boolean changeStateFlag) {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(notificationReceiver);
        return true;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }


    private BroadcastReceiver notificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long timestamp = intent.getLongExtra("timestamp",0L);
            String appName = intent.getStringExtra("appName");
            //Log.d("notification",appName);
            reading = new NotificationReading(timestamp, appName);
            dataReady(reading);
        }
    };

}
