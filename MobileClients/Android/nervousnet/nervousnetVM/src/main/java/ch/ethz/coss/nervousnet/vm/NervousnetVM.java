package ch.ethz.coss.nervousnet.vm;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Handler;
import android.os.RemoteException;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ch.ethz.coss.nervousnet.lib.ErrorReading;
import ch.ethz.coss.nervousnet.lib.LibConstants;
import ch.ethz.coss.nervousnet.lib.RemoteCallback;
import ch.ethz.coss.nervousnet.lib.SensorReading;
import ch.ethz.coss.nervousnet.lib.Utils;
import ch.ethz.coss.nervousnet.vm.events.NNEvent;
import ch.ethz.coss.nervousnet.vm.sensors.AccelerometerSensor;
import ch.ethz.coss.nervousnet.vm.sensors.BaseSensor;
import ch.ethz.coss.nervousnet.vm.sensors.BatterySensor;
import ch.ethz.coss.nervousnet.vm.sensors.GyroSensor;
import ch.ethz.coss.nervousnet.vm.sensors.LightSensor;
import ch.ethz.coss.nervousnet.vm.sensors.LocationSensor;
import ch.ethz.coss.nervousnet.vm.sensors.NoiseSensor;
import ch.ethz.coss.nervousnet.vm.sensors.NotificationSensor;
import ch.ethz.coss.nervousnet.vm.sensors.ProximitySensor;
import ch.ethz.coss.nervousnet.vm.sensors.SocketSensor;
import ch.ethz.coss.nervousnet.vm.sensors.TrafficSensor;
import ch.ethz.coss.nervousnet.vm.storage.Config;
import ch.ethz.coss.nervousnet.vm.storage.SQLHelper;
import ch.ethz.coss.nervousnet.vm.storage.SensorConfig;


public class NervousnetVM {

    private static final String LOG_TAG = NervousnetVM.class.getSimpleName();
    private static final String DB_NAME = "NN-DB";

    private Lock storeMutex;

    private Hashtable<Long, BaseSensor> hSensors = null;
    private Hashtable<Long, SensorConfig> hSensorConfig = null;

    private UUID uuid;
    private Context context;
    private byte state = NervousnetVMConstants.STATE_PAUSED;

    private SQLHelper sqlHelper;
    private SensorManager sensorManager;
    private Handler dataCollectionHandler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            NNLog.d(LOG_TAG, "Collect data now. " + this.hashCode() + ", " + dataCollectionHandler.hashCode());

