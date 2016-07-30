package com.nbs.contextawerenessapidemo;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.DetectedActivityFence;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.LocationFence;
import com.google.android.gms.awareness.fence.TimeFence;

import java.util.TimeZone;

public class FenceApiActivity extends BaseActivity {
    private long hourInMillis = 3600000;
    public static String TAG_START_DRIVING = "startDriving";
    public static String TAG_DURING_DRIVING = "drivingNearResto";
    private static final String FENCE_RECEIVER_ACTION = "START_FENCE_RECEIVE";
    private FenceReceiver mFenceReceiver;
    private Intent mIntent = null;
    private PendingIntent mPendingIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fence_api);

        mFenceReceiver = new FenceReceiver();
        mIntent = new Intent(FENCE_RECEIVER_ACTION);

        mPendingIntent = PendingIntent.getBroadcast(this, 123, mIntent, 0);
    }

    private void registerFences() {
        setFenceConfig();
    }

    private void unregisterFence() {
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerFences();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterFence();
        unregisterReceiver(mFenceReceiver);
    }

    private void setFenceConfig(){
        AwarenessFence startDriving = DetectedActivityFence.starting(DetectedActivityFence.IN_VEHICLE);
        AwarenessFence areaAndResto = LocationFence.in(-7.794002, 110.365611, 1000, 0L);
        AwarenessFence duringDriving = DetectedActivityFence.during(DetectedActivityFence.IN_VEHICLE);
        AwarenessFence openHours = TimeFence.inDailyInterval(TimeZone.getDefault(), 10*hourInMillis, 18*hourInMillis);

        AwarenessFence drivingNearResto = AwarenessFence.and(areaAndResto, duringDriving, openHours);
        FenceUpdateRequest mFenceUpdateRequest = new FenceUpdateRequest.Builder()
                .addFence(TAG_START_DRIVING, startDriving, mPendingIntent)
                .addFence(TAG_DURING_DRIVING, drivingNearResto, mPendingIntent)
                .build();
        Awareness.FenceApi.updateFences(mGoogleApiClient, mFenceUpdateRequest);
    }


}
