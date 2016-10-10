package tretnt.vn.weather.model.datalayer;

import android.app.Activity;
import android.content.Context;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import tretnt.vn.weather.model.WeatherObj;
import tretnt.vn.weather.model.util.Constant;
import tretnt.vn.weather.model.util.Utils;

/**
 * Created by Trieu on 7/31/2016.
 */
public class WeatherService {
    static private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    static public ExecutorFuture getWeather(Context context, String countryCode, RequestWeatherCallBack requestWeatherCallBac){
        ExecutorFuture executorFuture = new ExecutorFuture();
        executorFuture.execute(context, countryCode, requestWeatherCallBac);
        return executorFuture;
    }

    static public class ExecutorFuture{
        private Future future = null;
        public void execute(final Context context, final String countryCode, final RequestWeatherCallBack requestWeatherCallBack){
            future =  executorService.submit(new Runnable() {
                @Override
                public void run() {
                    if(!future.isCancelled()){
                        try {
                            String urlStr = Constant.BASE_URL;
                            urlStr += "/data/2.5/weather?";
                            urlStr += "q="+countryCode;
                            urlStr += "&appid="+Constant.API_KEY;
                            URL url = new URL(urlStr);

                            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                            urlConnection.setConnectTimeout(60 * 1000);
                            urlConnection.setReadTimeout(60 * 1000);
                            urlConnection.setRequestMethod("GET");
                            urlConnection.setDoInput(true);
                            urlConnection.setDoOutput(true);

                            if (!future.isCancelled()) {
                                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                                String json = Utils.convertStreamToString(in);
                                final WeatherObj weatherObj = DataParser.parseFromString(json);
                                if(!future.isCancelled()) {
                                    if(context != null) {
                                        ((Activity)context).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                requestWeatherCallBack.onSuccess(weatherObj);
                                            }
                                        });
                                    }
                                }
                            }
                        }catch (IOException | JSONException e){
                            if(!future.isCancelled()) {
                                if(context != null) {
                                    ((Activity)context).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            requestWeatherCallBack.onFail(e);
                                        }
                                    });
                                }
                            }
                        }
                    }
                }
            });
        }
        public void stop(){
            if(future != null){
                future.cancel(true);
            }
            future = null;
        }
    }


    public interface RequestWeatherCallBack{
        void onSuccess(WeatherObj weatherObj);
        void onFail(Exception e);
    }
}
