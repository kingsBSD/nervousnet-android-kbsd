package ch.ethz.coss.nervousnet.lib;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by grg on 23/08/16.
 */
public class TrafficReading extends SensorReading {

    private String[] appName = new String[1];
    private long txBytes;
    private long rxBytes;

    public TrafficReading(long timestamp, String name, long tx, long rx) {
        this.type = LibConstants.SENSOR_TRAFFIC;
        this.timestamp = timestamp;
        this.appName[0] = name;
        this.txBytes = tx;
        this.rxBytes = rx;
    }

    public TrafficReading(Parcel in) {
        readFromParcel(in);
    }

    public static final Parcelable.Creator<TrafficReading> CREATOR = new Parcelable.Creator<TrafficReading>() {
        @Override
        public TrafficReading createFromParcel(Parcel in) {
            return new TrafficReading(in);
        }

        @Override
        public TrafficReading[] newArray(int size) {
            return new TrafficReading[size];
        }
    };

    public String getAppName() {
        return appName[0];
    }


    public void readFromParcel(Parcel in) {
        timestamp = in.readLong();
        in.readStringArray(appName);
        txBytes = in.readLong();
        rxBytes = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(getClass().getName());
        out.writeLong(timestamp);
        out.writeStringArray(appName);
        out.writeLong(txBytes);
        out.writeLong(rxBytes);
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

}
