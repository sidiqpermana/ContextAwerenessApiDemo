package com.nbs.contextawerenessapidemo;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;


public class FenceApiService extends IntentService {

    public static String EXTRA_TITLE = "extra_title";
    public static String EXTRA_MESSAGE = "extra_message";
    public static String EXTRA_ACTION = "extra_action";

    public enum ACTION {
        OPEN_MAP, SHOW_SUGGESTION
    }

    public FenceApiService() {
        super("FenceApiService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String title = null, content = null;
        ACTION mAction = null;
        if (intent != null) {
            title = intent.getStringExtra(EXTRA_TITLE);
            content = intent.getStringExtra(EXTRA_MESSAGE);
            mAction = (ACTION) intent.getSerializableExtra(EXTRA_ACTION);

            if (mAction == ACTION.OPEN_MAP){
                showMap();
            }else if(mAction == ACTION.SHOW_SUGGESTION){
                Intent mIntent = new Intent(getApplicationContext(), MainActivity.class);
                createNotification(title, content, PendingIntent.getActivity(getApplicationContext(), 101, mIntent, PendingIntent.FLAG_ONE_SHOT));
            }
        }
    }

    private void showMap(){
        double latitude1 = -6.348717, longitude1 = 106.753123;
        double latitude2 = -6.245990, longitude2 =  106.886520;
        String uri = "http://maps.google.com/maps?f=d&hl=en&saddr="+latitude1+","+longitude1+"&daddr="+latitude2+","+longitude2;
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(Intent.createChooser(intent, "Select an application"));
    }

    public void createNotification(String title, String content, @Nullable PendingIntent pendingIntent) {
        Uri soundUri = Uri.parse("android.resource://"
                + getPackageName() + "/" + R.raw.granules);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setAutoCancel(true);
        builder.setSound(soundUri);
        builder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
        builder.setContentTitle(title);
        builder.setContentText(content);
        if (pendingIntent != null) {
            builder.setContentIntent(pendingIntent);
        }
        builder.setSmallIcon(R.mipmap.ic_launcher);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(200, builder.build());
    }
}
