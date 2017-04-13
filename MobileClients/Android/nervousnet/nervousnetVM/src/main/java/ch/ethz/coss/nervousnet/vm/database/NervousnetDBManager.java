package ch.ethz.coss.nervousnet.vm.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import ch.ethz.coss.nervousnet.lib.SensorReading;
import ch.ethz.coss.nervousnet.vm.configuration.GeneralSensorConfiguration;


/**
 * This class enables that values of sensors get stored and queried.
 * First, table for a specific sensor type has to be created by calling
 * {@link #createTableIfNotExists(GeneralSensorConfiguration)}  createTableIfNotExists}.
 * Then, SensorReading values of the sensor can be stored. As it is very expensive,
 * that evey SensorReading get stored individually, a 'cache' mechanism is implemented.
 * When a SensorReading is stored, it goes into a temporary storage and there is a
 * timer (storingRate) which tells the scheduler when to store temporary into the
 * database. The scheduler calls method {@link #run() run} which performs that temporary
 * storage is stored into the database and clears temporary storage for new input SensorReadings.
 * TODO: If would be great that method {@link #createTableIfNotExists(GeneralSensorConfiguration)}  createTableIfNotExists}
 * doesn't be called from outside. Little work can improve this.
 */
public class NervousnetDBManager extends SQLiteOpenHelper implements Runnable {

    private static final String LOG_TAG = NervousnetDBManager.class.getSimpleName();


    // Defining a singleton
    private static NervousnetDBManager instance = null;


    // Extra hashmap for the latest insert only. Each registered sensor has
    // the latest sensor value stored here.
    private static HashMap<Long, SensorReading> LATEST_SENSORS_DATA = new HashMap();


    // Temporary storage behaves like cache. Inserting each sensor value individually
    // can be very unefficient. For this reason, it collects data for the given interval
    // and then stores all of it.
    private static HashMap<Long, ArrayList<SensorReading>> TEMPORARY_STORAGE = new HashMap();


    // Initial delay for storing is used at start up of the application. Sometimes,
    // it is good to wait a bit and then start the scheduler for storing the data.
    private long initialDelayForStoring = 5000; //5 sec


    // Storing rate is a parameter which tells scheduler at what rate it should store
    // the data collected in temporary storage.
    private long storingRate = 10000; // 10 sec


    // Scheduler, which dictates storing of cache
    ScheduledExecutorService scheduler;


    // Constructor is private as we want only one instance of the class to prevent database
    // conflicts.
    private NervousnetDBManager(Context context) {
        super(context, ConstantsDB.DATABASE_NAME, null, ConstantsDB.DATABASE_VERSION);
        instance = this;
        startSchedluer();
    }


    /**
     * Creates new instance if it does not exist and returns it.
     * @param context
     * @return instance of NervousnetDBManager
     */
    public static NervousnetDBManager getInstance(Context context){
        if (instance == null){
            instance = new NervousnetDBManager(context);
        }
        return instance;
    }


    private void startSchedluer(){
        // Create new scheduler and run it
        this.scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(instance, initialDelayForStoring,
                storingRate, TimeUnit.MILLISECONDS);
        Log.d(LOG_TAG, "Start scheduler for storing at rate " + storingRate);
    }


    //####################################################################
    //QUERY
    //####################################################################

    // Querying the data usually requires GeneralSensorConfiguraion object.
    // The class provides all the data, which is necessary to create
    // SensorReading object.

    /**
     * Returns latest reading that has been stored since starting the application.
     */
    public static SensorReading getLatestReading(long sensorID) throws NoSuchElementException{
        if (LATEST_SENSORS_DATA.containsKey(sensorID))
            return LATEST_SENSORS_DATA.get(sensorID);
        else
            throw new NoSuchElementException("No data arrived yet for the sensor id " + sensorID);
    }

    /**
     * Returns list of all readings for a sensor specified in config.
     */
    public ArrayList<SensorReading> getReadings(
            GeneralSensorConfiguration config)
    {
        String query = "SELECT * FROM " + getTableName(config.getSensorID());
        return getReadings(config, query);
    }


