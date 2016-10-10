package tretnt.vn.weather.view.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import tretnt.vn.weather.R;
import tretnt.vn.weather.model.datalayer.OttoManager;
import tretnt.vn.weather.model.datalayer.PlayServiceGPSService;
import tretnt.vn.weather.model.util.Utils;
import tretnt.vn.weather.view.fragment.DetailFragment;

/**
 * Created by Apple on 10/7/16.
 */
public class DetailActivity extends BaseActivity {
    static public final String PERMISSION_GRANT = "PERMISSION_GRANT";
    static public final String PERMISSION_NOT_GRANT = "PERMISSION_NOT_GRANT";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        //need to check again
        clearAllBackStack();
        DetailFragment detailFragment = DetailFragment.getInstance();
        replaceFragment(R.id.activity_detail_root, detailFragment);
        Utils.print("DetailActivity", "onCreate, total fragments = "+(getSupportFragmentManager().getFragments()!=null?getSupportFragmentManager().getFragments().size():"null"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //need to check again
        clearAllBackStack();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PlayServiceGPSService.REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length == 2 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    ) {
                // Permission request was accepted
                OttoManager.pushMessage(PERMISSION_GRANT);
            } else {
                // Permission request was denied.
                OttoManager.pushMessage(PERMISSION_NOT_GRANT);
            }
        }
    }

    //http://stackoverflow.com/questions/18755550/fragment-pressing-back-button
    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() == 1
                ){
            //need to check again
            clearAllBackStack();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PlayServiceGPSService.REQUEST_CHECK_GPS_SETTINGS){
            if(resultCode == RESULT_OK) {
                OttoManager.pushMessage(PlayServiceGPSService.REQUEST_CHECK_GPS_SETTINGS_OK);
            }else if(resultCode == RESULT_CANCELED){
                OttoManager.pushMessage(PlayServiceGPSService.REQUEST_CHECK_GPS_SETTINGS_CANCEL);
            }
        }
    }
}
