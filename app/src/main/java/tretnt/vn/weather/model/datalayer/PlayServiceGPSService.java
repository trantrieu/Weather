package tretnt.vn.weather.model.datalayer;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import tretnt.vn.weather.R;

/**
 * Created by Apple on 10/6/16.
 */
public class PlayServiceGPSService implements ResultCallback<LocationSettingsResult>, LocationListener{
    private LocationRequest locationRequest;

    public enum PlayServiceConnectStatus {
        NONE,
        CONNECTED,
        CONNECT_SUPPENDED,
        CONNECT_FAILED,
        CONNECT_CONNECTING,
    }

    public interface PlayServiceConnectionListener {
        void onConnection(PlayServiceConnectStatus status);
    }

    public interface PlayServiceGetCurrentLocationListener {
        void onSuccessGetCurrentLocation(Address address);
        void onFailGetCurrentLocation(Throwable throwable);
        void onRequestPermission();
    }
    static private final int MAX_TRY_GET_LOCATION_NULL = 3;
    static private final int TIME_TRY_GET_LOCATION = 2000;
    private int countTryGetLocationNull = 0;
    private GoogleApiClient mGoogleApiClient = null;
    static private PlayServiceGPSService instance;
    private Context context;
    static public final int REQUEST_LOCATION_PERMISSION = 9991;
    private PlayServiceGetCurrentLocationListener playServiceGetCurrentLocationOneTime;
    private PlayServiceConnectionListener playServiceConnectionListener;
    static public final int REQUEST_CHECK_GPS_SETTINGS = 100;
    static public final String REQUEST_CHECK_GPS_SETTINGS_OK = "REQUEST_CHECK_GPS_SETTINGS_OK";
    static public final String REQUEST_CHECK_GPS_SETTINGS_CANCEL = "REQUEST_CHECK_GPS_SETTINGS_CANCEL";
    static private final Handler HANDLER = new Handler();
    static private boolean isStopGetLastLocation = false;
    static private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    static public PlayServiceGPSService getInstance() {
        if (instance == null) {
            instance = new PlayServiceGPSService();
        }
        return instance;
    }

    public void initPlayServiceGPS(Context context, PlayServiceConnectionListener playServiceConnectionListener1) {
        this.context = context;
        this.playServiceConnectionListener = playServiceConnectionListener1;
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(@Nullable Bundle bundle) {
                            updatePlayServiceConnectionListener(playServiceConnectionListener, PlayServiceConnectStatus.CONNECTED);
                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            resetGoogleAPIClient();
                            updatePlayServiceConnectionListener(playServiceConnectionListener, PlayServiceConnectStatus.CONNECT_SUPPENDED);
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                            resetGoogleAPIClient();
                            updatePlayServiceConnectionListener(playServiceConnectionListener, PlayServiceConnectStatus.CONNECT_FAILED);
                        }
                    })
                    .addApi(LocationServices.API)
                    .build();
        }
        initLocationRequest();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (playServiceGetCurrentLocationOneTime != null && location != null && !isStopGetLastLocation) {
            updateLocation(location);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void initLocationRequest(){
        if(locationRequest == null) {
            locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(10 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
        }
    }

    public synchronized void getLastKnown(PlayServiceGetCurrentLocationListener playServiceGetCurrentLocation) {
        if (mGoogleApiClient == null) {
            NullPointerException n = new NullPointerException(context.getString(R.string.error_forgot_to_init_connection));
            playServiceGetCurrentLocation.onFailGetCurrentLocation(n);
            return;
        }
        isStopGetLastLocation = false;
        this.playServiceGetCurrentLocationOneTime = playServiceGetCurrentLocation;
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            requestLocation();
        }else{
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity)context,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    || ActivityCompat.shouldShowRequestPermissionRationale((Activity)context,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    ){
                //show dialog cannot work without these permissions
                playServiceGetCurrentLocation.onRequestPermission();
            } else {
                requestLocationPermission();
            }
        }
    }

    public void stopGetLastKnown(){
        playServiceGetCurrentLocationOneTime = null;
        isStopGetLastLocation = true;
    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                // NO need to show the dialog;
                Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                updateLocationToAddressAndCallback(location);
                break;

            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                try {
                    status.startResolutionForResult((Activity)context, REQUEST_CHECK_GPS_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
                break;

            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                // Location settings are unavailable so not possible to show any dialog now
                break;
        }
    }

    private void updateLocationToAddressAndCallback(final Location location){
        if(playServiceGetCurrentLocationOneTime != null && location != null) {
            updateLocation(location);
        }else if(location == null){
            if(countTryGetLocationNull < MAX_TRY_GET_LOCATION_NULL) {
                HANDLER.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(!isStopGetLastLocation) {
                            requestLocation();
                        }
                    }
                }, TIME_TRY_GET_LOCATION);
                countTryGetLocationNull ++;
            }else {
                countTryGetLocationNull = 0;
                //fixed for some devices not query
                LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            }
        }
    }

    private void updateLocation(final Location location){
        isStopGetLastLocation = true;
        countTryGetLocationNull = 0;
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                final Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocation(lat, lng, 1);
                } catch (IOException | IllegalArgumentException e) {
                    // Catch network or other I/O problems.
                    e.printStackTrace();
                    if(context != null) {
                        ((Activity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (playServiceGetCurrentLocationOneTime != null) {
                                    playServiceGetCurrentLocationOneTime.onFailGetCurrentLocation(e);
                                }
                            }
                        });
                    }
                    return;
                }
                if(addresses != null){
                    final Address address = addresses.get(0);
                    if(context != null) {
                        ((Activity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (playServiceGetCurrentLocationOneTime != null) {
                                    playServiceGetCurrentLocationOneTime.onSuccessGetCurrentLocation(address);
                                }
                            }
                        });
                    }
                }

            }
        });
    }

    private void requestLocation(){
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(this);
    }

    public void requestLocationPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ((Activity) context).requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }

    public void onStartConnection() {
        if(mGoogleApiClient == null){
            throw new NullPointerException(context.getString(R.string.error_forgot_to_init_connection));
        }
        mGoogleApiClient.connect();
    }

    public void onStopConnection() {
        if(mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        playServiceConnectionListener = null;
    }

    public void resetGoogleAPIClient(){
        mGoogleApiClient = null;
    }

    private void updatePlayServiceConnectionListener(PlayServiceConnectionListener playServiceConnectionListener, PlayServiceConnectStatus playServiceConnectStatus){
        if(playServiceConnectionListener != null){
            playServiceConnectionListener.onConnection(playServiceConnectStatus);
        }
    }

}
