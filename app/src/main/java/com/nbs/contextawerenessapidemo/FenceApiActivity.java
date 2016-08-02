package com.nbs.contextawerenessapidemo;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.DetectedActivityFence;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.LocationFence;
import com.google.android.gms.awareness.fence.TimeFence;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;

import java.util.TimeZone;

public class FenceApiActivity extends BaseActivity {
    private long hourInMillis = 3600000;
    public static String TAG_START_DRIVING = "startDriving";
    public static String TAG_DURING_DRIVING = "drivingNearResto";
    private static final String FENCE_RECEIVER_ACTION = "START_FENCE_RECEIVE";
    private FenceReceiver mFenceReceiver;
    private Intent mIntent = null;
    private PendingIntent mPendingIntent;

    private int FENCE_REQ_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fence_api);

        mFenceReceiver = new FenceReceiver();
        mIntent = new Intent(FENCE_RECEIVER_ACTION);

        mPendingIntent = PendingIntent.getBroadcast(this, FENCE_REQ_CODE, mIntent, 0);
    }

    private void unregisterStartDrivingFence() {
        Awareness.FenceApi.updateFences(
                mGoogleApiClient,
                new FenceUpdateRequest.Builder()
                        .removeFence(TAG_START_DRIVING)
                        .build()).setResultCallback(new ResultCallbacks<Status>() {
            @Override
            public void onSuccess(@NonNull Status status) {
                Log.i(TAG_START_DRIVING, "Fence " + TAG_START_DRIVING + " successfully removed.");
            }

            @Override
            public void onFailure(@NonNull Status status) {
                Log.i(TAG_START_DRIVING, "Fence " + TAG_START_DRIVING + " could NOT be removed.");
            }
        });
    }

    private void unregisterDuringDrivingFence() {
        Awareness.FenceApi.updateFences(
                mGoogleApiClient,
                new FenceUpdateRequest.Builder()
                        .removeFence(TAG_DURING_DRIVING)
                        .build()).setResultCallback(new ResultCallbacks<Status>() {
            @Override
            public void onSuccess(@NonNull Status status) {
                Log.i(TAG_DURING_DRIVING, "Fence " + TAG_DURING_DRIVING + " successfully removed.");
            }

            @Override
            public void onFailure(@NonNull Status status) {
                Log.i(TAG_DURING_DRIVING, "Fence " + TAG_DURING_DRIVING + " could NOT be removed.");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        setFenceConfig();
        registerReceiver(mFenceReceiver, new IntentFilter(FENCE_RECEIVER_ACTION));
    }

    /**
     * Uncomment if you wanna unregister the fences and receivers
    @Override
    protected void onStop() {
        super.onStop();
        unregisterStartDrivingFence();
        unregisterDuringDrivingFence();
        unregisterReceiver(mFenceReceiver);
    }
    **/

    private void setFenceConfig(){
        AwarenessFence startDriving = DetectedActivityFence.starting(DetectedActivityFence.IN_VEHICLE);
        AwarenessFence areaAndResto = LocationFence.in(-6.224653, 106.803857, 1000, 0L);
        AwarenessFence duringDriving = DetectedActivityFence.during(DetectedActivityFence.IN_VEHICLE);
        AwarenessFence openHours = TimeFence.inDailyInterval(TimeZone.getDefault(), 10*hourInMillis, 18*hourInMillis);

        AwarenessFence drivingNearResto = AwarenessFence.and(areaAndResto, duringDriving, openHours);
        FenceUpdateRequest mFenceUpdateRequest = new FenceUpdateRequest.Builder()
                .addFence(TAG_START_DRIVING, startDriving, mPendingIntent)
                .addFence(TAG_DURING_DRIVING, drivingNearResto, mPendingIntent)
                .build();
        Awareness.FenceApi.updateFences(mGoogleApiClient, mFenceUpdateRequest).setResultCallback(new ResultCallbacks<Status>() {
            @Override
            public void onSuccess(@NonNull Status status) {
                Log.d("FenceApiUpdate", "Success to set the fence condition");
                Toast.makeText(FenceApiActivity.this, "Success to set the fence condition", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(@NonNull Status status) {
                Log.d("FenceApiUpdate", "Failed to set the fence condition");
            }
        });
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, FenceApiActivity.class);
        context.startActivity(starter);
    }

}
