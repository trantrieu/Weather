package tretnt.vn.weather.view.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import tretnt.vn.weather.R;

/**
 * Created by Apple on 10/6/16.
 */
public class DialogCreator {

    static public void showDialogMessage(final Context context, final String title, final String message, final DialogInterface.OnClickListener onOkClickListener){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(title);
                builder.setMessage(message);
                builder.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(onOkClickListener != null){
                            onOkClickListener.onClick(dialog, which);
                        }
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
        });
    }

    static public void showDialogMessage(final Context context, final int title, final int message, final DialogInterface.OnClickListener onOkClickListener){
        String titleStr = context.getString(title);
        String messageStr = context.getString(message);
        showDialogMessage(context, titleStr, messageStr, onOkClickListener);
    }

    static public void showDialogMessage(final Context context, final String message, final DialogInterface.OnClickListener onOkClickListener){
        showDialogMessage(context, null, message, onOkClickListener);
    }

    static public void showToast(final Context context, final String message){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    static public void showToast(Context context, int message){
        showToast(context, context.getString(message));
    }
}
