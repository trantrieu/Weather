package tretnt.vn.weather.view.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import tretnt.vn.weather.R;
import tretnt.vn.weather.model.util.Utils;

/**
 * Created by Trieu on 7/28/2016.
 */
public class InfoView extends FrameLayout {

    private LayoutInflater layoutInflater;

    private ProgressBar loadingView;
    private TextView messageView;
    private View errorView;

    public InfoView(Context context) {
        super(context);
        init();
    }

    public InfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public InfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public InfoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(){
        layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.information_layout, this, true);

        //find children
        loadingView = (ProgressBar)view.findViewById(R.id.information_layout_loading_bar);
        messageView = (TextView)view.findViewById(R.id.information_layout_tv_msg);
        errorView = view.findViewById(R.id.information_layout_error_layout);

        showLoading();
    }

    public void showLoading(){
        loadingView.setVisibility(View.VISIBLE);
        errorView.setVisibility(View.GONE);
        messageView.setVisibility(View.GONE);
    }

    public void showMessage(String message){
        loadingView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        messageView.setVisibility(View.VISIBLE);

        messageView.setText(message);
    }

    public void showError(String error, Throwable e, OnClickListener onClickListener){
        loadingView.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
        messageView.setVisibility(View.GONE);

        String txtError = (e != null ? e.getMessage() : "");
        if(error != null){
            txtError = error;
        }
        TextView.class.cast(errorView.findViewById(R.id.information_layout_tv_error)).setText(txtError);
        Button.class.cast(errorView.findViewById(R.id.information_layout_button_retry)).setOnClickListener(onClickListener);
    }
}
