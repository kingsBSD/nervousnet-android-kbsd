package ch.ethz.coss.nervousnet.vm.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import ch.ethz.coss.nervousnet.lib.AccelerometerReading;
import ch.ethz.coss.nervousnet.lib.BatteryReading;
import ch.ethz.coss.nervousnet.lib.ErrorReading;
import ch.ethz.coss.nervousnet.lib.GyroReading;
import ch.ethz.coss.nervousnet.lib.LibConstants;
import ch.ethz.coss.nervousnet.lib.LightReading;
import ch.ethz.coss.nervousnet.lib.LocationReading;
import ch.ethz.coss.nervousnet.lib.NoiseReading;
import ch.ethz.coss.nervousnet.lib.NotificationReading;
import ch.ethz.coss.nervousnet.lib.ProximityReading;
import ch.ethz.coss.nervousnet.lib.SensorReading;
import ch.ethz.coss.nervousnet.lib.RemoteCallback;
import ch.ethz.coss.nervousnet.lib.TrafficReading;
import ch.ethz.coss.nervousnet.vm.NNLog;
import ch.ethz.coss.nervousnet.vm.NervousnetVMConstants;
import ch.ethz.coss.nervousnet.vm.sensors.BaseSensor.BaseSensorListener;
import ch.ethz.coss.nervousnet.vm.storage.DaoMaster.DevOpenHelper;
import de.greenrobot.dao.query.QueryBuilder;

public class SQLHelper implements BaseSensorListener {

    private static final String LOG_TAG = SQLHelper.class.getSimpleName();
    Config config = null;
    DaoMaster daoMaster;
    DaoSession daoSession;
    SQLiteDatabase sqlDB;
    ConfigDao configDao;
    SensorConfigDao sensorConfigDao;
    AccelDataDao accDao;
    BatteryDataDao battDao;
    LightDataDao lightDao;
    NoiseDataDao noiseDao;
    NotificationDataDao notificationDao;
    LocationDataDao locDao;
    GyroDataDao gyroDao;
    ProximityDataDao proximityDao;
    TrafficDataDao trafficDao;

    ArrayList<SensorDataImpl> accelDataArrList, battDataArrList, gyroDataArrList, lightDataArrList, locDataArrList, noiseDataArrList,
            notificationDataArrList, proxDataArrList, trafficDataArrList;

    public SQLHelper() {

    }

    public SQLHelper(Context context, String DB_NAME) {
        initDao(context, DB_NAME);
    }

    private void initDao(Context context, String DB_NAME) {
        NNLog.d(LOG_TAG, "Inside initDao");
        try {
            DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
            sqlDB = helper.getWritableDatabase();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Inside constructor and creating DB = " + DB_NAME, e);
        }

        daoMaster = new DaoMaster(sqlDB);
        daoSession = daoMaster.newSession();
        configDao = daoSession.getConfigDao();
        sensorConfigDao = daoSession.getSensorConfigDao();
        accDao = daoSession.getAccelDataDao();
        battDao = daoSession.getBatteryDataDao();
        locDao = daoSession.getLocationDataDao();
        gyroDao = daoSession.getGyroDataDao();
        lightDao = daoSession.getLightDataDao();
        noiseDao = daoSession.getNoiseDataDao();
        notificationDao = daoSession.getNotificationDataDao();
        proximityDao = daoSession.getProximityDataDao();
        trafficDao = daoSession.getTrafficDataDao();

        populateSensorConfig();

        accelDataArrList = new ArrayList<SensorDataImpl>();
        battDataArrList = new ArrayList<SensorDataImpl>();
        gyroDataArrList = new ArrayList<SensorDataImpl>();
        lightDataArrList = new ArrayList<SensorDataImpl>();
        locDataArrList = new ArrayList<SensorDataImpl>();
        noiseDataArrList = new ArrayList<SensorDataImpl>();
        notificationDataArrList = new ArrayList<SensorDataImpl>();
        proxDataArrList = new ArrayList<SensorDataImpl>();
        trafficDataArrList = new ArrayList<SensorDataImpl>();

    }

    public synchronized void populateSensorConfig() {
        NNLog.d(LOG_TAG, "Inside populateSensorConfig");
        boolean success = true;
        SensorConfig sensorconfig = null;

        NNLog.d(LOG_TAG, "sensorConfigDao - count = " + sensorConfigDao.queryBuilder().count());
        if (sensorConfigDao.queryBuilder().count() == 0) {

            for (int i = 0; i < NervousnetVMConstants.sensor_ids.length; i++)
                sensorConfigDao.insert(new SensorConfig(NervousnetVMConstants.sensor_ids[i],
                        NervousnetVMConstants.sensor_labels[i], (byte) 0));
        }
    }

