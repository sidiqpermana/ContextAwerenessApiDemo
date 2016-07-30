package com.nbs.contextawerenessapidemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.awareness.fence.FenceState;

/**
 * Created by Sidiq on 30/07/2016.
 */
public class FenceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        FenceState mFenceState = FenceState.extract(intent);
        if (mFenceState.getFenceKey().equals(FenceApiActivity.TAG_START_DRIVING)){
            if (mFenceState.getCurrentState() == FenceState.TRUE){
                //Open Map
            }
        }else if (mFenceState.getFenceKey().equals(FenceApiActivity.TAG_DURING_DRIVING)){
            if (mFenceState.getCurrentState() == FenceState.TRUE){
                //show reminder
            }
        }

    }
}
