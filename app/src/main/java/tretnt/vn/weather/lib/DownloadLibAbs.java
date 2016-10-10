package tretnt.vn.weather.lib;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Apple on 9/28/16.
 */
abstract class DownloadLibAbs<T>  {

    protected String url;
    public DownloadLibAbs(String url){
        this.url = url;
    }

    protected InputStream connectTo(String urlStr) throws IOException {

        URL url = new URL(urlStr);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setConnectTimeout(60 * 1000);
        urlConnection.setReadTimeout(60 * 1000);
        urlConnection.setDoInput(true);
        //http://stackoverflow.com/questions/9365829/filenotfoundexception-for-httpurlconnection-in-ice-cream-sandwich
        //urlConnection.setDoOutput(true);
        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
        return in;
    }

    public abstract T connectAndParseObj() throws IOException, JSONException;


}