    /**
     * Returns list of readings in the interval specified with start timestamp and stop timestamp in
     * milliseconds.
     */
    public ArrayList<SensorReading> getReadings(
            GeneralSensorConfiguration config, long startTimestamp, long endTimestamp)
    {
        String query = "SELECT * FROM " + getTableName(config.getSensorID()) +
                " WHERE " + ConstantsDB.TIMESTAMP + " >= " + startTimestamp + " AND " +
                ConstantsDB.TIMESTAMP + " <= " + endTimestamp + ";";
        return getReadings(config, query);
    }


    /**
     * Returns list of readings in the interval specified with start timestamp and stop timestamp in
     * milliseconds. Additionally, user can specify sensor parameter names to be selected. Other
     * sensor parameters are ignored.
     */
    public ArrayList<SensorReading> getReadings(
            GeneralSensorConfiguration config, long start, long stop,
            ArrayList<String> selectParameters)
    {
        String cols = TextUtils.join(", ", selectParameters);
        String query = "SELECT " + ConstantsDB.ID + ", " +
                ConstantsDB.TIMESTAMP + ", " + cols +
                " FROM " + getTableName(config.getSensorID()) +
                " WHERE " + ConstantsDB.TIMESTAMP + " >= " + start + " AND " +
                "" + ConstantsDB.TIMESTAMP + " <= " + stop + ";";
        return getReadings(config, query);
    }


