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

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import ch.ethz.coss.nervousnet.hub.Application;
import ch.ethz.coss.nervousnet.hub.R;
import ch.ethz.coss.nervousnet.lib.ErrorReading;
import ch.ethz.coss.nervousnet.lib.LibConstants;
import ch.ethz.coss.nervousnet.lib.NoiseReading;
import ch.ethz.coss.nervousnet.lib.SensorReading;
import ch.ethz.coss.nervousnet.vm.NNLog;
import ch.ethz.coss.nervousnet.vm.NervousnetVMConstants;

public class NoiseFragment extends BaseFragment {
    final private int REQUEST_CODE_ASK_PERMISSIONS_NOISE = 2;
    private float db;
    private float newDb;


    public NoiseFragment() {
        super(LibConstants.SENSOR_NOISE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_noise, container, false);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sensorStatusTV = (TextView) getView().findViewById(R.id.sensorStatus);

        radioGroup = (RadioGroup) getView().findViewById(R.id.radioRateSensor);
        lastCollectionRate = ((((Application) ((Activity) getContext()).getApplication()).nn_VM.getSensorState(LibConstants.SENSOR_NOISE)));

        ((RadioButton)radioGroup.getChildAt(lastCollectionRate)).setChecked(true);


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch(checkedId){
                    case R.id.radioOff:
                        if(lastCollectionRate > NervousnetVMConstants.SENSOR_STATE_AVAILABLE_BUT_OFF){
                            ((Application) ((Activity) getContext()).getApplication()).nn_VM.updateSensorConfig(LibConstants.SENSOR_NOISE,NervousnetVMConstants.SENSOR_STATE_AVAILABLE_BUT_OFF);
                        }
                        break;
                    case R.id.radioLow:
                        if(lastCollectionRate >= NervousnetVMConstants.SENSOR_STATE_AVAILABLE_BUT_OFF){
                            ((Application) ((Activity) getContext()).getApplication()).nn_VM.updateSensorConfig(LibConstants.SENSOR_NOISE,NervousnetVMConstants.SENSOR_STATE_AVAILABLE_DELAY_LOW);
                        }
                        break;
                    case R.id.radioMed:
                        if(lastCollectionRate >= NervousnetVMConstants.SENSOR_STATE_AVAILABLE_BUT_OFF){
                            ((Application) ((Activity) getContext()).getApplication()).nn_VM.updateSensorConfig(LibConstants.SENSOR_NOISE,NervousnetVMConstants.SENSOR_STATE_AVAILABLE_DELAY_MED);
                        }
                        break;
                    case R.id.radioHigh:
                        if(lastCollectionRate >= NervousnetVMConstants.SENSOR_STATE_AVAILABLE_BUT_OFF){
                            ((Application) ((Activity) getContext()).getApplication()).nn_VM.updateSensorConfig(LibConstants.SENSOR_NOISE,NervousnetVMConstants.SENSOR_STATE_AVAILABLE_DELAY_HIGH);
                        }
                        break;
                }
            }
        });

        if((((Application) ((Activity) getContext()).getApplication()).nn_VM.getState() == NervousnetVMConstants.STATE_PAUSED)) {
            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                ((RadioButton) radioGroup.getChildAt(i)).setEnabled(false);
            }
            sensorStatusTV.setText(R.string.local_service_paused);
        }
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

        if (reading instanceof ErrorReading) {

            NNLog.d("NoiseFragment", "Inside updateReadings - ErrorReading");
            handleError((ErrorReading) reading);
        } else {
            NNLog.d("NoiseFragment", "Inside updateReadings");
            sensorStatusTV.setText(R.string.sensor_status_connected);
            db = ((NoiseReading) reading).getdbValue();
            TextView dbTV = (TextView) getActivity().findViewById(R.id.dbValue);

            if (newDb < Math.round(db))
                newDb++;
            else if (newDb > Math.round(db))
                newDb--;
            else
                newDb = db;

            dbTV.setText("" + Math.round(db));

        }
    }

    @Override
    public void handleError(ErrorReading reading) {
        NNLog.d("NoiseFragment", "handleError called");
        sensorStatusTV.setText(reading.getErrorString());

//        // Android 6.0 permission request
//        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//            if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.RECORD_AUDIO)) {
//                ActivityCompat.requestPermissions(
//                        getActivity(),
//                        new String[]{Manifest.permission.RECORD_AUDIO},
//                        REQUEST_CODE_ASK_PERMISSIONS_NOISE
//                );
//            }
//        }
        return;

    }
}
