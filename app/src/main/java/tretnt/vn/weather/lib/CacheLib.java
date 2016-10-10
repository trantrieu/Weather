package tretnt.vn.weather.lib;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

/**
 * Created by Apple on 9/28/16.
 */
public class CacheLib {
    static public final Object SYNC_REMOVED_OBJ = new Object();
    static private CacheLib instance;
    private LruCache<String, Object> mMemoryCache;
//    private final HashSet<String> lv_item_layout = new HashSet<>();
    private static final Object object = new Object();
    static public CacheLib getInstance(){
        synchronized (object) {
            if (instance == null) {
                instance = new CacheLib();
            }
            return instance;
        }
    }

    private CacheLib(){
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory;

        mMemoryCache = new LruCache<String, Object>(cacheSize) {
            @Override
            protected int sizeOf(String key, Object object) {
                if(object instanceof Bitmap) {
                    int byteCount = ((Bitmap)object).getByteCount() / 1024;
                    return byteCount;
                }
                else if(object instanceof String){
                    String str = (String)object;
                    return (str.getBytes().length / 1024) * 6/5;
                }
                throw new NullPointerException("Object only support with Bitmap and String");
            }

            @Override
            protected void entryRemoved(boolean evicted, String key, Object oldValue, Object newValue) {
                super.entryRemoved(evicted, key, oldValue, newValue);
                Object object = removeObjectFromMemCache(key);
                if(object instanceof Bitmap){
                    ((Bitmap)object).recycle();
                    return;
                }else if(object instanceof String){
                    return;
                }else if(oldValue != null){
                    if(oldValue instanceof Bitmap) {
                        Bitmap b = (Bitmap)oldValue;
                        if(!b.isRecycled()) {
                            b.recycle();
                        }
                        Log.e("", "entryRemoved removed bitmap");
                    }
                }
            }
        };
    }

    public void addObjectToMemory(String key, Object object) {
        if (getObjectFromMemCache(key) == null) {
            mMemoryCache.put(key, object);
        }
    }

    public Object getObjectFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    public Object removeObjectFromMemCache(String key){
        return mMemoryCache.remove(key);
    }

//    public void removeFirstKey(){
//        Iterator<String> iterator = lv_item_layout.iterator();
//        String first = iterator.next();
//        mMemoryCache.remove(first);
//    }

    public void clearAll(){
        mMemoryCache.evictAll();
        Log.e("", "clearAll");
    }
}
