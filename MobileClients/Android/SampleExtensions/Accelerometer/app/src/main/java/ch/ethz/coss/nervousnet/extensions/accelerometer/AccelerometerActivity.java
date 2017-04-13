package ch.ethz.coss.nervousnet.extensions.accelerometer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.coss.nervousnet.lib.InfoReading;
import ch.ethz.coss.nervousnet.lib.InfoReading;
import ch.ethz.coss.nervousnet.lib.LibConstants;
import ch.ethz.coss.nervousnet.lib.SensorReading;
import ch.ethz.coss.nervousnet.lib.NervousnetSensorDataListener;
import ch.ethz.coss.nervousnet.lib.NervousnetServiceConnectionListener;
import ch.ethz.coss.nervousnet.lib.NervousnetServiceController;
import ch.ethz.coss.nervousnet.lib.RemoteCallback;
import ch.ethz.coss.nervousnet.lib.SensorReading;


public class AccelerometerActivity extends Activity implements NervousnetServiceConnectionListener, NervousnetSensorDataListener {

    int m_interval = 100; // 100 milliseconds by default, can be changed later
    Handler m_handler = new Handler();
    Runnable m_statusChecker;

    NervousnetServiceController nervousnetServiceController;
    TextView x_val, y_val, z_val, errorView;
    LinearLayout reading, error;

    SensorReading aReading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_accelerometer);


        Button aboutButton = (Button) findViewById(R.id.about_button);
        aboutButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(AccelerometerActivity.this, AboutActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        Button startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                startActivity(getPackageManager().getLaunchIntentForPackage("ch.ethz.coss.nervousnet.hub"));
                System.exit(0);
            }
        });


        reading = (LinearLayout) findViewById(R.id.reading);
        error = (LinearLayout) findViewById(R.id.error);
        x_val = (TextView) findViewById(R.id.accel_x);
        y_val = (TextView) findViewById(R.id.accel_y);
        z_val = (TextView) findViewById(R.id.accel_z);
        errorView = (TextView) findViewById(R.id.error_tv);

        nervousnetServiceController = new NervousnetServiceController(AccelerometerActivity.this, this);
        nervousnetServiceController.connect();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }


    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        nervousnetServiceController.disconnect();

    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        nervousnetServiceController.connect();

    }


    @Override
    public void onBackPressed() {
        nervousnetServiceController.disconnect();
        finish();
        System.exit(0);
    }


    protected void update() throws RemoteException {
        Log.d("AccelerometerActivity", "before updating 1");

        if(nervousnetServiceController != null) {
            aReading  = nervousnetServiceController.getLatestReading(LibConstants.SENSOR_ACCELEROMETER);
            if (aReading != null) {
                if (aReading instanceof SensorReading) {
                    Log.d("AccelerometerActivity", "Accelreading found");
                    ArrayList values = aReading.getValues();

                    x_val.setText("" + values.get(0));
                    y_val.setText("" + values.get(1));
                    z_val.setText("" + values.get(2));
                    reading.setVisibility(View.VISIBLE);
                    error.setVisibility(View.INVISIBLE);

                    aReading.setSensorID(10001);
                    aReading.setValue(aReading.getParametersNames().get(0), ((float)aReading.getValues().get(0))+ 10);
                    Log.d("AccelerometerActivity", "Accelreading before write ");
                    InfoReading returnReading = nervousnetServiceController.writeReading(aReading);
                    Log.d("AccelerometerActivity", "Accelreading after write "+returnReading.getInfoCode());
                } else if (aReading instanceof InfoReading) {
                    Log.d("AccelerometerActivity", "InfoReading found");
                    errorView.setText("InfoReading Code: " + ((InfoReading) aReading).getInfoCode() + ", " + ((InfoReading) aReading).getInfoString());
                    reading.setVisibility(View.INVISIBLE);
                    error.setVisibility(View.VISIBLE);
                }
            } else {
                errorView.setText("Accelerometer object is null");
                reading.setVisibility(View.INVISIBLE);
                error.setVisibility(View.VISIBLE);
            }
        }

    }

    void startRepeatingTask() {
        m_statusChecker = new Runnable() {
            @Override
            public void run() {

                Log.d("AccelerometerActivity", "before updating");

                try {
                    update();
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                m_handler.postDelayed(m_statusChecker, m_interval);
            }
        };

        m_statusChecker.run();
    }

    void stopRepeatingTask() {
        m_handler.removeCallbacks(m_statusChecker);
    }

    @Override
    public void onServiceConnected() {
        startRepeatingTask();

    }

    @Override
    public void onServiceDisconnected() {

        errorView.setText("Nervousnet HUB application is required running to use this app. If already installed, please turn on the Data Collection option inside the Nervousnet HUB application.");
        reading.setVisibility(View.INVISIBLE);
        error.setVisibility(View.VISIBLE);

        stopRepeatingTask();
    }

    @Override
    public void onServiceConnectionFailed(InfoReading errorReading) {

        Log.d("AccelerometerActivity", "onServiceConnectionFailed");
        errorView.setText("Service Connection Failed" +"\n"
                +""+errorReading.getInfoCode()+" - "+errorReading.getInfoString());
        reading.setVisibility(View.INVISIBLE);
        error.setVisibility(View.VISIBLE);
    }


    @Override
    public void onSensorDataReady(SensorReading reading) {
        this.aReading = reading;
    }


    class Callback extends RemoteCallback.Stub {

        @Override
        public void success(List list) throws RemoteException {

        }

        @Override
        public void failure(InfoReading reading) throws RemoteException {

        }

        @Override
        public IBinder asBinder() {
            return null;
        }
    }


}
