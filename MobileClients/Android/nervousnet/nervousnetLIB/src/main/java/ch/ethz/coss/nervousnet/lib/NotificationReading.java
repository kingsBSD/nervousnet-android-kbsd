package ch.ethz.coss.nervousnet.lib;

import android.app.Notification;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by grg on 16/08/16.
 */
public class NotificationReading extends SensorReading {

    public static final Parcelable.Creator<NotificationReading> CREATOR = new Parcelable.Creator<NotificationReading>() {
        @Override
        public NotificationReading createFromParcel(Parcel in) {
            return new NotificationReading(in);
        }

        @Override
        public NotificationReading[] newArray(int size) {
            return new NotificationReading[size];
        }
    };

    private String[] appName = new String[1];

    public NotificationReading(long timestamp, String name) {
        this.type = LibConstants.SENSOR_NOTIFICATION;
        this.timestamp = timestamp;
        this.appName[0] = name;
    }

    public NotificationReading(Parcel in) {
        readFromParcel(in);
    }

    public String getAppName() {
        return appName[0];
    }

    public void readFromParcel(Parcel in) {
        timestamp = in.readLong();
        in.readStringArray(appName);
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(getClass().getName());
        out.writeLong(timestamp);
        out.writeStringArray(appName);
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }
}
