/*******************************************************************************
 * *     Nervousnet - a distributed middleware software for social sensing.
 * *      It is responsible for collecting and managing data in a fully de-centralised fashion
 * *
 * *     Copyright (C) 2016 ETH Zürich, COSS
 * *
 * *     This file is part of Nervousnet Framework
 * *
 * *     Nervousnet is free software: you can redistribute it and/or modify
 * *     it under the terms of the GNU General Public License as published by
 * *     the Free Software Foundation, either version 3 of the License, or
 * *     (at your option) any later version.
 * *
 * *     Nervousnet is distributed in the hope that it will be useful,
 * *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 * *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * *     GNU General Public License for more details.
 * *
 * *     You should have received a copy of the GNU General Public License
 * *     along with NervousNet. If not, see <http://www.gnu.org/licenses/>.
 * *
 * *
 * * 	Contributors:
 * * 	Prasad Pulikal - prasad.pulikal@gess.ethz.ch  -  Initial API and implementation
 *******************************************************************************/

package ch.ethz.coss.nervousnet.lib;

import android.os.BatteryManager;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author prasad
 */
public class BatteryReading extends SensorReading {

    public static final Parcelable.Creator<BatteryReading> CREATOR = new Parcelable.Creator<BatteryReading>() {
        @Override
        public BatteryReading createFromParcel(Parcel in) {
            return new BatteryReading(in);
        }

        @Override
        public BatteryReading[] newArray(int size) {
            return new BatteryReading[size];
        }
    };
    private float percent;
    private boolean isCharging;
    private byte charging_type = 0; // 0 = Unknown, 1 = USB, 2 = AC, 3 =
    // Wireless
    private float temp = 0;
    private int volt = 0;
    private byte health = 0; // 0 = Unknown, -1 is not supported
    private String tech;

    public BatteryReading(long timestamp, float batteryPercent, boolean isCharging, boolean isUsbCharge,
                          boolean isAcCharge, float temp, int volt, byte health, String tech) {
        this.type = LibConstants.SENSOR_BATTERY;
        this.timestamp = timestamp;
        this.percent = batteryPercent;
        this.isCharging = isCharging;
        this.charging_type = (byte) (isUsbCharge ? 1 : (isAcCharge ? 2 : 0));
        this.temp = temp;
        this.volt = volt;
        this.health = health;
        this.tech = tech;
    }

    public BatteryReading(boolean isCollect) {
        super(isCollect);
    }

    /**
     * @param in
     */
    public BatteryReading(Parcel in) {
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {

        timestamp = in.readLong();
        percent = in.readFloat();
        boolean array[] = in.createBooleanArray();
        isCharging = array[0];

        charging_type = in.readByte();
        temp = in.readFloat();
        volt = in.readInt();
        health = in.readByte();
        tech = in.readString();

    }

    /*
     * (non-Javadoc)
     *
     * @see android.os.Parcelable#describeContents()
     */
    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
     */
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(getClass().getName());
        out.writeLong(timestamp);
        out.writeFloat(percent);
        out.writeBooleanArray(new boolean[]{isCharging});
        out.writeByte(charging_type);
        out.writeFloat(temp);
        out.writeInt(volt);
        out.writeByte(health);
        out.writeString(tech);

    }

    /**
     * @return the isCharging
     */
    public boolean isCharging() {
        return isCharging;
    }

    /**
     * @return the percent
     */
    public float getPercent() {
        return percent;
    }

    /**
     * @return the charging_type
     */
    public byte getCharging_type() {
        return charging_type;
    }

    /**
     * @return the temp
     */
    public float getTemp() {
        return temp / 10;
    }

    /**
     * @return the volt
     */
    public int getVolt() {
        return volt;
    }

    /**
     * @return the health
     */
    public byte getHealth() {
        return health;
    }

    /**
     * @return the technology String
     */
    public String getTechnology() {
        return tech;
    }

    /**
     * @return the health status in string
     */
    public String getHealthString() {

        switch (health) {
            default:
            case 0:
            case (byte) BatteryManager.BATTERY_HEALTH_UNKNOWN:
                return new String("Unknown");

            case (byte) BatteryManager.BATTERY_HEALTH_GOOD:
                return new String("Good");

            case (byte) BatteryManager.BATTERY_HEALTH_OVERHEAT:
                return new String("Overheated");

            case (byte) BatteryManager.BATTERY_HEALTH_DEAD:
                return new String("Dead");

            case (byte) BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                return new String("Over Voltage");

            case (byte) BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                return new String("Unspecified Failure");

            case (byte) BatteryManager.BATTERY_HEALTH_COLD:
                return new String("Cold");

        }

    }

}
