package com.nbs.contextawerenessapidemo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.DetectedActivityResult;
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.awareness.snapshot.PlacesResult;
import com.google.android.gms.awareness.snapshot.WeatherResult;
import com.google.android.gms.awareness.state.Weather;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.places.PlaceLikelihood;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class SnapshotApiActivity extends BaseActivity implements
        EasyPermissions.PermissionCallbacks{
    private TextView tvDetectActivity, tvDetectUserLocation, tvDetectUserPlace, tvDetectWeather;
    private String TAG_ACTIVITY = "DetectActivity";
    private String TAG_LOCATION = "DetectLocation";
    private String TAG_PLACES = "DetectPlaces";
    private String TAG_WEATHER = "DetectWeather";
    private static final int RC_LOCATION = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snapshot_api);

        tvDetectActivity = (TextView)findViewById(R.id.tv_activity);
        tvDetectUserLocation = (TextView)findViewById(R.id.tv_user_location);
        tvDetectUserPlace = (TextView)findViewById(R.id.tv_user_place);
        tvDetectWeather = (TextView)findViewById(R.id.tv_weather);

        getCurrentUserActivity();

        askLocationPermission();
    }

    private void getCurrentUserActivity(){
        Awareness.SnapshotApi.getDetectedActivity(mGoogleApiClient)
                .setResultCallback(new ResultCallback<DetectedActivityResult>() {
                    @Override
                    public void onResult(@NonNull DetectedActivityResult detectedActivityResult) {
                        if (!detectedActivityResult.getStatus().isSuccess()){
                            Log.d(TAG_ACTIVITY, "Could not get the current activity");
                            return;
                        }

                        ActivityRecognitionResult mActivityRecognitionResult =
                                detectedActivityResult.getActivityRecognitionResult();
                        DetectedActivity probableActivity = mActivityRecognitionResult.getMostProbableActivity();
                        String currentUserActivity = "Possible activity : "+getActivityType(probableActivity);
                        tvDetectActivity.setText(Html.fromHtml(currentUserActivity));
                    }
                });
    }

    private void getCurrentUserLocation(){
        Awareness.SnapshotApi.getLocation(mGoogleApiClient)
                .setResultCallback(new ResultCallback<LocationResult>() {
                    @Override
                    public void onResult(@NonNull LocationResult locationResult) {
                        if (!locationResult.getStatus().isSuccess()) {
                            Log.e(TAG_LOCATION, "Could not get location.");
                            return;
                        }
                        Location location = locationResult.getLocation();
                        String userLocation = "User Location : "+location.getLatitude()+" "+location.getLongitude()+" " +
                                "[With accuracy : "+(int)location.getAccuracy()+" m]";
                        tvDetectUserLocation.setText(userLocation);
                    }
                });
    }

    private void getProbleUserPlaces(){
        Awareness.SnapshotApi.getPlaces(mGoogleApiClient)
                .setResultCallback(new ResultCallback<PlacesResult>() {
                    @Override
                    public void onResult(@NonNull PlacesResult placesResult) {
                        if (!placesResult.getStatus().isSuccess()) {
                            Log.e(TAG_PLACES, "Could not get places.");
                            return;
                        }
                        List<PlaceLikelihood> placeLikelihoodList = placesResult.getPlaceLikelihoods();
                        // Show the top 5 possible location results.
                        String places = "";
                        if (placeLikelihoodList != null) {
                            for (int i = 0; i < 5 && i < placeLikelihoodList.size(); i++) {
                                PlaceLikelihood p = placeLikelihoodList.get(i);
                                places += p.getPlace().getName().toString() + ", likelihood: " + p.getLikelihood()+" ";
                            }
                            String userPlaces = "Possible places : "+places;
                            tvDetectUserPlace.setText(userPlaces);
                        } else {
                            Log.e(TAG_PLACES, "Place is null.");
                        }
                    }
                });
    }

    private void getCurrentWeather(){
        Awareness.SnapshotApi.getWeather(mGoogleApiClient)
                .setResultCallback(new ResultCallback<WeatherResult>() {
                    @Override
                    public void onResult(@NonNull WeatherResult weatherResult) {
                        if (!weatherResult.getStatus().isSuccess()) {
                            Log.e(TAG_WEATHER, "Could not get weather.");
                            return;
                        }
                        Weather weather = weatherResult.getWeather();
                        String weatherDetails = "Condition : "+getWeatherType(weather)+"\n"+
                                                "Temprature : "+weather.getTemperature(Weather.CELSIUS)+"\n"+
                                                "Person feel temprature : "+weather.getFeelsLikeTemperature(Weather.CELSIUS)+"\n"+
                                                "Humidity : "+weather.getHumidity();
                        tvDetectWeather.setText(Html.fromHtml(weatherDetails));
                    }
                });
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, SnapshotApiActivity.class);
        context.startActivity(starter);
    }

    @AfterPermissionGranted(RC_LOCATION)
    public void askLocationPermission() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            getCurrentUserLocation();
            getProbleUserPlaces();
            getCurrentWeather();
        } else {
            // Ask for both permissions
            EasyPermissions.requestPermissions(this, getString(R.string.ask_location),
                    RC_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.d(TAG_ACTIVITY, "onPermissionsGranted:" + requestCode + ":" + perms.size());
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        EasyPermissions.checkDeniedPermissionsNeverAskAgain(this,
                getString(R.string.ask_location),
                R.string.setting, R.string.cancel, null, perms);
    }
}
