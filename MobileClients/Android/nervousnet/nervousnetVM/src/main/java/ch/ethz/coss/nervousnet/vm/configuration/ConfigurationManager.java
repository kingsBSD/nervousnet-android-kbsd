package ch.ethz.coss.nervousnet.vm.configuration;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * ConfigurationManager is responsible for sensor collection state. It is responsible
 * for storing the state when some sensor collection configuration changes and restoring
 * the state when the application turns back on. This is necessary because application
 * crashes can appear. The idea is that the whole state is managed by ConfigurationManager.
 * <br/>
 * At the initialization of the ConfigurationManager, the manager goes through the
 * configuration file, that holds all sensors' configurations, and initializes sensors
 * according to the configurations. Then, it checks database and restores states of the
 * sensors that are stored before the app crashes or is shut down - default states form configuration file
 * are overwritten. If there is nothing to be restored, the states from configuration file persist.
 * <br/>
 * All configurations are hold in hashmap that map sensors' IDs into their configuration.
 * <br/>
 * TODO: To make this class a singleton. At the current development
 * stage, this is not the case yet.
 * TODO: There may be better solutions for database management.
 */
public class ConfigurationManager implements iConfigurationManager {

    /**
     * This hashmap maps sensor ID into sensor's configuration.
     */
    HashMap<Long, GeneralSensorConfiguration> configMap;
    /**
     * Database manager for sensors' configuration storing and for application state storing.
     */
    StateDBManager stateDBManager;

    public ConfigurationManager(Context context) {
        this.configMap = new HashMap<>();
        this.stateDBManager = new StateDBManager(context);
        // Load default configuration from configuration file
        JsonConfigurationLoader loader = new JsonConfigurationLoader(context);
        ArrayList<BasicSensorConfiguration> confList = loader.load();
        // Check database if there are some stored states and overwrite default states from
        // the configuration file.
        for (BasicSensorConfiguration conf : confList){
            configMap.put(conf.getSensorID(), conf);
            try {
                int state = stateDBManager.getSensorState(conf.getSensorID());
                conf.setState(state);
            } catch (NoSuchElementException e) {
                stateDBManager.storeSensorState(conf.getSensorID(), conf.getState());
            }
        }
    }

    @Override
    public Collection<GeneralSensorConfiguration> getAllConfigurations() {
        return configMap.values();
    }

    @Override
    public Set<Long> getSensorIDs(){
        return configMap.keySet();
    }

    @Override
    public GeneralSensorConfiguration getConfiguration(long sensorID) throws NoSuchElementException {
        if (configMap.containsKey(sensorID))
            return configMap.get(sensorID);
        else
            throw new NoSuchElementException("Sensor " + sensorID + " has not been configured.");
    }

    @Override
    public int getSensorState(long sensorID) throws NoSuchElementException{
        if (configMap.containsKey(sensorID))
            return ((BasicSensorConfiguration)configMap.get(sensorID)).getState();
        else
            throw new NoSuchElementException("Sensor " + sensorID + " has not been configured.");
    }

    @Override
    public void setSensorState(long sensorID, int state) {
        if (configMap.containsKey(sensorID)) {
            stateDBManager.storeSensorState(sensorID, state);
            ((BasicSensorConfiguration) configMap.get(sensorID)).setState(state);
        }
        else
            throw new NoSuchElementException("Sensor " + sensorID + " has not been configured.");
    }

    @Override
    /**
     * @return Application state.
     */
    public int getNervousnetState() {
        try {
            return stateDBManager.getNervousnetState();
        } catch (NoSuchElementException e) {
            return NervousnetVMConstants.STATE_PAUSED;
        }
    }

    @Override
    /**
     * Set application state.
     */
    public void setNervousnetState(int state) throws NoSuchElementException {
        stateDBManager.storeNervousnetState(state);
    }

}
