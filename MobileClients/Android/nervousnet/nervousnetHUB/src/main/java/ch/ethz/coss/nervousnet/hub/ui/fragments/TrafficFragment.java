package ch.ethz.coss.nervousnet.hub.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import ch.ethz.coss.nervousnet.hub.Application;
import ch.ethz.coss.nervousnet.hub.R;
import ch.ethz.coss.nervousnet.lib.ErrorReading;
import ch.ethz.coss.nervousnet.lib.LibConstants;
import ch.ethz.coss.nervousnet.lib.SensorReading;
import ch.ethz.coss.nervousnet.lib.TrafficReading;
import ch.ethz.coss.nervousnet.vm.NNLog;
import ch.ethz.coss.nervousnet.vm.NervousnetVMConstants;

/**
 * Created by grg on 23/08/16.
 */
public class TrafficFragment extends BaseFragment {

    public TrafficFragment() {
        super(LibConstants.SENSOR_TRAFFIC);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_traffic, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sensorStatusTV = (TextView) getView().findViewById(R.id.sensorStatus);

        radioGroup = (RadioGroup) getView().findViewById(R.id.radioRateSensor);
        lastCollectionRate = ((Application) (getActivity().getApplication())).nn_VM.getSensorState(LibConstants.SENSOR_TRAFFIC);

        ((RadioButton) radioGroup.getChildAt(lastCollectionRate)).setChecked(true);


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.radioOff:
                        if (lastCollectionRate > NervousnetVMConstants.SENSOR_STATE_AVAILABLE_BUT_OFF){
                            ((Application) (getActivity().getApplication())).nn_VM.updateSensorConfig(LibConstants.SENSOR_TRAFFIC,NervousnetVMConstants.SENSOR_STATE_AVAILABLE_BUT_OFF);
                        }
                        break;
                    case R.id.radioLow:
                        if (lastCollectionRate >= NervousnetVMConstants.SENSOR_STATE_AVAILABLE_BUT_OFF){
                            ((Application) (getActivity().getApplication())).nn_VM.updateSensorConfig(LibConstants.SENSOR_TRAFFIC,NervousnetVMConstants.SENSOR_STATE_AVAILABLE_DELAY_LOW);
                        }
                        break;
                    case R.id.radioMed:
                        if (lastCollectionRate >= NervousnetVMConstants.SENSOR_STATE_AVAILABLE_BUT_OFF){
                            ((Application) (getActivity().getApplication())).nn_VM.updateSensorConfig(LibConstants.SENSOR_TRAFFIC,NervousnetVMConstants.SENSOR_STATE_AVAILABLE_DELAY_MED);
                        }
                        break;
                    case R.id.radioHigh:
                        if (lastCollectionRate >= NervousnetVMConstants.SENSOR_STATE_AVAILABLE_BUT_OFF){
                            ((Application) (getActivity().getApplication())).nn_VM.updateSensorConfig(LibConstants.SENSOR_TRAFFIC,NervousnetVMConstants.SENSOR_STATE_AVAILABLE_DELAY_HIGH);
                        }
                        break;
                }
            }
        });

        if ((((Application) (getActivity().getApplication())).nn_VM.getState() == NervousnetVMConstants.STATE_PAUSED)) {

            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                ((RadioButton) radioGroup.getChildAt(i)).setEnabled(false);
            }
            sensorStatusTV.setText(R.string.local_service_paused);
        }
    }

    @Override
    public void updateReadings(SensorReading reading) {
        NNLog.d("TrafficFragment", "Inside updateReadings ");

        if (reading instanceof ErrorReading) {

            NNLog.d("TrafficFragment", "Inside updateReadings - ErrorReading");
            handleError((ErrorReading) reading);
        } else {
            TextView appName = (TextView) getActivity().findViewById(R.id.app_name);
            appName.setText(((TrafficReading) reading).getAppName());
            TextView txBytes = (TextView) getActivity().findViewById(R.id.tx_bytes);
            txBytes.setText((Long.toString(((TrafficReading) reading).getTxBytes())));
            TextView rxBytes = (TextView) getActivity().findViewById(R.id.rx_bytes);
            rxBytes.setText((Long.toString(((TrafficReading) reading).getRxBytes())));
        }

    }

    @Override
    public void handleError(ErrorReading reading) {
        NNLog.d("TrafficFragment", "handleError called");
        sensorStatusTV.setText(reading.getErrorString());
    }

}
