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
                Intent openMapIntent = new Intent(context, FenceApiService.class);
                openMapIntent.putExtra(FenceApiService.EXTRA_TITLE, "Open Map");
                openMapIntent.putExtra(FenceApiService.EXTRA_MESSAGE, "Show default user direction");
                openMapIntent.putExtra(FenceApiService.EXTRA_ACTION, FenceApiService.ACTION.OPEN_MAP);
                context.startService(openMapIntent);
            }
        }else if (mFenceState.getFenceKey().equals(FenceApiActivity.TAG_DURING_DRIVING)){
            if (mFenceState.getCurrentState() == FenceState.TRUE){
                //show recommendation
                Intent openMapIntent = new Intent(context, FenceApiService.class);
                openMapIntent.putExtra(FenceApiService.EXTRA_TITLE, "Recommendation");
                openMapIntent.putExtra(FenceApiService.EXTRA_MESSAGE, "Hi, we got 10 preffered resto near to you");
                openMapIntent.putExtra(FenceApiService.EXTRA_ACTION, FenceApiService.ACTION.SHOW_SUGGESTION);
                context.startService(openMapIntent);
            }
        }

    }
}
