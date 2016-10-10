package tretnt.vn.weather.lib;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;

import tretnt.vn.weather.model.util.Utils;

/**
 * Created by Apple on 9/28/16.
 */
class DownloadLibJson extends DownloadLibAbs<String> {
    public DownloadLibJson(String url) {
        super(url);
    }


    @Override
    public String connectAndParseObj() throws IOException, JSONException {
        InputStream is = connectTo(url);
        String responseStr = Utils.convertStreamToString(is);
        return  responseStr;
//        JSONObject jsonObject = new JSONObject(responseStr);
//        return jsonObject;
    }

}
