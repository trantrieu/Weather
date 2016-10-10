package tretnt.vn.weather.model.datalayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tretnt.vn.weather.model.Clouds;
import tretnt.vn.weather.model.Coord;
import tretnt.vn.weather.model.Main;
import tretnt.vn.weather.model.Rain;
import tretnt.vn.weather.model.Snow;
import tretnt.vn.weather.model.Sys;
import tretnt.vn.weather.model.Weather;
import tretnt.vn.weather.model.WeatherObj;
import tretnt.vn.weather.model.Wind;

/**
 * Created by Apple on 10/9/16.
 */
public class DataParser {

    static public WeatherObj parseFromString(String str) throws JSONException{

        JSONObject jsonObject = new JSONObject(str);
        WeatherObj weatherObj = new WeatherObj();

        //Coord
        JSONObject jsonObjectCoord = jsonObject.getJSONObject("coord");
        double lat = jsonObjectCoord.getDouble("lat");
        double lon = jsonObjectCoord.getDouble("lon");
        Coord coord = new Coord();
        coord.setLat(lat);
        coord.setLon(lon);
        weatherObj.setCoord(coord);

        //weather
        JSONArray jsonObjectWeathers = jsonObject.getJSONArray("weather");
        List<Weather> weatherList = new ArrayList<>();
        for(int i = 0 ; i < jsonObjectWeathers.length() ; i++){
            JSONObject jsonObjectWeather = jsonObjectWeathers.getJSONObject(i);
            Weather weather = new Weather();
            double id = jsonObjectWeather.getDouble("id");
            String main = jsonObjectWeather.getString("main");
            String description = jsonObjectWeather.getString("description");
            String icon = jsonObjectWeather.getString("icon");
            weather.setDescription(description);
            weather.setIcon(icon);
            weather.setId(id);
            weather.setMain(main);
            weatherList.add(weather);
        }
        weatherObj.setWeather(weatherList);

        //base
        weatherObj.setBase(jsonObject.getString("base"));

        //main
        JSONObject jsonObjectMain = jsonObject.getJSONObject("main");
        double temp = jsonObjectMain.getDouble("temp");
        double pressure = jsonObjectMain.getDouble("pressure");
        double humidity = jsonObjectMain.getDouble("humidity");
        double temp_min = jsonObjectMain.getDouble("temp_min");
        double temp_max = jsonObjectMain.getDouble("temp_max");
        Main main = new Main();
        main.setHumidity(humidity);
        main.setPressure(pressure);
        main.setTemp(temp);
        main.setTempMax(temp_max);
        main.setTempMin(temp_min);
        weatherObj.setMain(main);

        //wind //check existing
        if(jsonObject.has("wind")) {
            JSONObject jsonObjectWind = jsonObject.getJSONObject("wind");
            double speed = jsonObjectWind.getDouble("speed");
            double deg = jsonObjectWind.getDouble("deg");
            Wind wind = new Wind();
            wind.setDeg(deg);
            wind.setSpeed(speed);
            weatherObj.setWind(wind);
        }

        //clouds
        if(jsonObject.has("clouds")) {
            JSONObject jsonObjectClouds = jsonObject.getJSONObject("clouds");
            double all = jsonObjectClouds.getDouble("all");
            Clouds clouds = new Clouds();
            clouds.setAll(all);
            weatherObj.setClouds(clouds);
        }

        //rain
        if(jsonObject.has("rain")) {
            JSONObject jsonObjectRain = jsonObject.getJSONObject("rain");
            double _3h = jsonObjectRain.getDouble("3h");
            Rain rain = new Rain();
            rain.set_3h(_3h);
            weatherObj.setRain(rain);
        }

        //snow
        if(jsonObject.has("snow")) {
            JSONObject jsonObjectSnow = jsonObject.getJSONObject("snow");
            double _3h = jsonObjectSnow.getDouble("3h");
            Snow snow = new Snow();
            snow.set_3h(_3h);
            weatherObj.setSnow(snow);
        }

        //dt
        weatherObj.setDt(jsonObject.getDouble("dt"));

        //sys
        JSONObject jsonObjectSys = jsonObject.getJSONObject("sys");
        double type = 0;
        if(jsonObjectSys.has("type")) {
            type  = jsonObjectSys.getDouble("type");
        }

        double id = 0;
        if(jsonObjectSys.has("id")) {
            jsonObjectSys.getDouble("id");
        }
        double message = jsonObjectSys.getDouble("message");
        String country = jsonObjectSys.getString("country");
        double sunrise = jsonObjectSys.getDouble("sunrise");
        double sunset = jsonObjectSys.getDouble("sunset");
        Sys sys = new Sys();
        sys.setCountry(country);
        sys.setId(id);
        sys.setMessage(message);
        sys.setSunrise(sunrise);
        sys.setSunset(sunset);
        sys.setType(type);
        weatherObj.setSys(sys);

        //id
        id = jsonObject.getDouble("id");
        weatherObj.setId(id);

        //name
        weatherObj.setName(jsonObject.getString("name"));

        //cod
        weatherObj.setCod(jsonObject.getDouble("cod"));

        return weatherObj;
    }

}