    public void resetDatabase() {
        accDao.deleteAll();
        battDao.deleteAll();
        locDao.deleteAll();
        gyroDao.deleteAll();
        lightDao.deleteAll();
        noiseDao.deleteAll();
        notificationDao.deleteAll();
        proximityDao.deleteAll();
        trafficDao.deleteAll();

        accelDataArrList.clear();
        battDataArrList.clear();
        gyroDataArrList.clear();
        lightDataArrList.clear();
        locDataArrList.clear();
        noiseDataArrList.clear();
        notificationDataArrList.clear();
        proxDataArrList.clear();
        trafficDataArrList.clear();
    }
    public synchronized Config loadVMConfig() {
        NNLog.d(LOG_TAG, "Inside loadVMConfig");

        NNLog.d(LOG_TAG, "Config - count = " + configDao.queryBuilder().count());
        if (configDao.queryBuilder().count() != 0) {
            config = configDao.queryBuilder().unique();
        }

        return config;
    }

    public synchronized void storeVMConfig(byte state, UUID uuid) {
        NNLog.d(LOG_TAG, "Inside storeVMConfig");
        Config config = null;

        if (configDao.queryBuilder().count() == 0) {
            NNLog.d(LOG_TAG, "Config DB Is empty.");
            config = new Config(state, uuid.toString(), Build.MANUFACTURER, Build.MODEL, "Android",
                    Build.VERSION.RELEASE, System.currentTimeMillis());
            configDao.insert(config);
        } else if (configDao.queryBuilder().count() == 1) {
            NNLog.d(LOG_TAG, "Config DB exists.");
            config = configDao.queryBuilder().unique();
            configDao.deleteAll();
            config.setState(state);
            config.setUUID(uuid.toString());
            configDao.insert(config);
            config = configDao.queryBuilder().unique();
            NNLog.d(LOG_TAG, "state = " + config.getState());
        } else
            Log.e(LOG_TAG, "Config DB count is more than 1. There is something wrong.");

    }

    public synchronized void updateSensorConfig(SensorConfig config) throws Exception {

        sensorConfigDao.insertOrReplace(config);
    }

    public synchronized void updateAllSensorConfig(SensorConfig... entities) throws Exception {

        sensorConfigDao.insertOrReplaceInTx(entities);
    }

    public synchronized void updateAllSensorConfig(Iterable entities) throws Exception {

        sensorConfigDao.insertOrReplaceInTx(entities);
    }


    public synchronized List<SensorConfig> getSensorConfigList() {
        return sensorConfigDao.queryBuilder().list();
    }

    public void storeSensorAsync(int type, ArrayList<SensorDataImpl> sensorDataList) {

        new StoreTask(type).execute(sensorDataList);
    }

    public synchronized boolean storeSensor(int type, ArrayList sensorDataList) {
        NNLog.d(LOG_TAG, "Inside storeSensor ");

        if (sensorDataList == null) {
            Log.e(LOG_TAG, "sensorDataList is null. please check it");
            return false;
        }
        NNLog.d(LOG_TAG, "sensorDataList (Type = " + type + ")"); // ,
        // Timestamp
        // =
        // "+sensorData.getTimeStamp()+",
        // Volatility
        // =
        // "+sensorData.getVolatility());

        switch (type) {
            case LibConstants.SENSOR_ACCELEROMETER:
                NNLog.d(LOG_TAG, "ACCEL_DATA table count = " + accDao.count());
                accDao.insertInTx(sensorDataList);

                return true;

            case LibConstants.SENSOR_BATTERY:
                NNLog.d(LOG_TAG, "BATT_DATA table count = " + battDao.count());

                battDao.insertInTx(sensorDataList);
                return true;

            case LibConstants.DEVICE_INFO:
                return true;

            case LibConstants.SENSOR_LOCATION:
                NNLog.d(LOG_TAG, "LOCATION_DATA table count = " + locDao.count());
                locDao.insertInTx(sensorDataList);
                return true;

            case LibConstants.SENSOR_GYROSCOPE:
                NNLog.d(LOG_TAG, "GYRO_DATA table count = " + gyroDao.count());
                gyroDao.insertInTx(sensorDataList);
                return true;
            case LibConstants.SENSOR_LIGHT:
                NNLog.d(LOG_TAG, "LIGHT_DATA table count = " + lightDao.count());
                lightDao.insertInTx(sensorDataList);
                return true;

            case LibConstants.SENSOR_NOISE:
                NNLog.d(LOG_TAG, "NoiseData table count = " + noiseDao.count());
                noiseDao.insertInTx(sensorDataList);
                return true;

            case LibConstants.SENSOR_NOTIFICATION:
                NNLog.d(LOG_TAG, "NotificatonData table count = " + notificationDao.count());
                notificationDao.insertInTx(sensorDataList);
                return true;

            case LibConstants.SENSOR_PROXIMITY:
                NNLog.d(LOG_TAG, "ProximityData table count = " + proximityDao.count());
                proximityDao.insertInTx(sensorDataList);
                return true;

            case LibConstants.SENSOR_TRAFFIC:
                NNLog.d(LOG_TAG, "TrafficData table count = " + trafficDao.count());
                trafficDao.insertInTx(sensorDataList);
                return true;

        }
        return false;
    }

