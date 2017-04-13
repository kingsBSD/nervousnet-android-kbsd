/**
 * *     Nervousnet - a distributed middleware software for social sensing.
 * *     It is responsible for collecting and managing data in a fully de-centralised fashion
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

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ch.ethz.coss.nervousnet.hub.Application;
import ch.ethz.coss.nervousnet.hub.R;
import ch.ethz.coss.nervousnet.lib.InfoReading;
import ch.ethz.coss.nervousnet.lib.LibConstants;
import ch.ethz.coss.nervousnet.lib.SensorReading;
import ch.ethz.coss.nervousnet.vm.NNLog;

public class AccelFragment extends BaseFragment {

    private static final String LOG_TAG = AccelFragment.class.getSimpleName();

    public AccelFragment() {
        super(LibConstants.SENSOR_ACCELEROMETER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_accel, container, false);

//        sensorSwitch.setChecked(((((Application) ((Activity) getContext()).getApplication()).nn_VM.getSensorState(LibConstants.SENSOR_ACCELEROMETER)) == 1) ? true : false);
//
//        sensorSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked)
//                    ((Application) ((Activity) getContext()).getApplication()).nn_VM.startSensor(LibConstants.SENSOR_ACCELEROMETER);
//                else {
//                    ((Application) ((Activity) getContext()).getApplication()).nn_VM.stopSensor(LibConstants.SENSOR_ACCELEROMETER, true);
//
//                }
//
//            }
//        });
//        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sensorStatusTV = (TextView) getView().findViewById(R.id.sensorStatus);
        radioGroup = (RadioGroup) getView().findViewById(R.id.radioRateSensor);
        lastCollectionRate = ((((Application) ((Activity) getContext()).getApplication()).nn_VM.getSensorState(LibConstants.SENSOR_ACCELEROMETER)));

        ((RadioButton) radioGroup.getChildAt(lastCollectionRate)).setChecked(true);


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                NNLog.d(LOG_TAG, "Inside radioGroup onCheckedChanged ");

                byte state;
                switch (checkedId) {
                    case R.id.radioOff:
                        state = NervousnetVMConstants.SENSOR_STATE_AVAILABLE_BUT_OFF; break;
                    case R.id.radioLow:
                        state = NervousnetVMConstants.SENSOR_STATE_AVAILABLE_DELAY_LOW; break;
                    case R.id.radioMed:
                        state = NervousnetVMConstants.SENSOR_STATE_AVAILABLE_DELAY_MED; break;
                    case R.id.radioHigh:
                        state = NervousnetVMConstants.SENSOR_STATE_AVAILABLE_DELAY_HIGH; break;
                    default: state = -1;
                }
                if (lastCollectionRate >= NervousnetVMConstants.SENSOR_STATE_AVAILABLE_BUT_OFF
                        && state >= 0) {
                    ((Application) ((Activity) getContext()).getApplication()).nn_VM.updateSensorState(LibConstants.SENSOR_ACCELEROMETER, state);
                }
            }
        });

        if ((((Application) ((Activity) getContext()).getApplication()).nn_VM.getNervousnetState() == NervousnetVMConstants.STATE_PAUSED)) {
            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                ((RadioButton) radioGroup.getChildAt(i)).setEnabled(false);
            }
            sensorStatusTV.setText(R.string.local_service_paused);
        }


    }

    @Override
    public void updateReadings(SensorReading reading) {
        NNLog.d(LOG_TAG, "Inside updateReadings ");

        if (reading instanceof InfoReading) {

            NNLog.d(LOG_TAG, "Inside updateReadings - InfoReading");
            handleError((InfoReading) reading);
        } else {

            sensorStatusTV.setText(R.string.sensor_status_connected);

            TextView x_value = (TextView) getView().findViewById(R.id.accel_x);
            TextView y_value = (TextView) getView().findViewById(R.id.accel_y);
            TextView z_value = (TextView) getView().findViewById(R.id.accel_z);

            ArrayList values = reading.getValues();
            x_value.setText("" + values.get(0));
            y_value.setText("" + values.get(1));
            z_value.setText("" + values.get(2));

        }

    }

    @Override
    public void handleError(InfoReading reading) {
        NNLog.d(LOG_TAG, "handleError called");
        sensorStatusTV.setText(reading.getInfoString());
    }

}
