package ethz.ch.client;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.jjoe64.graphview.GraphView;

import data.Nervousnet;

public class Client extends Activity {

    TextView sendResponse, textNervousnet;
    Button buttonReconnect, buttonNervousnet;
    OnClickListener buttonNervousnetOnClickListener, buttonNervousnetReconnectListener;
    Nervousnet nervousnet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Don't allow rotation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        // Create front-end view
        setContentView(R.layout.activity_client);

        // 1. Initialize buttons
        buttonNervousnet = (Button)findViewById(R.id.nervousnet);
        initButtonNervousnet(this);

        buttonReconnect = (Button)findViewById(R.id.buttonReconnect);
        initButtonReconnect(this);

        // 2. Initialize text
        sendResponse = (TextView)findViewById(R.id.sendResponse);
        textNervousnet = (TextView)findViewById(R.id.textNervousnet);

        // 4. Initialize data source
        nervousnet = new Nervousnet(this);
        nervousnet.connect();


        Thread thread = new Thread() {
            @Override
            public void run() {
                    try {
                        sleep(1000);
                        nervousnet.test();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }


            }
        };

        thread.start();

    }



    protected void initButtonNervousnet(final Context context) {
        // Create button connector
        this.buttonNervousnetOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View arg0) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                try {
                    nervousnet.test();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                }
            };

            thread.start();
            }
        };

        buttonNervousnet.setOnClickListener(buttonNervousnetOnClickListener);
    }

    protected void initButtonReconnect(final Context context) {
        // Create button connector
        this.buttonNervousnetReconnectListener = new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                nervousnet = new Nervousnet(context);
                nervousnet.connect();

            }
        };
        buttonReconnect.setOnClickListener(buttonNervousnetReconnectListener);
    }
}
