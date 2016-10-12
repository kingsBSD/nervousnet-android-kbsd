/**
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
 */
/**
 *
 */
package ch.ethz.coss.nervousnet.hub.ui.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ch.ethz.coss.nervousnet.hub.R;
import ch.ethz.coss.nervousnet.lib.BatteryReading;
import ch.ethz.coss.nervousnet.lib.ErrorReading;
import ch.ethz.coss.nervousnet.lib.LibConstants;
import ch.ethz.coss.nervousnet.lib.SensorReading;

public class BatteryFragment extends BaseFragment {

    private static final String LOG_TAG = BatteryFragment.class.getSimpleName();

    public BatteryFragment() {
        super(LibConstants.SENSOR_BATTERY);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_battery, container, false);
    }


    /*
     * (non-Javadoc)
     *
     * @see
     * ch.ethz.coss.nervousnet.sample.BaseFragment#updateReadings(ch.ethz.coss.
     * nervousnet.vm.SensorReading)
     */
    @Override
    public void updateReadings(SensorReading reading) {
        //NNLog.d(LOG_TAG, "Inside updateReadings");
        if (reading instanceof ErrorReading) {

            //NNLog.d(LOG_TAG, "Inside updateReadings - ErrorReading");
            handleError((ErrorReading) reading);
        } else {
            sensorStatusTV.setText(R.string.sensor_status_connected);

            TextView percent = (TextView) getActivity().findViewById(R.id.battery_percent);
            percent.setText("" + ((BatteryReading) reading).getPercent() * 100 + " %");

            TextView isCharging = (TextView) getActivity().findViewById(R.id.battery_isCharging);
            isCharging.setText((((BatteryReading) reading).isCharging()) ? "YES" : "NO");

            TextView USB_Charging = (TextView) getActivity().findViewById(R.id.battery_isUSB);
            USB_Charging.setText(((BatteryReading) reading).getCharging_type() == 1 ? getActivity().getString(R.string.yes) : getActivity().getString(R.string.no));

            TextView AC_charging = (TextView) getActivity().findViewById(R.id.battery_isAC);
            AC_charging.setText(((BatteryReading) reading).getCharging_type() == 2 ? getActivity().getString(R.string.yes) : getActivity().getString(R.string.no));


        }
    }

    @Override
    public void handleError(ErrorReading reading) {
        //NNLog.d(LOG_TAG, "handleError called");
        sensorStatusTV.setText(reading.getErrorString());
    }


}
