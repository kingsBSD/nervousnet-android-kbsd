package ch.ethz.coss.nervousnet.vm.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import de.greenrobot.dao.AbstractDaoMaster;
import de.greenrobot.dao.identityscope.IdentityScopeType;

import ch.ethz.coss.nervousnet.vm.storage.ConfigDao;
import ch.ethz.coss.nervousnet.vm.storage.SensorConfigDao;
import ch.ethz.coss.nervousnet.vm.storage.AuthenticationDao;
import ch.ethz.coss.nervousnet.vm.storage.LocationDataDao;
import ch.ethz.coss.nervousnet.vm.storage.AccelDataDao;
import ch.ethz.coss.nervousnet.vm.storage.BatteryDataDao;
import ch.ethz.coss.nervousnet.vm.storage.BeaconDataDao;
import ch.ethz.coss.nervousnet.vm.storage.ConnectivityDataDao;
import ch.ethz.coss.nervousnet.vm.storage.GyroDataDao;
import ch.ethz.coss.nervousnet.vm.storage.HumidityDataDao;
import ch.ethz.coss.nervousnet.vm.storage.LightDataDao;
import ch.ethz.coss.nervousnet.vm.storage.MagneticDataDao;
import ch.ethz.coss.nervousnet.vm.storage.NoiseDataDao;
import ch.ethz.coss.nervousnet.vm.storage.NotificationDataDao;
import ch.ethz.coss.nervousnet.vm.storage.PressureDataDao;
import ch.ethz.coss.nervousnet.vm.storage.ProximityDataDao;
import ch.ethz.coss.nervousnet.vm.storage.TemperatureDataDao;
import ch.ethz.coss.nervousnet.vm.storage.TrafficDataDao;
import ch.ethz.coss.nervousnet.vm.storage.SocketDataDao;
import ch.ethz.coss.nervousnet.vm.storage.PacketDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * Master of DAO (schema version 3): knows all DAOs.
*/
public class DaoMaster extends AbstractDaoMaster {
    public static final int SCHEMA_VERSION = 3;

    /** Creates underlying database table using DAOs. */
    public static void createAllTables(SQLiteDatabase db, boolean ifNotExists) {
        ConfigDao.createTable(db, ifNotExists);
        SensorConfigDao.createTable(db, ifNotExists);
        AuthenticationDao.createTable(db, ifNotExists);
        LocationDataDao.createTable(db, ifNotExists);
        AccelDataDao.createTable(db, ifNotExists);
        BatteryDataDao.createTable(db, ifNotExists);
        BeaconDataDao.createTable(db, ifNotExists);
        ConnectivityDataDao.createTable(db, ifNotExists);
        GyroDataDao.createTable(db, ifNotExists);
        HumidityDataDao.createTable(db, ifNotExists);
        LightDataDao.createTable(db, ifNotExists);
        MagneticDataDao.createTable(db, ifNotExists);
        NoiseDataDao.createTable(db, ifNotExists);
        NotificationDataDao.createTable(db, ifNotExists);
        PressureDataDao.createTable(db, ifNotExists);
        ProximityDataDao.createTable(db, ifNotExists);
        TemperatureDataDao.createTable(db, ifNotExists);
        TrafficDataDao.createTable(db, ifNotExists);
        SocketDataDao.createTable(db, ifNotExists);
        PacketDao.createTable(db, ifNotExists);
    }
    
    /** Drops underlying database table using DAOs. */
    public static void dropAllTables(SQLiteDatabase db, boolean ifExists) {
        ConfigDao.dropTable(db, ifExists);
        SensorConfigDao.dropTable(db, ifExists);
        AuthenticationDao.dropTable(db, ifExists);
        LocationDataDao.dropTable(db, ifExists);
        AccelDataDao.dropTable(db, ifExists);
        BatteryDataDao.dropTable(db, ifExists);
        BeaconDataDao.dropTable(db, ifExists);
        ConnectivityDataDao.dropTable(db, ifExists);
        GyroDataDao.dropTable(db, ifExists);
        HumidityDataDao.dropTable(db, ifExists);
        LightDataDao.dropTable(db, ifExists);
        MagneticDataDao.dropTable(db, ifExists);
        NoiseDataDao.dropTable(db, ifExists);
        NotificationDataDao.dropTable(db, ifExists);
        PressureDataDao.dropTable(db, ifExists);
        ProximityDataDao.dropTable(db, ifExists);
        TemperatureDataDao.dropTable(db, ifExists);
        TrafficDataDao.dropTable(db, ifExists);
        SocketDataDao.dropTable(db, ifExists);
        PacketDao.dropTable(db, ifExists);
    }
    
    public static abstract class OpenHelper extends SQLiteOpenHelper {

        public OpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory, SCHEMA_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i("greenDAO", "Creating tables for schema version " + SCHEMA_VERSION);
            createAllTables(db, false);
        }
    }
    
    /** WARNING: Drops all table on Upgrade! Use only during development. */
    public static class DevOpenHelper extends OpenHelper {
        public DevOpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
            dropAllTables(db, true);
            onCreate(db);
        }
    }

    public DaoMaster(SQLiteDatabase db) {
        super(db, SCHEMA_VERSION);
        registerDaoClass(ConfigDao.class);
        registerDaoClass(SensorConfigDao.class);
        registerDaoClass(AuthenticationDao.class);
        registerDaoClass(LocationDataDao.class);
        registerDaoClass(AccelDataDao.class);
        registerDaoClass(BatteryDataDao.class);
        registerDaoClass(BeaconDataDao.class);
        registerDaoClass(ConnectivityDataDao.class);
        registerDaoClass(GyroDataDao.class);
        registerDaoClass(HumidityDataDao.class);
        registerDaoClass(LightDataDao.class);
        registerDaoClass(MagneticDataDao.class);
        registerDaoClass(NoiseDataDao.class);
        registerDaoClass(NotificationDataDao.class);
        registerDaoClass(PressureDataDao.class);
        registerDaoClass(ProximityDataDao.class);
        registerDaoClass(TemperatureDataDao.class);
        registerDaoClass(TrafficDataDao.class);
        registerDaoClass(SocketDataDao.class);
        registerDaoClass(PacketDao.class);
    }
    
    public DaoSession newSession() {
        return new DaoSession(db, IdentityScopeType.Session, daoConfigMap);
    }
    
    public DaoSession newSession(IdentityScopeType type) {
        return new DaoSession(db, type, daoConfigMap);
    }
    
}
