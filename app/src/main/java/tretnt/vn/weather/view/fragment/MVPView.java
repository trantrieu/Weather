package tretnt.vn.weather.view.fragment;

import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by Trieu on 7/27/2016.
 */
public interface MVPView {
    Context getCContext();
    void showDialog(String title, String message, DialogInterface.OnClickListener onClickListener);
    void showToast(String message);
    void showDialog(int title, int message, DialogInterface.OnClickListener onClickListener);
    void showToast(int message);
}
