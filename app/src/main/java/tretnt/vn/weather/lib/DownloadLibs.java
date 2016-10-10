package tretnt.vn.weather.lib;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.os.AsyncTaskCompat;
import android.util.Log;
import android.widget.ImageView;

import org.json.JSONException;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import tretnt.vn.weather.R;


/**
 * Created by Apple on 9/28/16.
 */
public class DownloadLibs {

    static private Set<String> ALREADY_REQUEST_URL_SET = Collections.synchronizedSet(new HashSet<String>());
    static private final Object SYNC_WORK_OBJ = new Object();
    static public void getImageByUrl(Context context, String url, DownloadCallBack<Bitmap> downloadCallBack){
        Bitmap bitmap = (Bitmap) CacheLib.getInstance().getObjectFromMemCache(url);
        if(bitmap != null){
            downloadCallBack.onDownloadCompleted(bitmap);
        }else {
            ObjDownload obj = new ObjDownload();
            obj.downloadCallBack = downloadCallBack;
            obj.url = url;
            new BitmapWorkerLoader(context, obj).execute();
        }
    }
    static public void getJsonByUrl(Context context, String url, DownloadCallBack<String> downloadCallBack){
        String str = (String) CacheLib.getInstance().getObjectFromMemCache(url);
        if(str != null){
            downloadCallBack.onDownloadCompleted(str);
        }else{
            ObjDownload obj = new ObjDownload();
            obj.downloadCallBack = downloadCallBack;
            obj.url = url;
            new JSONWorkerLoader(context, obj).execute();
        }
    }

    static public JSONWorkerLoader forceRequestJsonByUrl(Context context, String url, DownloadCallBack<String> downloadCallBack){
        CacheLib.getInstance().removeObjectFromMemCache(url);
        ObjDownload obj = new ObjDownload();
        obj.downloadCallBack = downloadCallBack;
        obj.url = url;
        JSONWorkerLoader jsonWorkerLoader = new JSONWorkerLoader(context, obj);
        jsonWorkerLoader.execute();
        return jsonWorkerLoader;
    }

