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
    public void updateReadings(SensorReading reading) {
        NNLog.d("TrafficFragment", "Inside updateReadings ");

        if (reading instanceof ErrorReading) {

            NNLog.d("TrafficFragment", "Inside updateReadings - ErrorReading");
            handleError((ErrorReading) reading);
        } else {

            sensorStatusTV.setText(R.string.sensor_status_connected);

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