    public synchronized void getSensorReadings(int type, long startTime, long endTime, RemoteCallback cb) {

        NNLog.d(LOG_TAG, "getSensorReadings with callback");
        QueryBuilder<?> qb = null;
        ArrayList<SensorReading> list = new ArrayList<SensorReading>();
        ArrayList<SensorDataImpl> aList;
        Iterator<SensorDataImpl> iterator;
        switch (type) {
            case LibConstants.SENSOR_ACCELEROMETER:
                qb = accDao.queryBuilder();
                qb.where(AccelDataDao.Properties.TimeStamp.between(startTime, endTime));

                NNLog.d(LOG_TAG, "SENSOR_ACCELEROMETER List size = " + list.size());
                break;
            case LibConstants.SENSOR_BATTERY:
                qb = battDao.queryBuilder();
                qb.where(BatteryDataDao.Properties.TimeStamp.between(startTime, endTime));

                NNLog.d(LOG_TAG, "SENSOR_BATTERY List size = " + list.size());
                break;
//		case LibConstants.DEVICE_INFO:
//
//			// TODO
//			return;
            case LibConstants.SENSOR_LOCATION:
                qb = locDao.queryBuilder();
                qb.where(LocationDataDao.Properties.TimeStamp.between(startTime, endTime));

                NNLog.d(LOG_TAG, "SENSOR_LOCATION List size = " + list.size());

                break;

            case LibConstants.SENSOR_GYROSCOPE:
                qb = gyroDao.queryBuilder();
                qb.where(GyroDataDao.Properties.TimeStamp.between(startTime, endTime));


                NNLog.d(LOG_TAG, "SENSOR_GYROSCOPE");

                break;

            case LibConstants.SENSOR_LIGHT:
                NNLog.d(LOG_TAG, "SENSOR_LIGHT");
                qb = lightDao.queryBuilder();
                qb.where(LightDataDao.Properties.TimeStamp.between(startTime, endTime));


                break;

            case LibConstants.SENSOR_NOISE:
                qb = noiseDao.queryBuilder();
                qb.where(NoiseDataDao.Properties.TimeStamp.between(startTime, endTime));
                break;

            case LibConstants.SENSOR_NOTIFICATION:
                qb = notificationDao.queryBuilder();
                qb.where(NotificationDataDao.Properties.TimeStamp.between(startTime, endTime));
                break;

            case LibConstants.SENSOR_PROXIMITY:
                qb = proximityDao.queryBuilder();
                qb.where(ProximityDataDao.Properties.TimeStamp.between(startTime, endTime));
                break;

            case LibConstants.SENSOR_TRAFFIC:
                qb = trafficDao.queryBuilder();
                qb.where(TrafficDataDao.Properties.TimeStamp.between(startTime, endTime));
                break;

            default:
                break;
        }

        aList = (ArrayList<SensorDataImpl>) qb.list();
        NNLog.d(LOG_TAG, " List size = " + aList.size());
        iterator = aList.iterator();
        while (iterator.hasNext()) {

            NNLog.d(LOG_TAG, " 2. List size = " + list.size());
            SensorDataImpl data = iterator.next();
            data.setType(type);
            list.add(convertSensorDataToSensorReading(data));

        }
        try {
            if(cb == null || list == null)
                NNLog.d(LOG_TAG, "getSensorReadings with callback, Callback instance or list is  null");
            NNLog.d(LOG_TAG, "getSensorReadings with callback - SUCCESS");

            cb.success(list);
        } catch (RemoteException e) {
            NNLog.d(LOG_TAG, "getSensorReadings with callback - RemoteException");
//            cb.failure(new ErrorReading(new String[]{"300", "RemoteException while sending success"}));
            e.printStackTrace();
        } catch (Exception e) {
            NNLog.d(LOG_TAG, "getSensorReadings with callback - FAILURE");
//            cb.failure(new ErrorReading(new String[]{"301", "Exception while sending success"}));
        }

    }

