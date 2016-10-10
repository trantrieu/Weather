package tretnt.vn.weather.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import tretnt.vn.weather.R;
import tretnt.vn.weather.lib.DownloadLibs;
import tretnt.vn.weather.model.WeatherObj;
import tretnt.vn.weather.model.util.Utils;
import tretnt.vn.weather.presenter.DetailFragmentPresenter;
import tretnt.vn.weather.view.view.InfoView;

/**
 * Created by Apple on 10/7/16.
 */
public class DetailFragment extends BaseFragment implements DetailMVPView{

    private ImageView iconImgView;
    private TextView cityTv;
    private TextView descTv;
    private TextView humidityTv;
    private TextView tempMinTv;
    private TextView tempMaxTv;
    private TextView windTv;
    private InfoView infoView;
    private View detailContentView;

    private DetailFragmentPresenter detailFragmentPresenter;

    public static DetailFragment getInstance() {
        DetailFragment instance = new DetailFragment();
        return instance;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        detailFragmentPresenter = new DetailFragmentPresenter();
        detailFragmentPresenter.attachTo(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        detailFragmentPresenter.detach();
        detailFragmentPresenter = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, null);
        iconImgView = (ImageView) view.findViewById(R.id.fragment_detail_img_icon);
        cityTv = (TextView)view.findViewById(R.id.fragment_detail_tv_city);
        descTv = (TextView)view.findViewById(R.id.fragment_detail_tv_description);
        humidityTv = (TextView)view.findViewById(R.id.fragment_detail_tv_humidity);
        tempMinTv = (TextView)view.findViewById(R.id.fragment_detail_tv_temp_min);
        tempMaxTv = (TextView)view.findViewById(R.id.fragment_detail_tv_temp_max);
        windTv = (TextView)view.findViewById(R.id.fragment_detail_tv_wind);
        infoView = (InfoView)view.findViewById(R.id.fragment_detail_info_view);
        detailContentView = view.findViewById(R.id.fragment_detail_content);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        detailFragmentPresenter.initConnectionAndGetLastLocation();
    }

    @Override
    public void onDestroyView() {
        detailFragmentPresenter.stopConnectionAndStopGetLastLocation();
        super.onDestroyView();
    }

    @Override
    public void showWeatherCity(WeatherObj weatherObj) {
        detailContentView.setVisibility(View.VISIBLE);
        infoView.setVisibility(View.GONE);

        //Picasso.with(getActivity()).load(weatherObj.getWeather().get(0).getIconUrl()).into(iconImgView);
        DownloadLibs.getImageByUrl(getActivity(), weatherObj.getWeather().get(0).getIconUrl(), null, iconImgView);
        cityTv.setText(getString(R.string.city_name, weatherObj.getName()));
        descTv.setText(getString(R.string.weather_description, weatherObj.getWeather().get(0).getDescription()));
        humidityTv.setText(getString(R.string.humidity, weatherObj.getMain().getHumidity() + "%"));
        tempMinTv.setText(getString(R.string.min_temperature, Utils.convertKtoC(weatherObj.getMain().getTempMin()) + "C"));
        tempMaxTv.setText(getString(R.string.max_temperature, Utils.convertKtoC(weatherObj.getMain().getTempMax()) + "C"));
        windTv.setText(getString(R.string.wind_speed, weatherObj.getWind().getSpeed() +" MPs"));
    }

    @Override
    public void showErrorForCity(String error, Throwable e, View.OnClickListener onClickListener) {
        detailContentView.setVisibility(View.GONE);
        infoView.setVisibility(View.VISIBLE);
        infoView.showError(error, e, onClickListener);
    }

    @Override
    public void showLoadingCity() {
        detailContentView.setVisibility(View.GONE);
        infoView.setVisibility(View.VISIBLE);
        infoView.showLoading();
    }

    @Override
    public void showInformation(String message) {
        detailContentView.setVisibility(View.GONE);
        infoView.setVisibility(View.VISIBLE);
        infoView.showMessage(message);
    }

    @Override
    public Context getCContext() {
        return getActivity();
    }

    @Override
    protected String setTagName() {
        return DetailFragment.class.getSimpleName();
    }
}
