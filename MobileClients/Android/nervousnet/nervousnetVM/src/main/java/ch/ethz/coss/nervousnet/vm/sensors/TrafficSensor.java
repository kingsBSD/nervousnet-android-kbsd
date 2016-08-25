package ch.ethz.coss.nervousnet.vm.sensors;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.os.AsyncTask;
import android.os.Handler;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;

import ch.ethz.coss.nervousnet.lib.TrafficReading;


/**
 * Created by grg on 23/08/16.
 */
public class TrafficSensor extends BaseSensor {

    private Context context;
    private Handler scanHandle;
    private TrafficTask trafficWorker;

    ArrayList<Integer> uids;
    ConcurrentHashMap<Integer,String> namesByUid;
    ConcurrentHashMap<Integer,Long> trafficTxByUid;
    ConcurrentHashMap<Integer,Long> trafficRxByUid;

    public TrafficSensor(Context context, byte sensorState) {
        this.context = context;
        this.sensorState = sensorState;

        scanHandle = new Handler();
        uids = new ArrayList<Integer>();
        namesByUid = new ConcurrentHashMap<Integer, String>();
        trafficTxByUid = new ConcurrentHashMap<Integer, Long>();
        trafficRxByUid = new ConcurrentHashMap<Integer, Long>();

        PackageManager pm = this.context.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        String[] permissions;
        long txBytes, rxBytes;
        for (ApplicationInfo appInfo : packages) {
            try {
                permissions = pm.getPackageInfo(appInfo.packageName, PackageManager.GET_PERMISSIONS).requestedPermissions;
                if (permissions != null) {
                    for (String permission : permissions) {
                        if (permission.equals("android.permission.INTERNET")) {
                            // ...then get the initial traffic stats.
                            txBytes = TrafficStats.getUidTxBytes(appInfo.uid);
                            rxBytes = TrafficStats.getUidRxBytes(appInfo.uid);
                            if (txBytes != -1 && rxBytes != -1) {
                                uids.add(appInfo.uid);
                                namesByUid.put(appInfo.uid, appInfo.processName);
                                trafficTxByUid.put(appInfo.uid, txBytes);
                                trafficRxByUid.put(appInfo.uid, rxBytes);
                            }
                            break;
                        }
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
            }
        }

        trafficWorker = new TrafficTask();
    }

    @Override
    public boolean start() {
        trafficWorker.execute();
        return true;
    }

    @Override
    public boolean stopAndRestart(byte state) {
        return true;
    }

    @Override
    public boolean stop(boolean changeStateFlag) {
        trafficWorker.cancel(true);
        return true;
    }

    public class TrafficTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            //Log.d("traffic", "scanning...");
            long bytes, txBytes, rxBytes, lastTx, lastRx = 0;
            for (Integer uid : uids) {
                txBytes = 0;
                rxBytes = 0;
                bytes = TrafficStats.getUidTxBytes(uid);
                lastTx =  trafficTxByUid.get(uid);
                lastRx =  trafficRxByUid.get(uid);
                if (bytes > trafficTxByUid.get(uid)) {
                    txBytes = bytes - lastTx;
                    trafficTxByUid.put(uid, bytes);
                }
                bytes = TrafficStats.getUidRxBytes(uid) - trafficTxByUid.get(uid);
                if (bytes > trafficRxByUid.get(uid)) {
                    rxBytes = bytes - lastRx;
                    trafficRxByUid.put(uid, bytes);
                }
                if (txBytes > 0 || rxBytes > 0) {
                    long timestamp = System.currentTimeMillis();
                    String appName= namesByUid.get(uid);
                    reading = new TrafficReading(timestamp, appName, txBytes, rxBytes);
                    dataReady(reading);
                }
            }
            return null;
        }
    }

}
