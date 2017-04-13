package ch.ethz.coss.nervousnet.lib;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

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
 * *     along with NervousNet. If not, see http://www.gnu.org/licenses/.
 * *
 * *
 * * 	Contributors:
 * * 	Prasad Pulikal - prasad.pulikal@gess.ethz.ch  -  Initial API and implementation
 *******************************************************************************/
public class NervousnetServiceController {

    private static String LOG_TAG = NervousnetServiceController.class.getSimpleName();
    public NervousnetRemote mService;
    NervousnetServiceConnectionListener listener;
    Context context;
    private ServiceConnection mServiceConnection;
    private Boolean bindFlag;

    public NervousnetServiceController(Context context, NervousnetServiceConnectionListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void connect() {

        if (isAppInstalled("ch.ethz.coss.nervousnet.hub") || isAppInstalled("ch.ethz.coss.nervousnet.hub.debug")) {

            if (mServiceConnection == null) {
                initConnection();
            }

            if (mService == null) {
                try {
                    doBindService();
                    Log.d(LOG_TAG, "bindflag : " + bindFlag.toString());
                    if (!bindFlag) {
//                    Utils.displayAlert(context, "Alert",
//                            "Nervousnet HUB application is required running to use this app. Please turn on the Data Collection option inside the Nervousnet HUB application. ",
//                            "Launch Nervousnet", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    context.startActivity(context.getPackageManager().getLaunchIntentForPackage("ch.ethz.coss.nervousnet.hub"));
//
//                                }
//                            }, "Exit", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    System.exit(0);
//                                }
//                            });

                        listener.onServiceConnectionFailed(Utils.getInfoReading(101));

                    }
                } catch (SecurityException e) {
                    e.printStackTrace();
                    Log.e(LOG_TAG, "SecurityException - cannot bind to nervousnet service due to missing permission or permission denied. use 'ch.ethz.coss.nervousnet.hub.BIND_PERM' in your manifest to connect to nervousnet HUB Service");
                    doUnbindService();

                    listener.onServiceConnectionFailed(Utils.getInfoReading(102));
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(LOG_TAG, "Exception - not able to bind ! ");
                    doUnbindService();

                    listener.onServiceConnectionFailed(Utils.getInfoReading(103));
                }


            }

        } else {
            Utils.displayAlert(context, "Alert",
                    "Nervousnet HUB application is required to be installed for running this app. If not installed please download it from the App Store.",
                    "Download Now", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            try {
                                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=ch.ethz.coss.nervousnet.hub")));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=ch.ethz.coss.nervousnet.hub")));
                            }

                        }
                    }, "Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            System.exit(0);
                        }
                    });
        }
    }


    public void disconnect() {
        doUnbindService();
    }


    private void initConnection() {
        Log.d(LOG_TAG, "Inside initConnection");
        mServiceConnection = new ServiceConnection() {

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(LOG_TAG, "Inside onServiceDisconnected 2");

                mService = null;
                mServiceConnection = null;
                Toast.makeText(context, "NervousnetRemote Service not connected", Toast.LENGTH_SHORT)
                        .show();
                Log.d("NervousnetServiceUtil", "Binding - Service disconnected");
                listener.onServiceDisconnected();
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d("NervousnetServiceUtil", "onServiceConnected");

                mService = NervousnetRemote.Stub.asInterface(service);

                try {
                    if (mService != null) {
                        if (mService.getNervousnetHubStatus()) {
                            Toast.makeText(context, "Nervousnet Remote Service Connected", Toast.LENGTH_SHORT)
                                    .show();
                            Log.d(LOG_TAG, "Binding is done - Service connected");
                            if (listener != null)
                                listener.onServiceConnected();
                        } else {
                            if (listener != null)
                                listener.onServiceConnectionFailed(Utils.getInfoReading(101));
                        }
                    }

                } catch (RemoteException e) {
                    if (listener != null)
                        listener.onServiceConnectionFailed(Utils.getInfoReading(104));

                    e.printStackTrace();
                }


            }
        };

    }


    private void doBindService() {
        Log.d(LOG_TAG, "doBindService successfull");
        Intent it = new Intent();
        it.setClassName("ch.ethz.coss.nervousnet.hub", "ch.ethz.coss.nervousnet.hub.NervousnetHubApiService");
        bindFlag = context.bindService(it, mServiceConnection, 0);

        if (!bindFlag && listener != null) {
            listener.onServiceConnectionFailed(Utils.getInfoReading(103));
        }

    }

    private void doUnbindService() {
        Log.d(LOG_TAG, "doUnbindService successfull");
        if (mServiceConnection != null)
            context.unbindService(mServiceConnection);

        bindFlag = false;
        mService = null;
        mServiceConnection = null;
        Log.d(LOG_TAG, "doUnbindService successfull");
    }


    public SensorReading getLatestReading(long sensorID) throws RemoteException {
        if (bindFlag) {
            if (mService != null)
                return mService.getLatestReading(sensorID);
            else
               return new InfoReading(new String[]{"002", "Service not connected."});
        } else
            return new InfoReading(new String[]{"003", "Service not bound."});


    }

    public InfoReading writeReading(SensorReading reading) throws RemoteException {
        if (bindFlag) {
            if (mService != null) {
                return mService.writeReading(reading);
            }

            else
                return new InfoReading(new String[]{"002", "Service not connected."});
        } else
            return new InfoReading(new String[]{"003", "Service not bound."});


    }


    /*public SensorReading getReadings(long sensorID, long startTime, long endTime, RemoteCallback cb) throws RemoteException {
        if (bindFlag) {
            if (mService != null) {

                mService.getReadings(sensorID, startTime, endTime, cb);
                return null;
            } else
                return new InfoReading(new String[]{"002", "Nervousnet Service not connected."});
        } else
            return new InfoReading(new String[]{"003", "Nervousnet Service not bound."});


    }*/

    /*public List getAverage(long sensorID, long startTime, long endTime) throws RemoteException {
        Callback cb = new Callback();
        getReadings(sensorID, startTime, endTime, cb);
        List list = cb.getList();
        Aggregation aggr = new Aggregation(list);
        return aggr.getAverage();
    }*/




    private boolean isAppInstalled(String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
    class Callback extends RemoteCallback.Stub {
        private List<SensorReading> list;

        public Callback(){
        }

        @Override
        public void success(final List<SensorReading> list) throws RemoteException {
            Log.d("NERVOUSNET CALLBACK", " callback success " + list.size());
            this.list = list;
        }

        @Override
        public void failure(final InfoReading reading) throws RemoteException {
            //Log.d("NERVOUSNET CALLBACK", sType + "callback failure "+reading.getInfoString());
        }

        public List getList() { return this.list; }
    }
}