            dataCollectionHandler.postDelayed(this, 1000);
        }
    };

    public NervousnetVM(Context context) {
        NNLog.d(LOG_TAG, "Inside constructor");
        this.context = context;

        sqlHelper = new SQLHelper(context, DB_NAME);

        Config config = sqlHelper.loadVMConfig();

        if (config != null) {
            state = config.getState();
            uuid = UUID.fromString(config.getUUID());
            NNLog.d(LOG_TAG, "Config - UUID = " + uuid);
            NNLog.d(LOG_TAG, "Config - state = " + state);
        } else {
            NNLog.d(LOG_TAG, "Inside Constructure after loadVMConfig() no config found. Create a new config.");
            newUUID();
        }

        initSensors();

        if (state == NervousnetVMConstants.STATE_RUNNING)
            startSensors();

        EventBus.getDefault().register(this);
    }


    private void initSensors() {
        NNLog.d(LOG_TAG, "Inside initSensors");
        storeMutex = new ReentrantLock();
        // Initialize sensor manager
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        LocationManager locManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        PackageManager manager = context.getPackageManager();

        hSensors = new Hashtable<Long, BaseSensor>();
        hSensorConfig = new Hashtable<Long, SensorConfig>();

        for (SensorConfig element : sqlHelper.getSensorConfigList()) {
            hSensorConfig.put(element.getID(), element);
        }

        int count = 0;
        for (Long key : hSensorConfig.keySet()) {
            SensorConfig sensorConfig = hSensorConfig.get(NervousnetVMConstants.sensor_ids[count++]);
            BaseSensor sensor = null;

            if (sensorConfig.getID() == LibConstants.SENSOR_ACCELEROMETER) {
                sensor = new AccelerometerSensor(sensorManager,
                        manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER) ? sensorConfig.getState()
                                : NervousnetVMConstants.SENSOR_STATE_NOT_AVAILABLE);
            } else if (sensorConfig.getID() == LibConstants.SENSOR_BATTERY) {
                sensor = new BatterySensor(context, sensorConfig.getState());
            } else if (sensorConfig.getID() == LibConstants.SENSOR_GYROSCOPE) {
                sensor = new GyroSensor(sensorManager, manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE)
                        ? sensorConfig.getState() : NervousnetVMConstants.SENSOR_STATE_NOT_AVAILABLE);
            } else if (sensorConfig.getID() == LibConstants.SENSOR_LOCATION) {
                sensor = new LocationSensor(manager.hasSystemFeature(PackageManager.FEATURE_LOCATION)
                        ? sensorConfig.getState() : NervousnetVMConstants.SENSOR_STATE_NOT_AVAILABLE, locManager,
                        context);
            } else if (sensorConfig.getID() == LibConstants.SENSOR_LIGHT) {
                sensor = new LightSensor(sensorManager, manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_LIGHT)
                        ? sensorConfig.getState() : NervousnetVMConstants.SENSOR_STATE_NOT_AVAILABLE);
            } else if (sensorConfig.getID() == LibConstants.SENSOR_NOISE) {
                sensor = new NoiseSensor(manager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)
                        ? sensorConfig.getState() : NervousnetVMConstants.SENSOR_STATE_NOT_AVAILABLE, context);
            } else if (sensorConfig.getID() == LibConstants.SENSOR_PROXIMITY) {
                sensor = new ProximitySensor(sensorManager, manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_PROXIMITY)
                        ? sensorConfig.getState() : NervousnetVMConstants.SENSOR_STATE_NOT_AVAILABLE);
            } else if (sensorConfig.getID() == LibConstants.SENSOR_NOTIFICATION) {
                sensor = new NotificationSensor(context, sensorConfig.getState());
            } else if (sensorConfig.getID() == LibConstants.SENSOR_TRAFFIC) {
                sensor = new TrafficSensor(context, sensorConfig.getState());
            } else if (sensorConfig.getID() == LibConstants.SENSOR_SOCKET) {
                sensor = new SocketSensor(context, sensorConfig.getState());
            }

            if (sensor != null) {
                sensor.addListener(sqlHelper);
                hSensors.put(sensorConfig.getID(), sensor);

            }

        }

    }

    public void startSensors() {
        NNLog.d(LOG_TAG, "Inside startSensors");
        int count = 0;
        for (Long key : hSensors.keySet()) {
            NNLog.d(LOG_TAG, "Inside startSensors Sensor ID = " + key);
            BaseSensor sensor = hSensors.get(NervousnetVMConstants.sensor_ids[count]);
            if (sensor != null) {
                sensor.stopAndRestart(hSensorConfig.get(NervousnetVMConstants.sensor_ids[count++]).getState());
            }
        }
        dataCollectionHandler.postDelayed(runnable, 1000);
    }

    public void stopSensors() {
        NNLog.d(LOG_TAG, "Inside stopSensors");
        int count = 0;
        for (Long key : hSensors.keySet()) {
            NNLog.d(LOG_TAG, "Inside stopSensors Sensor ID = " + key);
            BaseSensor sensor = hSensors.get(NervousnetVMConstants.sensor_ids[count++]);
            if (sensor != null)
                sensor.stop(true);
        }

        dataCollectionHandler.removeCallbacks(runnable);
    }

    public void stopSensor(long sensorID, boolean changeStateFlag) {
        BaseSensor sensor = hSensors.get(sensorID);
        if (sensor != null)
            sensor.stop(true);
    }

    public synchronized UUID getUUID() {
        return uuid;
    }


    public synchronized void newUUID() {
        uuid = UUID.randomUUID();
        sqlHelper.storeVMConfig(state, uuid);
    }


    public synchronized void regenerateUUID() {
        newUUID();
        sqlHelper.resetDatabase();
    }


    public void storeNervousnetState(byte state) {
        this.state = state;
        try {
            sqlHelper.storeVMConfig(state, uuid);
        } catch (Exception e) {
            NNLog.d(LOG_TAG, "Exception while calling storeVMConfig ");
            e.printStackTrace();
        }

    }

    public synchronized void updateSensorConfig(long id, byte state) {
        NNLog.d(LOG_TAG, "UpdateSensorConfig called with state = " + state);
        SensorConfig sensorConfig = hSensorConfig.get(id);

        sensorConfig.setState(state);
        try {
            sqlHelper.updateSensorConfig(sensorConfig);
            hSensorConfig.put(id, sensorConfig);
        } catch (Exception e) {
            NNLog.d(LOG_TAG, "Exception while calling updateSensorConfig ");
            e.printStackTrace();
        }

    }

    public synchronized void updateAllSensorConfig(byte state) {
        NNLog.d(LOG_TAG, "updateAllSensorConfig called with state = " + state);
        int count = 0;
        for (Long key : hSensorConfig.keySet()) {
            SensorConfig sensorConfig = hSensorConfig.get(NervousnetVMConstants.sensor_ids[count++]);
            sensorConfig.setState(state);
            hSensorConfig.put(sensorConfig.getID(), sensorConfig);
        }


        try {
            sqlHelper.updateAllSensorConfig(hSensorConfig.values());

        } catch (Exception e) {
            NNLog.d(LOG_TAG, "Exception while calling updateAllSensorConfig ");
            e.printStackTrace();
        }

    }

    public byte getState() {
        return state;
    }

    public synchronized SensorReading getLatestReading(long sensorID) {
        NNLog.d(LOG_TAG, "getLatestReading of ID = " + sensorID + " requested ");

        if (state == NervousnetVMConstants.STATE_PAUSED) {
            NNLog.d(LOG_TAG, "Error 001 : nervousnet is paused.");

            return Utils.getErrorReading(101);
        }
        return hSensors.get(sensorID).getReading();
    }

    public synchronized void getReading(Long sensorID, RemoteCallback cb) {
        NNLog.d(LOG_TAG, "getReading with callback " + cb);

        if (state == NervousnetVMConstants.STATE_PAUSED) {
            NNLog.d(LOG_TAG, "Error 001 : nervousnet is paused.");
            try {
                cb.failure(Utils.getErrorReading(101));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            NNLog.d(LOG_TAG, "getReading callback with success");
            ArrayList aList = new ArrayList();
            NNLog.d(LOG_TAG, "getReading with callback called and state is not paused2");
            aList.add(hSensors.get(sensorID).getReading());
            NNLog.d(LOG_TAG, "getReading with callback called and state is not paused3 " + aList.size());
            try {
                cb.success(aList);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (Exception e) {
                try {
                    cb.failure(Utils.getErrorReading(301));
                } catch (RemoteException re) {
                    re.printStackTrace();
                }
            }
        }


    }

    public synchronized void getReadings(long sensorID, long startTime, long endTime, RemoteCallback cb) {
        if (state == NervousnetVMConstants.STATE_PAUSED) {
            NNLog.d(LOG_TAG, "Error 001 : nervousnet is paused.");
            try {
                cb.failure(Utils.getErrorReading(101));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {

            sqlHelper.getSensorReadings((int) sensorID, startTime, endTime, cb);
        }
    }

    public byte getSensorState(long id) {
        return hSensorConfig.get(id).getState();
    }


    @Subscribe
    public void onNNEvent(NNEvent event) {
        NNLog.d(LOG_TAG, "onSensorStateEvent called ");

        if (event.eventType == NervousnetVMConstants.EVENT_CHANGE_SENSOR_STATE_REQUEST) {
            updateSensorConfig(event.sensorID, event.state);
            BaseSensor sensor = hSensors.get(event.sensorID);
            sensor.stopAndRestart(state);
            EventBus.getDefault().post(new NNEvent(NervousnetVMConstants.EVENT_SENSOR_STATE_UPDATED));


        } else if (event.eventType == NervousnetVMConstants.EVENT_CHANGE_ALL_SENSORS_STATE_REQUEST) {
            updateAllSensorConfig(event.state);
            stopSensors();
            startSensors();
            EventBus.getDefault().post(new NNEvent(NervousnetVMConstants.EVENT_SENSOR_STATE_UPDATED));
        } else if (event.eventType == NervousnetVMConstants.EVENT_PAUSE_NERVOUSNET_REQUEST) {
            storeNervousnetState(NervousnetVMConstants.STATE_PAUSED);
            EventBus.getDefault().post(new NNEvent(NervousnetVMConstants.EVENT_NERVOUSNET_STATE_UPDATED));
        } else if (event.eventType == NervousnetVMConstants.EVENT_START_NERVOUSNET_REQUEST) {
            storeNervousnetState(NervousnetVMConstants.STATE_RUNNING);
            EventBus.getDefault().post(new NNEvent(NervousnetVMConstants.EVENT_NERVOUSNET_STATE_UPDATED));
        }

    }
}