    /**
     * Returns list of readings based on a manual sql query.
     */
    public synchronized ArrayList<SensorReading> getReadings(
            GeneralSensorConfiguration config, String query)
    {
        ArrayList<String> sensorParamNames = config.getParametersNames();
        SQLiteDatabase DATABASE = getReadableDatabase();
        // TODO: check this version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            DATABASE.enableWriteAheadLogging();
        }
        Cursor cursor = DATABASE.rawQuery(query, null);
        ArrayList<SensorReading> returnList = new ArrayList();
        if (cursor.moveToFirst()) {
            do {
                int indexTimestamp = cursor.getColumnIndex(ConstantsDB.TIMESTAMP);
                SensorReading reading = new SensorReading(
                        config.getSensorID(),
                        config.getSensorName(),
                        sensorParamNames);
                reading.setTimestampEpoch(cursor.getLong(indexTimestamp));
                for (String columnName : sensorParamNames){
                    int indexParam = cursor.getColumnIndex(columnName);
                    Object value = null;
                    // TODO: check this version
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        int type = cursor.getType(indexParam);
                        switch (type) {
                            case ConstantsDB.FIELD_TYPE_INTEGER:
                                value = cursor.getInt(indexParam); break;
                            case ConstantsDB.FIELD_TYPE_FLOAT:
                                value = cursor.getFloat(indexParam); break;
                            case ConstantsDB.FIELD_TYPE_STRING:
                                value = cursor.getString(indexParam); break;
                            default:
                                value = null;
                        }
                    }
                    reading.setValue(columnName, value);
                }
                returnList.add(reading);
            } while (cursor.moveToNext());
        }
        cursor.close();
        DATABASE.close();
        return returnList;
    }



    //####################################################################
    //STORE
    //####################################################################

    /**
     * Delete table of the sesnor.
     */
    public synchronized void deleteTableIfExists(long sensorID){
        String sql = "DROP TABLE IF EXISTS " + getTableName(sensorID) + ";";
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(sql);
        db.close();
    }


    /**
     * Create table for a sensor. Configuration of a sensor has to be passed.
     */
    public synchronized void createTableIfNotExists(GeneralSensorConfiguration config){
        Log.d(LOG_TAG, "createTableIfNotExists called with name: "+getTableName(config.getSensorID()));
        String sql = "CREATE TABLE IF NOT EXISTS " +
                getTableName(config.getSensorID()) + " ( " +
                ConstantsDB.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ConstantsDB.TIMESTAMP + " INTEGER";
        ArrayList<String> paramNames = config.getParametersNames();
        ArrayList<String> paramTypes = config.getParametersTypes();
        for (int i = 0; i < config.getDimension(); i++){
            String type = "";
            switch (paramTypes.get(i)){
                case "int":
                    type = "INT"; break;
                case "double":
                    type = "REAL"; break;
                case "String":
                    type = "TEXT"; break;
            }
            sql += ", " + paramNames.get(i) + " " + type;
        }
        sql += " );";
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(sql);
        db.close();
    }


    /**
     * Store list of readings.
     */
    public synchronized void store(ArrayList<? extends SensorReading> readings){
        SQLiteDatabase db = this.getWritableDatabase();
        for (SensorReading reading : readings) {
            ContentValues insertList = new ContentValues();
            insertList.put(ConstantsDB.TIMESTAMP, reading.getTimestampEpoch());
            ArrayList<String> paramNames = reading.getParametersNames();
            ArrayList<Object> values = reading.getValues();
            for (int i = 0; i < paramNames.size(); i++) {
                String name = paramNames.get(i);
                Object val = values.get(i);
                if (val.getClass().equals(String.class))
                    insertList.put(name, (String) val);
                else if (val instanceof Integer)
                    insertList.put(name, (Integer) val);
                else if (val instanceof Double)
                    insertList.put(name, (Double) val);
                else if (val instanceof Float)
                    insertList.put(name, (Float) val);
                else if (val instanceof Long)
                    insertList.put(name, (Long) val);
                else
                    insertList.putNull(name);
            }
            db.insert(getTableName(reading.getSensorID()), null, insertList);
        }
        db.close();
    }


    private synchronized void store(Collection<ArrayList<SensorReading>> readingsList){
        SQLiteDatabase db = this.getWritableDatabase();
        boolean allEmpty = true;
        for (ArrayList<SensorReading> readings : readingsList) {
            Log.d(LOG_TAG, "Store " + readings.get(0).getSensorName() + " size " + readings.size());
            if (!readings.isEmpty()) allEmpty = false;
            for (SensorReading reading : readings) {
                ContentValues insertList = new ContentValues();
                insertList.put(ConstantsDB.TIMESTAMP, reading.getTimestampEpoch());
                ArrayList<String> paramNames = reading.getParametersNames();
                ArrayList<Object> values = reading.getValues();
                for (int i = 0; i < paramNames.size(); i++) {
                    String name = paramNames.get(i);
                    Object val = values.get(i);
                    if (val.getClass().equals(String.class))
                        insertList.put(name, (String) val);
                    else if (val instanceof Integer)
                        insertList.put(name, (Integer) val);
                    else if (val instanceof Double)
                        insertList.put(name, (Double) val);
                    else if (val instanceof Float)
                        insertList.put(name, (Float) val);
                    else if (val instanceof Long)
                        insertList.put(name, (Long) val);
                    else
                        insertList.putNull(name);
                }
                db.insert(getTableName(reading.getSensorID()), null, insertList);
            }
        }
        db.close();
        if (allEmpty){
            this.scheduler.shutdown();
            Log.d(LOG_TAG, "Shutdown the NervousnetDBManager");
        }
    }


    /**
     * Store reading into cache.
     */
    public void store(SensorReading reading){
        if (scheduler.isShutdown()){
            startSchedluer();
        }
        LATEST_SENSORS_DATA.put(reading.getSensorID(), reading);
        if (TEMPORARY_STORAGE.containsKey(reading.getSensorID())) {
            ArrayList<SensorReading> readings = TEMPORARY_STORAGE.get(reading.getSensorID());
            readings.add(reading);
        }
        else {
            ArrayList<SensorReading> newArray = new ArrayList();
            newArray.add(reading);
            TEMPORARY_STORAGE.put(reading.getSensorID(), newArray);
        }
    }


    /**
     * Delete readings that are older than threshold timestamp (milliseconds).
     */
    public synchronized void removeOldReadings(long sensorID, long threshold){
        String sql = "DELETE FROM " + getTableName(sensorID) +
                " WHERE " + ConstantsDB.TIMESTAMP + " < " + threshold + ";";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
        db.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


    private static String getTableName(long sensorID){
        return "ID" + String.valueOf(sensorID);
    }

    /**
     * Start periodic storage of readings. If run method is not called, readings will not be
     * stored into database but will be kept only in cache.
     */
    @Override
    public void run() {
        HashMap<Long, ArrayList<SensorReading>> tmp = TEMPORARY_STORAGE;
        TEMPORARY_STORAGE = new HashMap<>();
        long startTime = System.currentTimeMillis();
        store(tmp.values());
        long stopTime = System.currentTimeMillis();
        long duration = stopTime - startTime;
        Log.d(LOG_TAG, "Store all readings in " + duration + "ms");
    }
}
