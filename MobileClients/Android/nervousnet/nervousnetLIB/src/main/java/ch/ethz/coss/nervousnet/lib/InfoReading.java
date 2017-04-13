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

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * @author prasad
 */
public class InfoReading extends SensorReading {

    public static final Parcelable.Creator<InfoReading> CREATOR = new Parcelable.Creator<InfoReading>() {
        @Override
        public InfoReading createFromParcel(Parcel in) {
            return new InfoReading(in);
        }

        @Override
        public InfoReading[] newArray(int size) {
            return new InfoReading[size];
        }
    };
    private String[] infoValues = new String[2];

    public InfoReading() {
        this.setSensorID(LibConstants.ERROR);
    }

    public InfoReading(String[] values) {
        this.setSensorID(LibConstants.ERROR);
        this.infoValues = values;
    }

    /**
     * @param in
     */
    public InfoReading(Parcel in) {
        readFromParcel(in);
    }

    public int getInfoCode() {
        return Integer.parseInt(infoValues[0]);
    }

    public String getInfoString() {
        return infoValues[1];
    }

    public void readFromParcel(Parcel in) {
        Log.e("InfoReading", "Exception - not able to bind ! ");

        in.readStringArray(infoValues);
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
        out.writeStringArray(infoValues);
    }

}
