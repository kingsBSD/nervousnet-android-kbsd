package ch.ethz.coss.nervousnet.vm.configuration;

import java.util.Collection;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * iConfigurationManager is an interface that defines methods of ConfigurationManager.
 * The idea is that this interface is the only connection between configuration package
 * and other packages. All management should be done through this interface.
 */
public interface iConfigurationManager {
    public Collection<GeneralSensorConfiguration> getAllConfigurations();
    public Set<Long> getSensorIDs();
    public GeneralSensorConfiguration getConfiguration(long sensorID) throws NoSuchElementException;
    public int getSensorState(long sensorID) throws NoSuchElementException;
    public void setSensorState(long sensorID, int state) throws NoSuchElementException;
    public int getNervousnetState();
    public void setNervousnetState(int state);
}
