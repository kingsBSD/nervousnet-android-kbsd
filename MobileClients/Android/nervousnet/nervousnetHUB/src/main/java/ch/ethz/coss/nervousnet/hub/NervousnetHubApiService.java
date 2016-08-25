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
 *******************************************************************************/
package ch.ethz.coss.nervousnet.hub;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.widget.Toast;

import java.util.concurrent.locks.Lock;

import ch.ethz.coss.nervousnet.lib.NervousnetRemote;
import ch.ethz.coss.nervousnet.lib.RemoteCallback;
import ch.ethz.coss.nervousnet.lib.SensorReading;
import ch.ethz.coss.nervousnet.vm.NNLog;
import ch.ethz.coss.nervousnet.vm.NervousnetVMConstants;

public class NervousnetHubApiService extends Service {

    private static final String LOG_TAG = NervousnetHubApiService.class.getSimpleName();

    private static int NOTIFICATION_Text = R.string.local_service_started;

    private final NervousnetRemote.Stub mBinder = new NervousnetRemote.Stub() {

        @Override
        public boolean getNervousnetHubStatus() throws RemoteException {
            return (((Application) getApplication()).getState()) == 0 ? false : true;
        }

        @Override
        public SensorReading getLatestReading(long sensorType) throws RemoteException {
            NNLog.d(LOG_TAG, "Sensor getReading() of Type = " + sensorType + " requested ");
            return ((Application) getApplication()).nn_VM.getLatestReading(sensorType);
        }

        @Override
        public void getReading(long sensorType, RemoteCallback cb) throws RemoteException {
            NNLog.d(LOG_TAG, "Sensor getReading() with callback of Type = " + sensorType + " requested  " + cb);
//			return ((Application) getApplication()).nn_VM.getLatestReading(sensorType);
            ((Application) getApplication()).nn_VM.getReading(sensorType, cb);
        }


        @Override
        public void getReadings(long sensorType, long startTime, long endTime, RemoteCallback cb) {
            NNLog.d(LOG_TAG, "getReadings of Type = " + sensorType + " requested ");
            ((Application) getApplication()).nn_VM.getReadings(sensorType, startTime, endTime, cb);
//			((Application) getApplicationContext()).nn_VM.getSensorReadings(sensorType, startTime, endTime,
//					(ArrayList) list);

        }


    };


    private PowerManager.WakeLock wakeLock;
    //    private HandlerThread hthread;
//    private Handler handler;
    private Lock storeMutex;

    @Override
    public void onCreate() {
        NNLog.d(LOG_TAG, "Starting Sensor Service");
        // Prepare the wakelock
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOG_TAG);
//            hthread = new HandlerThread("HandlerThread");
//            hthread.start();
        // Acquire wakelock, some sensors on some phones need this
        if (!wakeLock.isHeld()) {
            wakeLock.acquire();
        }
        if (((Application) getApplication()).nn_VM.getState() == NervousnetVMConstants.STATE_RUNNING) {

            // Display a notification about us starting. We put an icon in the
            // status bar.
            ((Application) getApplication()).initNotification();

        } else {
            NNLog.d(LOG_TAG, "Stopping Sensor Service as nervousnet is not running");
//            stopSelf();
            ((Application) getApplication()).removeNotification();
        }

    }


    @Override
    public IBinder onBind(Intent intent) {
//        NNLog.d(LOG_TAG, "Inside onBind " + mBinder.getCallingPid());
//        NNLog.d(LOG_TAG, "Inside onBind " + mBinder.getCallingUid());
//        NNLog.d(LOG_TAG, "Inside onBind " + mBinder.getCallingUserHandle());

        if (((Application) getApplication()).nn_VM.getState() == NervousnetVMConstants.STATE_PAUSED) {

            return null;
        }

        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NNLog.d(LOG_TAG, "Service execution started");
        if (((Application) getApplicationContext()).nn_VM.getState() == NervousnetVMConstants.STATE_RUNNING) {
            Toast.makeText(NervousnetHubApiService.this, R.string.toast_service_started, Toast.LENGTH_SHORT).show();
            ((Application) getApplicationContext()).nn_VM.startSensors();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        NNLog.d(LOG_TAG, "SERVICE Destroyed ");

        ((Application) getApplicationContext()).nn_VM.stopSensors();
        ((Application) getApplication()).removeNotification();
        // Release the wakelock.
        if (wakeLock.isHeld()) {
            wakeLock.release();
        }
//        hthread.quit();
    }


    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.ic_logo_white : R.drawable.ic_logo;
    }

}
