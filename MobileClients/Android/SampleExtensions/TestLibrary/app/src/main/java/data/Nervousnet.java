package data;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;


import java.util.ArrayList;
import java.util.List;

import ch.ethz.coss.nervousnet.aggregation.Aggregation;
import ch.ethz.coss.nervousnet.lib.ErrorReading;
import ch.ethz.coss.nervousnet.lib.NervousnetSensorDataListener;
import ch.ethz.coss.nervousnet.lib.NervousnetServiceConnectionListener;
import ch.ethz.coss.nervousnet.lib.NervousnetServiceController;
import ch.ethz.coss.nervousnet.lib.RemoteCallback;
import ch.ethz.coss.nervousnet.lib.SensorReading;

/**
 * Created by ales on 28/06/16.
 *
 * Nervousnet queries the data in Nervousnet application. This class is source of the data.
 */
public class Nervousnet implements NervousnetServiceConnectionListener, NervousnetSensorDataListener {

    // We need context to get connections and sensor data
    private Context context;

    // Connection to the service
    NervousnetServiceController nervousnetServiceController;

    // Constructor
    public Nervousnet(Context context){
        this.context = context;
    }

    // Connect
    public void connect(){
        Log.d("NERVOUSNET", "Connecting ...");
        nervousnetServiceController = new NervousnetServiceController(this.context, this);
        Log.d("NERVOUSNET", "Connecting2 ..." + nervousnetServiceController);
        try {
            nervousnetServiceController.connect();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void testReading() throws RemoteException {
        Callback cb = new Callback();
        SensorReading reading1 = nervousnetServiceController.getLatestReading(0);
        Log.d("TEST READING", "" + reading1);
        reading1 = nervousnetServiceController.getLatestReading(1);
        Log.d("TEST READING", "" + reading1);
        reading1 = nervousnetServiceController.getLatestReading(2);
        Log.d("TEST READING", "" + reading1);
        // Gyroscope doesn't work in nervousnet, so we skip it
        //reading1 = nervousnetServiceController.getLatestReading(3);
        //Log.d("TEST READING", "" + reading1);
        reading1 = nervousnetServiceController.getLatestReading(4);
        Log.d("TEST READING", "" + reading1);
        reading1 = nervousnetServiceController.getLatestReading(5);
        Log.d("TEST READING", "" + reading1);

    }


    public void testReadings() throws RemoteException {
        Callback cb = new Callback();
        nervousnetServiceController.getReadings(0, System.currentTimeMillis() - 80000, System.currentTimeMillis(), cb);
        Log.d("Nervousnet", "testReadings acc size " + cb.getList().size());

        //cb = new Callback();
        //nervousnetServiceController.getReadings(5, System.currentTimeMillis() - 80000, System.currentTimeMillis(), cb);
        //Log.d("Nervousnet", "testReadings noise size " + cb.getList().size());

    }



    public void testAverage() throws RemoteException {
        // TEST
        List list;
        list = nervousnetServiceController.getAverage(0, System.currentTimeMillis(), System.currentTimeMillis() - 86400000);
        Log.d("NERVOSNET-TEST", "Average acc size: " + list.size());

        for (Object o : list)
            Log.d("NERVOSNET-TEST", "Average acc " + o);

        // For others RANGE doesn't work

        /*list = nervousnetServiceController.getAverage(1);
        for (Object o : list)
            Log.d("NERVOSNET-TEST", "Average bat " + o);*/

        //list = nervousnetServiceController.getAverage(2);
        //for (Object o : list)
        //    Log.d("NERVOSNET-TEST", "Average gyro " + o);

        /*list = nervousnetServiceController.getAverage(5);
        for (Object o : list)
            Log.d("NERVOSNET-TEST", "Average noise " + o);*/

        /*list = nervousnetServiceController.getAverage(4);
        for (Object o : list)
            Log.d("NERVOSNET-TEST", "Average light " + o);*/

    }


    public void testQueryNumVectorValue() throws RemoteException {
        Log.d("TEST", "Start ...");
        Callback cb = new Callback();
        nervousnetServiceController.getReadings(0, System.currentTimeMillis() - 80000, System.currentTimeMillis(), cb);
        List list = cb.getList();
        Log.d("TEST", "List size " + list.size());
        Aggregation aggr = new Aggregation((ArrayList<SensorReading>) list);
        Log.d("TEST", "Test average " + aggr.getAverage());
        Log.d("TEST", "Test median " + aggr.getMedian());
        Log.d("TEST", "Test maxValue " + aggr.getMaxValue());
        Log.d("TEST", "Test minValue " + aggr.getMinValue());
        Log.d("TEST", "Test var " + aggr.var());
        Log.d("TEST", "Test sd " + aggr.sd());
        Log.d("TEST", "Test largest " + aggr.getLargest(7));
        Log.d("TEST", "Test largest rank " + aggr.getRankLargest(7));
        Log.d("TEST", "Test smallest " + aggr.getSmallest(9));
        Log.d("TEST", "Test smallest rank " + aggr.getRankSmallest(9));
        Log.d("TEST", "Test largest " + aggr.getLargest(7));
        Log.d("TEST", "Test sum " + aggr.getSum());
        Log.d("TEST", "Test sum^2 " + aggr.getSumSquare());
        Log.d("TEST", "Test rms " + aggr.getRms());
        Log.d("TEST", "Test largest " + aggr.getLargest(7));
        Log.d("TEST", "Test mean square " + aggr.getMeanSquare());
        Log.d("TEST", "Test entropy " + aggr.getEntropy());


    }


    public void test() throws RemoteException {
        //testReading();
        //testReadings();
        //testAverage();
        testQueryNumVectorValue();

    }

    /////////////////////////////////////////////////////////////////////////
    // CALLBACK
    /////////////////////////////////////////////////////////////////////////

    class Callback extends RemoteCallback.Stub {
        private List list;

        public Callback(){
        }

        @Override
        public void success(final List<SensorReading> list) throws RemoteException {
            //Log.d("NERVOUSNET CALLBACK", sType + " callback success " + list.size());
            this.list = list;
        }

        @Override
        public void failure(final ErrorReading reading) throws RemoteException {
            //Log.d("NERVOUSNET CALLBACK", sType + "callback failure "+reading.getErrorString());
        }

        public List getList() { return this.list; }
    }


    /////////////////////////////////////////////////////////////////////////
    // OVERWRITE
    /////////////////////////////////////////////////////////////////////////

    @Override
    public void onSensorDataReady(SensorReading reading) {

    }

    @Override
    public void onServiceConnected() {

    }

    @Override
    public void onServiceDisconnected() {

    }

    @Override
    public void onServiceConnectionFailed(ErrorReading errorReading) {

    }

}
