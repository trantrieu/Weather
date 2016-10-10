package tretnt.vn.weather;

import android.app.Application;

/**
 * Created by Trieu on 7/26/2016.
 */
public class WeatherApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initializeLeakDetection();

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    private void initializeLeakDetection() {
        if (BuildConfig.DEBUG) {

        }
    }
}