    private synchronized SensorReading convertSensorDataToSensorReading(SensorDataImpl data) {
        NNLog.d(LOG_TAG, "convertSensorDataToSensorReading reading Type = " + data.getType());
        SensorReading reading = null;

        switch (data.getType()) {
            case LibConstants.SENSOR_ACCELEROMETER:
                AccelData adata = (AccelData) data;
                reading = new AccelerometerReading(adata.getTimeStamp(),
                        new float[]{adata.getX(), adata.getY(), adata.getZ()});
                reading.type = LibConstants.SENSOR_ACCELEROMETER;

                return reading;

            case LibConstants.SENSOR_BATTERY:

                BatteryData bdata = (BatteryData) data;
                reading = new BatteryReading(bdata.getTimeStamp(),
                        bdata.getPercent(), false, false, false, 0f, 0, (byte) 0,
                        null);
                reading.type = LibConstants.SENSOR_BATTERY;

                return reading;

            case LibConstants.SENSOR_GYROSCOPE:
                GyroData gdata = (GyroData) data;
                reading = new GyroReading(gdata.getTimeStamp(),
                        new float[]{gdata.getGyroX(), gdata.getGyroY(), gdata.getGyroZ()});

                reading.type = LibConstants.SENSOR_GYROSCOPE;
                return reading;

            case LibConstants.SENSOR_LIGHT:
                LightData ldata = (LightData) data;
                reading = new LightReading(ldata.getTimeStamp(), ldata.getLux());
                reading.type = LibConstants.SENSOR_LIGHT;
                return reading;

            case LibConstants.SENSOR_LOCATION:
                LocationData locdata = (LocationData) data;
                reading = new LocationReading(locdata.getTimeStamp(), new double[]{locdata.getLatitude(), locdata.getLongitude()});
                reading.type = LibConstants.SENSOR_LOCATION;
                return reading;

            case LibConstants.SENSOR_NOISE:
                NoiseData ndata = (NoiseData) data;
                reading = new NoiseReading(ndata.getTimeStamp(), ndata.getDecibel());
                reading.type = LibConstants.SENSOR_NOISE;
                return reading;

            case LibConstants.SENSOR_NOTIFICATION:
                NotificationData nodata = (NotificationData) data;
                reading = new NotificationReading(nodata.getTimeStamp(), nodata.getAppName());
                reading.type = LibConstants.SENSOR_NOTIFICATION;
                return reading;

            case LibConstants.SENSOR_PROXIMITY:
                ProximityData pdata = (ProximityData) data;
                reading = new ProximityReading(pdata.getTimeStamp(), pdata.getProximity());
                reading.type = LibConstants.SENSOR_PROXIMITY;
                return reading;

            case LibConstants.SENSOR_TRAFFIC:
                TrafficData trdata = (TrafficData) data;
                reading = new TrafficReading(trdata.getTimeStamp(), trdata.getAppName(), trdata.getTxBytes(), trdata.getRxBytes());
                reading.type = LibConstants.SENSOR_TRAFFIC;
                return reading;


            default:
                return null;

        }
    }

    @Override
    public void sensorDataReady(SensorReading reading) {
        if (reading != null)
            convertSensorReadingToSensorData(reading);
    }

