package ch.ethz.coss.nervousnet.lib;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by grg on 02/09/16.
 */
public class SocketReading extends SensorReading {

    private String[] appName = new String[1];
    private String[] protocol = new String[1];
    private int port;

    public SocketReading(long timestamp, String name, String protocol, int port) {
        this.type = LibConstants.SENSOR_SOCKET;
        this.timestamp = timestamp;
        this.appName[0] = name;
        this.protocol[0] = protocol;
        this.port = port;
    }

    public SocketReading(Parcel in) {
        readFromParcel(in);
    }

    public static final Parcelable.Creator<SocketReading> CREATOR = new Parcelable.Creator<SocketReading>() {
        @Override
        public SocketReading createFromParcel(Parcel in) {
            return new SocketReading(in);
        }

        @Override
        public SocketReading[] newArray(int size) {
            return new SocketReading[size];
        }
    };

    public String getAppName() {
        return appName[0];
    }

    public String getProtocol() { return protocol[0]; }

    public int getPort() { return port; }

    public void readFromParcel(Parcel in) {
        timestamp = in.readLong();
        in.readStringArray(appName);
        in.readStringArray(protocol);
        port = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(getClass().getName());
        out.writeLong(timestamp);
        out.writeStringArray(appName);
        out.writeStringArray(protocol);
        out.writeInt(port);
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

}
