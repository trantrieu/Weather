package tretnt.vn.weather.presenter;

import android.location.Address;
import android.view.View;

import tretnt.vn.weather.R;
import tretnt.vn.weather.model.WeatherObj;
import tretnt.vn.weather.model.datalayer.OttoManager;
import tretnt.vn.weather.model.datalayer.PlayServiceGPSService;
import tretnt.vn.weather.model.datalayer.WeatherService;
import tretnt.vn.weather.view.activity.DetailActivity;
import tretnt.vn.weather.view.fragment.DetailMVPView;

/**
 * Created by Apple on 10/7/16.
 */
public class DetailFragmentPresenter implements OttoManager.OttoInterface, Presenter<DetailMVPView>, PlayServiceGPSService.PlayServiceConnectionListener, PlayServiceGPSService.PlayServiceGetCurrentLocationListener {

    private DetailMVPView mvpView;
    private WeatherService.ExecutorFuture executorFuture;
    @Override
    public void attachTo(DetailMVPView mvpView) {
        this.mvpView = mvpView;
        OttoManager.register(this);
    }

    @Override
    public void detach() {
        mvpView = null;
        if (executorFuture != null){
            executorFuture.stop();
        }
        PlayServiceGPSService.getInstance().onStopConnection();
        PlayServiceGPSService.getInstance().stopGetLastKnown();
        OttoManager.unRegister(this);
    }

    public void initConnectionAndGetLastLocation(){
        mvpView.showLoadingCity();
        PlayServiceGPSService.getInstance().initPlayServiceGPS(this.mvpView.getCContext(), this);
        PlayServiceGPSService.getInstance().onStartConnection();
    }

    public void stopConnectionAndStopGetLastLocation(){
        PlayServiceGPSService.getInstance().onStopConnection();
        PlayServiceGPSService.getInstance().stopGetLastKnown();
        PlayServiceGPSService.getInstance().resetGoogleAPIClient();
    }

    @Override
    public void onConnection(PlayServiceGPSService.PlayServiceConnectStatus status) {
        if(status == PlayServiceGPSService.PlayServiceConnectStatus.CONNECTED){
            PlayServiceGPSService.getInstance().getLastKnown(this);
            mvpView.showToast(R.string.success_connect_gps);
        }else{
            //mvpView.showDialog(R.string.error, R.string.error_cannot_connect_gps, null);
            mvpView.showErrorForCity(mvpView.getCContext().getString(R.string.error), new NullPointerException(mvpView.getCContext().getString(R.string.error_cannot_connect_gps)), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopConnectionAndStopGetLastLocation();
                    initConnectionAndGetLastLocation();
                }
            });
        }
    }

    @Override
    public void onSuccessGetCurrentLocation(Address address) {
        String countryCode = address.getCountryCode();
        fetchWeather(countryCode);

        //fixed for some device
        PlayServiceGPSService.getInstance().stopGetLastKnown();
    }

    private void fetchWeather(final String countryCode){
        executorFuture = WeatherService.getWeather(mvpView.getCContext(), countryCode, new WeatherService.RequestWeatherCallBack() {
            @Override
            public void onSuccess(WeatherObj weatherObj) {
                mvpView.showWeatherCity(weatherObj);
            }

            @Override
            public void onFail(Exception e) {
                mvpView.showErrorForCity(e.getLocalizedMessage(), e, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stopConnectionAndStopGetLastLocation();
                        initConnectionAndGetLastLocation();
                    }
                });
            }
        });
    }

    @Override
    public void onFailGetCurrentLocation(Throwable throwable) {
        mvpView.showErrorForCity(throwable.getLocalizedMessage(), throwable, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopConnectionAndStopGetLastLocation();
                initConnectionAndGetLastLocation();
            }
        });
    }

    @Override
    public void onRequestPermission() {

        mvpView.showErrorForCity(mvpView.getCContext().getString(R.string.warning_permission_required), null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayServiceGPSService.getInstance().requestLocationPermission();
            }
        });

    }

    @Override
    public void onReceiveObject(Object object) {
        if(object instanceof String){
            String command = (String)object;
            if(command.equals(DetailActivity.PERMISSION_GRANT)){
                PlayServiceGPSService.getInstance().getLastKnown(this);
            }else if (command.equals(DetailActivity.PERMISSION_NOT_GRANT)){
                onRequestPermission();
            }else if(command.equals(PlayServiceGPSService.REQUEST_CHECK_GPS_SETTINGS_OK)){
                PlayServiceGPSService.getInstance().getLastKnown(this);
            }else if(command.equals(PlayServiceGPSService.REQUEST_CHECK_GPS_SETTINGS_CANCEL)){
                String str = mvpView.getCContext().getString(R.string.warning_enable_gps);
                mvpView.showErrorForCity(str, new IllegalStateException(str), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stopConnectionAndStopGetLastLocation();
                        initConnectionAndGetLastLocation();
                    }
                });
            }
        }
    }
}
