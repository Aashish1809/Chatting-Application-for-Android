package Notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

public class OreoNotify extends ContextWrapper {
    public OreoNotify(Context base) {
        super(base);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
            create();
    }
    @TargetApi(Build.VERSION_CODES.O)
    private void create() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME
        ,NotificationManager.IMPORTANCE_HIGH);
        channel.enableLights(false);
        channel.enableVibration(true);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getNotificationManager().createNotificationChannel(channel);
    }

    public  NotificationManager getNotificationManager(){
        if(notificationManager==null){
           notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }
        return notificationManager;
    }

    private static final String CHANNEL_ID = "com.example.chat";
    private static final String CHANNEL_NAME = "channel";
    private NotificationManager notificationManager;

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getNotify(String title, String body, PendingIntent pendingIntent, Uri sound, String icon){
        return new Notification.Builder(getApplicationContext(),CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSound(sound)
                .setSmallIcon(Integer.parseInt(icon))
                .setAutoCancel(true);
    }

}