    private synchronized void convertSensorReadingToSensorData(SensorReading reading) {
        NNLog.d(LOG_TAG, "convertSensorReadingToSensorData reading Type = " + reading.type);
        SensorDataImpl sensorData;

        switch (reading.type) {
            case LibConstants.SENSOR_ACCELEROMETER:
                AccelerometerReading areading = (AccelerometerReading) reading;
                sensorData = new AccelData(null, reading.timestamp, areading.getX(), areading.getY(), areading.getZ(), 0l,
                        true);
                sensorData.setType(LibConstants.SENSOR_ACCELEROMETER);
                accelDataArrList.add((SensorDataImpl) sensorData);

                if (accelDataArrList.size() > 100) {
                    storeSensorAsync(LibConstants.SENSOR_ACCELEROMETER, new ArrayList<SensorDataImpl>(accelDataArrList));
                    accelDataArrList.clear();
                }
                break;

            case LibConstants.SENSOR_BATTERY:
                BatteryReading breading = (BatteryReading) reading;
                sensorData = new BatteryData(null, reading.timestamp, breading.getPercent(), breading.getCharging_type(),
                        breading.getHealth(), breading.getTemp(), breading.getVolt(), breading.volatility,
                        breading.isShare);
                sensorData.setType(LibConstants.SENSOR_BATTERY);

                battDataArrList.add((SensorDataImpl) sensorData);

                if (battDataArrList.size() > 10) {
                    storeSensorAsync(LibConstants.SENSOR_BATTERY, new ArrayList<SensorDataImpl>(battDataArrList));
                    battDataArrList.clear();
                }
                break;

            case LibConstants.SENSOR_GYROSCOPE:
                GyroReading greading = (GyroReading) reading;
                sensorData = new GyroData(null, reading.timestamp, greading.getGyroX(), greading.getGyroY(),
                        greading.getGyroZ(), 0l, true);
                sensorData.setType(LibConstants.SENSOR_GYROSCOPE);
                gyroDataArrList.add((SensorDataImpl) sensorData);

                if (gyroDataArrList.size() > 100) {
                    storeSensorAsync(LibConstants.SENSOR_GYROSCOPE, new ArrayList<SensorDataImpl>(gyroDataArrList));
                    gyroDataArrList.clear();
                }
                break;

            case LibConstants.SENSOR_LIGHT:
                LightReading lreading = (LightReading) reading;
                sensorData = new LightData(null, reading.timestamp, lreading.getLuxValue(), lreading.volatility,
                        lreading.isShare);
                sensorData.setType(LibConstants.SENSOR_LIGHT);
                lightDataArrList.add((SensorDataImpl) sensorData);

                if (lightDataArrList.size() > 100) {
                    storeSensorAsync(LibConstants.SENSOR_LIGHT, new ArrayList<SensorDataImpl>(lightDataArrList));
                    lightDataArrList.clear();
                }
                break;

            case LibConstants.SENSOR_LOCATION:
                LocationReading locReading = (LocationReading) reading;
                sensorData = new LocationData(null, reading.timestamp, locReading.getLatnLong()[0],
                        locReading.getLatnLong()[1], 0.0, 0l, true);
                sensorData.setType(LibConstants.SENSOR_LOCATION);
                locDataArrList.add((SensorDataImpl) sensorData);
                if (locDataArrList.size() > 100) {
                    storeSensorAsync(LibConstants.SENSOR_LOCATION, new ArrayList<SensorDataImpl>(locDataArrList));
                    locDataArrList.clear();
                }
                break;

            case LibConstants.SENSOR_NOISE:
                NoiseReading noiseReading = (NoiseReading) reading;
                sensorData = new NoiseData(null, reading.timestamp, noiseReading.getdbValue(), 0l, true);
                sensorData.setType(LibConstants.SENSOR_NOISE);
                noiseDataArrList.add((SensorDataImpl) sensorData);
                if (noiseDataArrList.size() > 100) {
                    storeSensorAsync(LibConstants.SENSOR_NOISE, new ArrayList<SensorDataImpl>(noiseDataArrList));
                    noiseDataArrList.clear();
                }
                break;

            case LibConstants.SENSOR_NOTIFICATION:
                NotificationReading notificationReading = (NotificationReading) reading;
                sensorData = new NotificationData(null, reading.timestamp, notificationReading.getAppName(), true);
                sensorData.setType(LibConstants.SENSOR_NOTIFICATION);
                notificationDataArrList.add((SensorDataImpl) sensorData);
                if (notificationDataArrList.size() > 100) {
                    storeSensorAsync(LibConstants.SENSOR_NOTIFICATION, new ArrayList<SensorDataImpl>(notificationDataArrList));
                    notificationDataArrList.clear();
                }
                break;

            case LibConstants.SENSOR_PROXIMITY:
                ProximityReading proxReading = (ProximityReading) reading;
                sensorData = new ProximityData(null, proxReading.timestamp, proxReading.getProximity(), proxReading.volatility, proxReading.isShare);
                sensorData.setType(LibConstants.SENSOR_PROXIMITY);
                proxDataArrList.add((SensorDataImpl) sensorData);
                if (proxDataArrList.size() > 100) {
                    storeSensorAsync(LibConstants.SENSOR_PROXIMITY, new ArrayList<SensorDataImpl>(proxDataArrList));
                    proxDataArrList.clear();
                }
                break;

            default:
                break;

        }
    }

    public String getUUID() {

        return ((Config)loadVMConfig()).getUUID();
    }

    class StoreTask extends AsyncTask<ArrayList<SensorDataImpl>, Integer, Void> {
        int type;

        public StoreTask(int type) {
            this.type = type;
        }

        @Override
        protected Void doInBackground(ArrayList<SensorDataImpl>... params) {
            storeSensor(type, params[0]);
            return null;
        }

    }

}
