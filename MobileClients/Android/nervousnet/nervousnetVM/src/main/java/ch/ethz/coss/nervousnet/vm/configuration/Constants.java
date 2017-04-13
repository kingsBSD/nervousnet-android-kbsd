package ch.ethz.coss.nervousnet.vm.configuration;

/**
 * This class holds static constants for database that holds configurations.
 * TODO: This is temporary solution. It would be better to make this safer as
 * it is possible that a developer creates new database with the same name
 * and some collisions can appear.
 */
public class Constants {

    public static final String DATABASE_NAME = "ConfigDB";
    public static final int DATABASE_VERSION = 1;
    public static final String SENSOR_CONFIG_TABLE = "ConfigTable";
    public static final String STATE = "State";
    public static final String ID = "ID";
    public static final String NERVOUSNET_CONFIG_TABLE = "ConfigNervousnetTable";

}
