package tretnt.vn.weather.model.util;

import android.util.Log;

import java.text.DecimalFormat;

/**
 * Created by Trieu on 7/26/2016.
 */
public class Utils {

    static private final DecimalFormat df2 = new DecimalFormat(".##");

    static public final void print(String title, String message){
        if(Constant.ENABLE_LOG) {
            Log.e(title, message);
        }
    }

    static public final void printException(Throwable e){
        if(Constant.ENABLE_LOG){
            e.printStackTrace();
        }
    }

    //http://programers-guide.blogspot.com/2012/06/java-programs.html
    static public final double convertKtoC(double kelvin){
        double c = kelvin - 273.0;
        c = Double.parseDouble(df2.format(c));
        return c;
    }

    static public String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
