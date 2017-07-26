package com.hackathon.urbanites;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
    //Icon=R.drawable.icon
    //busDetail=Activity name
    public void map_notify(View view, int icon, String contentTitle, String contentText, Class ActivityName) throws ClassNotFoundException {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(icon)
                            .setContentTitle(contentTitle)
                            .setContentText(contentText)
                            .setPriority(Notification.PRIORITY_MAX);
            Intent resultIntent = new Intent(this, ActivityName);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);
            int mNotificationId=001;
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNotificationManager.notify(mNotificationId, mBuilder.build());

    }


}
