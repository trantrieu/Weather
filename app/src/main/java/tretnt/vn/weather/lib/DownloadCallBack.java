package tretnt.vn.weather.lib;

/**
 * Created by Apple on 9/28/16.
 */
public interface DownloadCallBack<T> {
    void onDownloadCompleted(T t);
    void onDownloadError(Throwable t);
}
