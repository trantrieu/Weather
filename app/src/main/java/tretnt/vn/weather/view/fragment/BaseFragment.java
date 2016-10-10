package tretnt.vn.weather.view.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import tretnt.vn.weather.view.dialog.DialogCreator;

/**
 * Created by Trieu on 7/26/2016.
 */
public abstract class BaseFragment extends Fragment {

    private String tagName;

    protected BaseFragment(){
        tagName = setTagName();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public String getTagName(){
        return tagName;
    }

    protected abstract String setTagName();

    @Override public void onDestroy() {
        super.onDestroy();
//        RefWatcher refWatcher = WeatherApp.refWatcher;
//        refWatcher.watch(this);
    }

    public void showDialog(String title, String message, DialogInterface.OnClickListener onClickListener){
        DialogCreator.showDialogMessage(getActivity(), title, message, onClickListener);
    }

    public void showToast(String message){
        DialogCreator.showToast(getActivity(), message);
    }

    public void showDialog(int title, int message, DialogInterface.OnClickListener onClickListener){
        DialogCreator.showDialogMessage(getActivity(), title, message, onClickListener);
    }

    public void showToast(int message){
        DialogCreator.showToast(getActivity(), message);
    }
}
