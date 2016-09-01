package ch.ethz.coss.nervousnet.vm.sensors;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.os.Handler;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;

import ch.ethz.coss.nervousnet.lib.LibConstants;
import ch.ethz.coss.nervousnet.lib.TrafficReading;
import ch.ethz.coss.nervousnet.vm.NervousnetVMConstants;


/**
 * Created by grg on 23/08/16.
 */
public class TrafficSensor extends BaseSensor {

    private Context context;
    private Handler scanHandle;
    private boolean running;
    private int interval;
    private Runnable trafficWorker;

    private ArrayList<Integer> uids;

    private ConcurrentHashMap<Integer,String> namesByUid;
    private ConcurrentHashMap<Integer,Long> trafficTxByUid;
    private ConcurrentHashMap<Integer,Long> trafficRxByUid;

    public TrafficSensor(Context context, byte sensorState) {
        this.context = context;
        this.sensorState = sensorState;

        interval = NervousnetVMConstants.sensor_freq_constants[LibConstants.SENSOR_TRAFFIC][sensorState];

        running = false;

        scanHandle = new Handler();
        uids = new ArrayList<Integer>();
        namesByUid = new ConcurrentHashMap<Integer, String>();
        trafficTxByUid = new ConcurrentHashMap<Integer, Long>();
        trafficRxByUid = new ConcurrentHashMap<Integer, Long>();

        trafficWorker = new Runnable() {
            @Override
            public void run() {
                if (running) {
                    scan();
                    scanHandle.postDelayed(this, interval);
                }

            }
        };
    }

    @Override
    public boolean start() {
        init();
        running = true;
        scanHandle.post(trafficWorker);
        return true;
    }

    @Override
    public boolean stopAndRestart(byte state) {
        return true;
    }

    @Override
    public boolean stop(boolean changeStateFlag) {
        running = false;
        return true;
    }

    private void init() {
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
    }

    private void scan() {

        long bytes, txBytes, rxBytes, lastTx, lastRx = 0;
        for (Integer uid : uids) {
            txBytes = 0;
            rxBytes = 0;
            lastTx =  trafficTxByUid.get(uid);
            lastRx =  trafficRxByUid.get(uid);
            bytes = TrafficStats.getUidTxBytes(uid);
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
    }

}
