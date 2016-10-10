package tretnt.vn.weather.view.fragment;

import android.view.View;

import tretnt.vn.weather.model.WeatherObj;

/**
 * Created by Apple on 10/7/16.
 */
public interface DetailMVPView extends MVPView {

    void showWeatherCity(WeatherObj weatherObj);
    void showErrorForCity(String error, Throwable e, View.OnClickListener onClickListener);
    void showLoadingCity();
    void showInformation(String message);

}
