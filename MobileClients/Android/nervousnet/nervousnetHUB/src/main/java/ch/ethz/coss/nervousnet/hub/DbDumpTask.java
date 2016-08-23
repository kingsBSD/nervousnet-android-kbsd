package ch.ethz.coss.nervousnet.hub;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.nio.channels.FileChannel;

import ch.ethz.coss.nervousnet.vm.storage.SQLHelper;

/**
 * Created by grg on 23/08/16.
 */
public class DbDumpTask extends AsyncTask<Integer, Integer, Integer> {

    private static final String DB_NAME = "NN-DB";
    private Context context;
    private SQLHelper helper;

    public DbDumpTask(Context ctx) {
        context = ctx;
        helper = new SQLHelper(context, DB_NAME);
    }

    @Override
    protected Integer doInBackground(Integer... params) {

        File source = Environment.getDataDirectory();
        File dest = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date rightNow = new Date();
        String exportPath = "NervousNet-" + df.format(rightNow) + ".sqlite";
        File exportFile = new File(dest, exportPath);

        File dbFile = context.getDatabasePath(DB_NAME);

        FileChannel sourceStream = null, destStream = null;

        try {
            sourceStream = new FileInputStream(dbFile).getChannel();
            destStream = new FileOutputStream(exportFile).getChannel();
            destStream.transferFrom(sourceStream, 0, sourceStream.size());
            publishProgress(0);
        } catch (IOException e) {
            e.printStackTrace();
            publishProgress(1);
        } finally { // We don't want to be leaky
            closeResourceGracefully(sourceStream);
            closeResourceGracefully(destStream);
        }

        // http://stackoverflow.com/questions/13737261/nexus-4-not-showing-files-via-mtp
        MediaScannerConnection.scanFile(context, new String[]{exportFile.getAbsolutePath()}, null, null);
        
        return 0;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        switch (values[0]) {
            case 0:
                Toast.makeText(context, "Data Exported to Downloads.", Toast.LENGTH_LONG).show();
                break;
            case 1:
                Toast.makeText(context, "Couldn't Export Data!", Toast.LENGTH_LONG).show();
        }
    }

    private static void closeResourceGracefully(Closeable closeMe) {
        if (closeMe != null) {
            try {
                closeMe.close();
            } catch (IOException e) {
                // It was already closed, ignore.
            }
        }
    }

}