    static public void getImageByUrl(Context context, String url, DownloadCallBack<Bitmap> downloadCallBack, ImageView imageView){
        Bitmap bitmap = (Bitmap) CacheLib.getInstance().getObjectFromMemCache(url);
        if(bitmap != null){
            cancelOldBitmapWorker(imageView, url);
            imageView.setImageBitmap(bitmap);
            if(downloadCallBack != null) {
                downloadCallBack.onDownloadCompleted(bitmap);
            }
            Log.e("", "getImageByUrl mem cache url = "+url);
        }else if(cancelOldBitmapWorker(imageView, url)){
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.progress_animation));
            ObjDownload obj = new ObjDownload();
            obj.downloadCallBack = downloadCallBack;
            obj.url = url;
            obj.imageView = imageView;
            BitmapWorkerLoader bitmapWorkerLoader = new BitmapWorkerLoader(context, obj);
            imageView.setTag(bitmapWorkerLoader);
            bitmapWorkerLoader.execute();
            Log.e("", "getImageByUrl request url = "+url);
        }
    }
    static private boolean cancelOldBitmapWorker(ImageView imageView, String url){
        BitmapWorkerLoader bitmapWorkerLoader = (BitmapWorkerLoader) imageView.getTag();
        if(bitmapWorkerLoader != null) {
            ObjDownload objDownload = bitmapWorkerLoader.getObj();
            if(objDownload.url.equals(url)){
                return false;
            }else{
                bitmapWorkerLoader.cancel();
            }
        }
        imageView.setTag(null);
        return true;
    }

    static public class JSONWorkerLoader {
        private ObjDownload obj;
        private AsyncTask<Void, Void, String> asyncTask;
        private Context context;

        public JSONWorkerLoader(Context context, ObjDownload obj) {
            this.obj = obj;
            this.context = context;
        }

        public void execute() {
            asyncTask = new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    Log.e("", "doInBackground, url = " + obj.url);
                    boolean isContain;
                    isContain = ALREADY_REQUEST_URL_SET.contains(obj.url);

                    if (isContain) {
                        synchronized (SYNC_WORK_OBJ) {
                            try {
                                SYNC_WORK_OBJ.wait();
                            } catch (InterruptedException e) {
                            }
                        }
                    } else {
                        ALREADY_REQUEST_URL_SET.add(obj.url);
                    }

                    Log.e("", "doInBackground, url 1 = " + obj.url);
                    String str = null;
                    if (!isCancelled()) {
                        Object object = CacheLib.getInstance().getObjectFromMemCache(obj.url);
                        if (object != null) {
                            str = (String) object;
                        }
                    }
                    if (str == null && !isCancelled()) {
                        DownloadLibJson downloadLibJson = new DownloadLibJson(obj.url);
                        try {
                            str = downloadLibJson.connectAndParseObj();
                        } catch (IOException | JSONException e) {
                            obj.error = e;
                            return null;
                        }
                    }
                    if (!isCancelled() && str != null) {
                        CacheLib.getInstance().addObjectToMemory(obj.url, str);
                        return str;
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(String str) {
                    super.onPostExecute(str);
                    if (!isCancelled() && obj.downloadCallBack != null) {
                        if(str != null) {
                            obj.downloadCallBack.onDownloadCompleted(str);
                        }else{
                            obj.downloadCallBack.onDownloadError(obj.error != null ? obj.error : new NullPointerException("String null"));
                        }
                    }
                    removeRequest(obj);
                    removeTagImageView(obj);
                    Log.e("", "Finish url = " + obj.url);
                    synchronized (SYNC_WORK_OBJ) {
                        SYNC_WORK_OBJ.notifyAll();
                    }
                }

                @Override
                protected void onCancelled(String str) {
                    super.onCancelled(str);
                    Log.e("", "Cancel url = " + obj.url);
                    synchronized (SYNC_WORK_OBJ) {
                        SYNC_WORK_OBJ.notifyAll();
                    }
                }
            };
//            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            //http://stackoverflow.com/questions/9119627/android-sdk-asynctask-doinbackground-not-running-subclass
            AsyncTaskCompat.executeParallel(asyncTask);
        }

        public void cancel() {
            asyncTask.cancel(true);
        }

        public ObjDownload getObj() {
            return obj;
        }
    }

    static private class BitmapWorkerLoader {
        private ObjDownload obj;
        private AsyncTask<Void, Void, Bitmap> asyncTask;
        private Context context;

        public BitmapWorkerLoader(Context context, ObjDownload obj) {
            this.obj = obj;
            this.context = context;
        }

        public void execute() {
            asyncTask = new AsyncTask<Void, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(Void... params) {
                    Log.e("", "doInBackground, url = "+obj.url);
                    if (ALREADY_REQUEST_URL_SET.contains(obj.url)) {
                        synchronized (SYNC_WORK_OBJ) {
                            try {
                                SYNC_WORK_OBJ.wait();
                            } catch (InterruptedException e) {
                            }
                        }
                    } else {
                        ALREADY_REQUEST_URL_SET.add(obj.url);
                    }

                    Log.e("", "doInBackground, url 1 = "+obj.url);
                    Bitmap bitmap = null;
                    if (!isCancelled()) {
                        Object object = CacheLib.getInstance().getObjectFromMemCache(obj.url);
                        if (object != null) {
                            bitmap = (Bitmap) object;
                        }
                    }
                    if (bitmap == null && !isCancelled()) {
                        DownloadLibImage downloadLibImage = new DownloadLibImage(obj.url);
                        try {
                            bitmap = downloadLibImage.connectAndParseObj();
                        } catch (IOException e) {
                            Log.e("", "error url = "+obj.url);
                            Log.e("", "e = "+e.getLocalizedMessage());
                            obj.error = e;
                            return null;
                        }
                    }
                    if (!isCancelled() && bitmap != null) {
                        CacheLib.getInstance().addObjectToMemory(obj.url, bitmap);
                        return bitmap;
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    super.onPostExecute(bitmap);
                    ImageView imageView = getAttachedImageView();

                    if(imageView != null && !isCancelled() && bitmap == null){
                        imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_error));
                    }

                    if(imageView != null && isCancelled()){
                        imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_cancel));
                    }

                    if (imageView != null && !isCancelled() && bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    }

                    if(!isCancelled() && obj.downloadCallBack != null){
                        if(bitmap != null) {
                            obj.downloadCallBack.onDownloadCompleted(bitmap);
                        }else{
                            obj.downloadCallBack.onDownloadError(obj.error != null ? obj.error :new NullPointerException("Bitmap null"));
                        }
                    }
                    removeRequest(obj);
                    removeTagImageView(obj);
                    Log.e("", "Finish url = " + obj.url);
                    synchronized (SYNC_WORK_OBJ) {
                        SYNC_WORK_OBJ.notifyAll();
                    }
                }

                @Override
                protected void onCancelled(Bitmap bitmap) {
                    super.onCancelled(bitmap);
                    Log.e("", "Cancel url = " + obj.url);
                    ImageView imageView = getAttachedImageView();
                    if(imageView != null) {
                        imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_cancel));
                    }
                    removeRequest(obj);
                    synchronized (SYNC_WORK_OBJ) {
                        SYNC_WORK_OBJ.notifyAll();
                    }
                }
            };
//            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            //http://stackoverflow.com/questions/9119627/android-sdk-asynctask-doinbackground-not-running-subclass
            AsyncTaskCompat.executeParallel(asyncTask);
        }

        public void cancel(){
            asyncTask.cancel(true);
        }
        public ObjDownload getObj(){
            return obj;
        }

        //for checking again if current bitmap worker task is this
        private ImageView getAttachedImageView() {
            final ImageView imageView = obj.imageView;
            if(imageView != null) {
                final BitmapWorkerLoader bitmapWorkerLoader = (BitmapWorkerLoader) imageView.getTag();

                if (this == bitmapWorkerLoader) {
                    return imageView;
                }
            }
            return null;
        }
    }

    static private void removeRequest(ObjDownload obj){
        ALREADY_REQUEST_URL_SET.remove(obj.url);
    }

    static private void removeTagImageView(ObjDownload obj){
        if(obj.imageView != null && obj.imageView.getTag() != null){
            obj.imageView.setTag(null);
        }
    }

    private static class ObjDownload{
        public String url;
        public DownloadCallBack downloadCallBack;
        public ImageView imageView;
        public Throwable error;
    }


}
