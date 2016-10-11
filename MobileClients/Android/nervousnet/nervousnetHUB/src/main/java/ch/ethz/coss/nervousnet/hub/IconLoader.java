package ch.ethz.coss.nervousnet.hub;

/**
 * Created by grg on 11/10/16.
 */

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

// https://developer.android.com/training/displaying-bitmaps/index.html

public class IconLoader {

    private Context ctx;
    private LruCache<String, Bitmap> bitmapCache;

    public IconLoader(Context context) {
        ctx = context;

        final int cacheSize = (int) (Runtime.getRuntime().maxMemory() / 1024) / 16;

        bitmapCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };

    }

    public android.graphics.Bitmap getIcon(int id, int width, int height, ImageView target) {

        final String key = String.valueOf(id) + String.valueOf(width) + String.valueOf(height);

        final Bitmap bitmap = getBitmapFromMemCache(key);

        if (bitmap != null) {
            target.setImageBitmap(bitmap);

        } else {
            BitmapWorkerTask task = new BitmapWorkerTask(key,target);
            task.execute(id, width, height);
        }

        return bitmap;
    }

    class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {

        private final WeakReference<ImageView> targetRef;
        private final String bitmapKey;

        public BitmapWorkerTask(String key, ImageView target) {
            bitmapKey = key;
            targetRef = new WeakReference<ImageView>(target);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Integer... params) {
            final Bitmap bitmap = decodeSampledBitmapFromResource(ctx.getResources(), params[0], params[1], params[2]);
            addBitmapToMemoryCache(bitmapKey, bitmap);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (targetRef != null && bitmap != null) {
                final ImageView imageView = targetRef.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }

    }

    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            bitmapCache.put(key, bitmap);
        }
    }

    private Bitmap getBitmapFromMemCache(String key) {
        return bitmapCache.get(key);
    }

    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }


}
