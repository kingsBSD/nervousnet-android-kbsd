package ch.ethz.coss.nervousnet.vm;

/**
 * Created by ales on 15/12/16.
 */
public class SensorIsOffException extends Exception {
    public SensorIsOffException(String msg){
        super(msg);
    }
}
