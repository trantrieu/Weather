package tretnt.vn.weather.lib;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Apple on 9/28/16.
 */
class DownloadLibImage extends DownloadLibAbs<Bitmap> {
    public DownloadLibImage(String url) {
        super(url);
    }

    @Override
    public Bitmap connectAndParseObj() throws IOException {
        InputStream is = connectTo(url);
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(is);
        }catch (OutOfMemoryError e){
            e.printStackTrace();
        }
        if(bitmap == null){
            CacheLib.getInstance().clearAll();
            bitmap = BitmapFactory.decodeStream(is);
        }
        return bitmap;
    }
}
