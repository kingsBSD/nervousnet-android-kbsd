package ch.ethz.coss.nervousnet.extensions.lightmeter;

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

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ch.ethz.coss.nervousnet.lib.ErrorReading;
import ch.ethz.coss.nervousnet.lib.LibConstants;
import ch.ethz.coss.nervousnet.lib.LightReading;
import ch.ethz.coss.nervousnet.lib.NervousnetSensorDataListener;
import ch.ethz.coss.nervousnet.lib.NervousnetServiceConnectionListener;
import ch.ethz.coss.nervousnet.lib.NervousnetServiceController;
import ch.ethz.coss.nervousnet.lib.RemoteCallback;
import ch.ethz.coss.nervousnet.lib.SensorReading;


public class LightmeterActivity extends Activity implements NervousnetServiceConnectionListener, NervousnetSensorDataListener {

    int m_interval = 100; // 100 milliseconds by default, can be changed later
    Handler m_handler = new Handler();
    Runnable m_statusChecker;

    NervousnetServiceController nervousnetServiceController;
    TextView lux, errorView;
    LinearLayout reading, error;

    SensorReading lReading;

//    Callback cb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("LightmeterActivity", "onCreate()");
        setContentView(R.layout.activity_lightmeter);


        Button aboutButton = (Button) findViewById(R.id.about_button);
        aboutButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(LightmeterActivity.this, AboutActivity.class);
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
        lux = (TextView) findViewById(R.id.lux);
        errorView = (TextView) findViewById(R.id.error_tv);


        nervousnetServiceController = new NervousnetServiceController(LightmeterActivity.this, this);
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
        Log.d("LightmeterActivity", "update()");
        SensorReading lReading;

        if(nervousnetServiceController != null) {
            lReading  = nervousnetServiceController.getLatestReading(LibConstants.SENSOR_LIGHT);
            if (lReading != null) {
                if (lReading instanceof LightReading) {
                    Log.d("LightmeterActivity", "LightReading found");
                    lux.setText("" + ((LightReading) lReading).getLuxValue());
                    reading.setVisibility(View.VISIBLE);
                    error.setVisibility(View.INVISIBLE);
                } else if (lReading instanceof ErrorReading) {
                    Log.d("LightmeterActivity", "ErrorReading found");
                    lux.setText("Error Code: " + ((ErrorReading) lReading).getErrorCode() + ", " + ((ErrorReading) lReading).getErrorString());
                    reading.setVisibility(View.VISIBLE);
                    error.setVisibility(View.INVISIBLE);

                }
            } else {
                lux.setText("Light object is null");
                reading.setVisibility(View.INVISIBLE);
                error.setVisibility(View.VISIBLE);
            }
        }

    }

    void startRepeatingTask() {
        m_statusChecker = new Runnable() {
            @Override
            public void run() {

                Log.d("LightmeterActivity", "before updating");

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
        Log.d("LightmeterActivity", "onServiceConnected");
        startRepeatingTask();
//        cb = new Callback(){
//            @Override
//            public IBinder success() {
//                return super.asBinder();
//            }
//        };
//
//        if (nervousnetServiceController != null) {
//            try {
//
//                Log.d("LightmeterActivity", "Before requesting getReadings callback : Timestamp = "+(System.currentTimeMillis() - 20000 )+ "<<>>"+System.currentTimeMillis());
//                nervousnetServiceController.getReadings(LibConstants.SENSOR_LIGHT, System.currentTimeMillis() - 1000000, System.currentTimeMillis(), cb);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    }

    @Override
    public void onServiceDisconnected() {
        Log.d("LightmeterActivity", "onServiceDisconnected");
        lux.setText("Nervousnet HUB application is required running to use this app. If already installed, please turn on the Data Collection option inside the Nervousnet HUB application.");
        reading.setVisibility(View.VISIBLE);
        error.setVisibility(View.INVISIBLE);

        stopRepeatingTask();
    }

    @Override
    public void onServiceConnectionFailed(ErrorReading errorReading) {

        Log.d("LightmeterActivity", "onServiceConnectionFailed");
        errorView.setText("Service Connection Failed" +"\n"
        +""+errorReading.getErrorCode()+" - "+errorReading.getErrorString());
        reading.setVisibility(View.INVISIBLE);
        error.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSensorDataReady(SensorReading reading) {
        this.lReading = reading;
    }


//    class Callback extends RemoteCallback.Stub {
//
//        @Override
//        public void success(final List<SensorReading> list) throws RemoteException {
//            runOnUiThread(new Runnable() {
//                public void run() {
//                    Log.d("LightmeterActivity", "callback success "+list.size());
////                                successImpl(result);
//
//
//                        Iterator<SensorReading> iterator;
//                        iterator = list.iterator();
//                        while (iterator.hasNext()) {
//                            SensorReading reading = iterator.next();
//
//                            Log.d("LightmeterActivity", "Light Reading found - " + ((LightReading) reading).getLuxValue());
//                        }
//
//                }
//            });
//        }
//
//        @Override
//        public void failure(final ErrorReading reading) throws RemoteException {
//            runOnUiThread(new Runnable() {
//                public void run() {
//                    Log.d("LightmeterActivity", "callback failure "+reading.getErrorString());
//                }
//            });
//        }
////
////        @Override
////        public IBinder asBinder() {
////            return super.asBinder();
////        }
//    }

}


