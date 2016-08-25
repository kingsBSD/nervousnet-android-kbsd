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
package ch.ethz.coss.nervousnet.vm.sensors;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import ch.ethz.coss.nervousnet.lib.LocationReading;
import ch.ethz.coss.nervousnet.vm.NNLog;
import ch.ethz.coss.nervousnet.vm.NervousnetVMConstants;

public class LocationSensor extends BaseSensor implements LocationListener {

    private static String LOG_TAG = LocationSensor.class.getSimpleName();

    private LocationManager locationManager;
    private float MIN_UPDATE_DISTANCE = 1; // Minimum Distance between
    // updates in meters
    private long MIN_TIME_BW_UPDATES = 100; // Minimum Time between
    // updates in milliseconds.

    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;

    private boolean canGetLocation = false;

    private LocationReading reading;
    private Location location;
    private Context mContext;

    public LocationSensor(byte sensorState, LocationManager locationManager, Context context) {
        this.sensorState = sensorState;
        this.locationManager = locationManager;
        mContext = context;
    }

    @Override
    public boolean start() {


        if (sensorState == NervousnetVMConstants.SENSOR_STATE_NOT_AVAILABLE) {
            NNLog.d(LOG_TAG, "Cancelled Starting Location sensor as Sensor is not available.");
            return false;
        } else if (sensorState == NervousnetVMConstants.SENSOR_STATE_AVAILABLE_PERMISSION_DENIED) {
            NNLog.d(LOG_TAG, "Cancelled Starting Location sensor as permission denied by user.");
            return false;
        } else if (sensorState == NervousnetVMConstants.SENSOR_STATE_AVAILABLE_BUT_OFF) {
            NNLog.d(LOG_TAG, "Cancelled starting Location sensor as Sensor state is switched off.");
            return false;
        }

        NNLog.d(LOG_TAG, "Starting Location sensor with state = " + sensorState);

        MIN_TIME_BW_UPDATES = NervousnetVMConstants.sensor_freq_constants[0][sensorState - 1];
        startLocationCollection();
        // sensorManager.registerListener(this,
        // sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
        // NervousnetVMConstants.sensor_freq_constants[0][sensorState - 1]);

        return true;
    }

    @Override
    public boolean stopAndRestart(byte state) {

        if (state == NervousnetVMConstants.SENSOR_STATE_NOT_AVAILABLE) {
            NNLog.d(LOG_TAG, "Cancelled Starting Location sensor as Sensor is not available.");
            return false;
        } else if (state == NervousnetVMConstants.SENSOR_STATE_AVAILABLE_PERMISSION_DENIED) {
            NNLog.d(LOG_TAG, "Cancelled Starting Location sensor as permission denied by user.");
            return false;
        } else if (state == NervousnetVMConstants.SENSOR_STATE_AVAILABLE_BUT_OFF) {
            setSensorState(state);
            NNLog.d(LOG_TAG, "Cancelled starting Location sensor as Sensor state is switched off.");
            return false;
        }

        stop(false);

        setSensorState(state);
        NNLog.d(LOG_TAG, "Restarting Location sensor with state = " + sensorState);

        start();
        return true;
    }

    @Override
    public boolean stop(boolean changeStateFlag) {
        if (sensorState == NervousnetVMConstants.SENSOR_STATE_NOT_AVAILABLE) {
            NNLog.d(LOG_TAG, "Cancelled stop Location sensor as Sensor state is not available ");
            return false;
        } else if (sensorState == NervousnetVMConstants.SENSOR_STATE_AVAILABLE_PERMISSION_DENIED) {
            NNLog.d(LOG_TAG, "Cancelled stop Location sensor as permission denied by user.");
            return false;
        } else if (sensorState == NervousnetVMConstants.SENSOR_STATE_AVAILABLE_BUT_OFF) {
            NNLog.d(LOG_TAG, "Cancelled stop Location sensor as Sensor state is switched off ");
            return false;
        }
        setSensorState(NervousnetVMConstants.SENSOR_STATE_AVAILABLE_BUT_OFF);
        this.reading = null;
        locationManager.removeUpdates(LocationSensor.this);
        return true;
    }


    @TargetApi(23)
    public void startLocationCollection() {
        NNLog.d(LOG_TAG, "startLocationCollection ");

        if (Build.VERSION.SDK_INT >= 23
                && ContextCompat.checkSelfPermission(mContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(mContext,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//			ActivityCompat.requestPermissions((Activity)mContext, new String[]{"Manifest.permission.ACCESS_FINE_LOCATION"}, REQUEST_CODE_ASK_PERMISSIONS);
            setSensorState(NervousnetVMConstants.SENSOR_STATE_AVAILABLE_PERMISSION_DENIED);
            return;
        }

        if (locationManager == null)
            return;
        NNLog.d(LOG_TAG, "startLocationCollection2");

        // getting GPS status
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnabled && !isNetworkEnabled) {
            setSensorState(NervousnetVMConstants.SENSOR_STATE_AVAILABLE_PERMISSION_DENIED);
            NNLog.d(LOG_TAG, "Location settings disabled");
            // no network provider is enabled
            Toast.makeText(mContext, "Location settings disabled", Toast.LENGTH_LONG).show();
        } else {
            this.canGetLocation = true;
            // First get location from Network Provider
            if (isNetworkEnabled) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,
                        MIN_TIME_BW_UPDATES, this);
                NNLog.d(LOG_TAG, "Network");
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        reading = new LocationReading(System.currentTimeMillis(),
                                new double[]{location.getLatitude(), location.getLongitude()});
                    }
                }
            }
            // if GPS Enabled get lat/long using GPS Services
            if (isGPSEnabled) {
                if (location == null) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
                            MIN_TIME_BW_UPDATES, this);

                    NNLog.d(LOG_TAG, "GPS Enabled");

                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            reading = new LocationReading(System.currentTimeMillis(),
                                    new double[]{location.getLatitude(), location.getLongitude()});
                        }
                    }
                }
            }
            dataReady(reading);
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.location.LocationListener#onLocationChanged(android.location.
     * Location)
     */
    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        // TODO Auto-generated method stub
        reading = new LocationReading(System.currentTimeMillis(),
                new double[]{location.getLatitude(), location.getLongitude()});

        if (reading != null)
            dataReady(reading);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.location.LocationListener#onProviderDisabled(java.lang.String)
     */
    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.location.LocationListener#onProviderEnabled(java.lang.String)
     */
    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     *
     * @see android.location.LocationListener#onStatusChanged(java.lang.String,
     * int, android.os.Bundle)
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

}