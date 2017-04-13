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

public class LibConstants {

    public static final int SENSOR_ACCELEROMETER = 1;
    public static final int SENSOR_BATTERY = 2;
    public static final int SENSOR_GYROSCOPE = 4;
    public static final int SENSOR_LOCATION = 6;
    public static final int SENSOR_LIGHT = 3;
    public static final int SENSOR_NOISE = 5;
    public static final int SENSOR_PROXIMITY = 7;

    public static final int DEVICE_INFO = 101;

    public static final int INFO = -1;
    public static final int ERROR = -2;

}
