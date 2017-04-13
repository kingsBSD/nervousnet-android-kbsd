package ch.ethz.coss.nervousnet.hub.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ch.ethz.coss.nervousnet.hub.R;
import ch.ethz.coss.nervousnet.lib.ErrorReading;
import ch.ethz.coss.nervousnet.lib.LibConstants;
import ch.ethz.coss.nervousnet.lib.SensorReading;
import ch.ethz.coss.nervousnet.lib.SocketReading;
import ch.ethz.coss.nervousnet.vm.NNLog;

/**
 * Created by grg on 03/09/16.
 */
public class SocketFragment extends BaseFragment {

    public SocketFragment() {
        super(LibConstants.SENSOR_SOCKET);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_socket, container, false);
        return rootView;
    }


    @Override
    public void updateReadings(SensorReading reading) {
        NNLog.d("SocketFragment", "Inside updateReadings ");

        if (reading instanceof ErrorReading) {

            NNLog.d("SocketFragment", "Inside updateReadings - ErrorReading");
            handleError((ErrorReading) reading);
        } else {

            sensorStatusTV.setText(R.string.sensor_status_connected);

            TextView appName = (TextView) getActivity().findViewById(R.id.app_name);
            appName.setText(((SocketReading) reading).getAppName());
            TextView protocol = (TextView) getActivity().findViewById(R.id.protocol);
            protocol.setText(((SocketReading) reading).getProtocol());
            TextView port = (TextView) getActivity().findViewById(R.id.port);
            port.setText((Integer.toString(((SocketReading) reading).getPort())));
        }

    }

    @Override
    public void handleError(ErrorReading reading) {
        NNLog.d("SocketFragment", "handleError called");
        sensorStatusTV.setText(reading.getErrorString());
    }

}
