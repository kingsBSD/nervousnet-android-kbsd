package ch.ethz.coss.nervousnet.vm.events;

/**
 * Created by prasad on 30/08/16.
 */
public class NNEvent {

    public byte eventType;
    public byte state;
    public long sensorID;

    public NNEvent(long sensorID, byte state, byte type) {
        this.sensorID = sensorID;
        this.state = state;
        this.eventType = type;
    }


    public NNEvent(byte state, byte type) {
        this.eventType = type;
        this.state = state;
    }

    public NNEvent(byte type) {
        this.eventType = type;
    }


}
