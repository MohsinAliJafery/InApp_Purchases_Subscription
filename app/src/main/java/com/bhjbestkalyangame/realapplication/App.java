package com.bhjbestkalyangame.realapplication;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {

    public static final String CHANNEL_ONE_ID = "Contents_Updated";

    @Override
    public void onCreate() {
        super.onCreate();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ONE_ID, "Updated", NotificationManager.IMPORTANCE_HIGH);

            NotificationManager mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            mManager.createNotificationChannel(mChannel);

        }
    }
}
